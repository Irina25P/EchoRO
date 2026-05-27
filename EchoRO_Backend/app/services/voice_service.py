import os
import re
import uuid

import soundfile as sf
import torch
from fastapi import HTTPException
from num2words import num2words

from app.repositories import history_repository


def _normalize_text(text: str) -> str:
    def replace_number(match):
        return num2words(int(match.group(0)), lang="ro")

    return re.sub(r"\b\d+\b", replace_number, text)


def generate(
    user_id: int,
    text: str,
    description: str,
    model_type: str,
    models: dict,
    tokenizers: dict,
    device: str,
) -> dict:
    if model_type == "Large" and user_id == 0:
        raise HTTPException(
            status_code=403,
            detail="Trebuie să fii logat pentru a utiliza modelul Large.",
        )

    if model_type not in models or models[model_type] is None:
        raise HTTPException(
            status_code=500,
            detail=f"Modelul AI '{model_type}' nu este disponibil pe server.",
        )

    active_model = models[model_type]
    active_tokenizer = tokenizers[model_type]

    try:
        text_normalized = _normalize_text(text)

        input_ids = active_tokenizer(description, return_tensors="pt").input_ids.to(device)
        prompt_input_ids = active_tokenizer(text_normalized, return_tensors="pt").input_ids.to(device)

        with torch.no_grad():
            generation = active_model.generate(
                input_ids=input_ids, prompt_input_ids=prompt_input_ids
            )

        audio_arr = generation.cpu().numpy().squeeze().astype("float32")

        file_name = f"voice_{uuid.uuid4()}.wav"
        file_path = os.path.join("generated_audio", file_name)
        sf.write(file_path, audio_arr, active_model.config.sampling_rate)

        history_repository.save(user_id, text, file_name, model_type)

        return {
            "status": "success",
            "audio_url": f"/static/{file_name}",
            "text_used": text_normalized,
        }

    except HTTPException:
        raise
    except Exception as e:
        import traceback
        traceback.print_exc()
        raise HTTPException(status_code=500, detail=f"Eroare la generare: {str(e)}")
