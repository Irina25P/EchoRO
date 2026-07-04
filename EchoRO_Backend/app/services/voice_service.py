import os
import re
import uuid

import numpy as np
import soundfile as sf
import torch
from fastapi import HTTPException
from num2words import num2words

from app.repositories import history_repository

DO_SAMPLE = True
TEMPERATURE = 1.0
MAX_LENGTH = 2580
MIN_NEW_TOKENS = None

ADD_SILENCE_PADDING = True
SILENCE_SECONDS = 0.3


def _normalize_text(text: str) -> str:
    def replace_number(match):
        try:
            return num2words(int(match.group(0)), lang="ro")
        except Exception:
            return match.group(0)

    text_curat = re.sub(r'\d+', replace_number, text)
    return ' '.join(text_curat.split())


def generate(
    user_id: int,
    text: str,
    description: str,
    model_type: str,
    models: dict,
    tokenizers: dict,
    device: str,
) -> dict:
    if model_type not in models or models[model_type] is None:
        raise HTTPException(
            status_code=500,
            detail=f"Modelul AI '{model_type}' nu este disponibil pe server.",
        )

    active_model = models[model_type]
    active_tokenizer = tokenizers[model_type]

    try:
        text_normalized = _normalize_text(text)

        desc_tokens = active_tokenizer(description, return_tensors="pt").to(device)
        prompt_tokens = active_tokenizer(text_normalized, return_tensors="pt").to(device)

        num_codebooks = active_model.decoder.config.num_codebooks
        decoder_max = active_model.decoder.config.max_length
        gen_kwargs = {
            "do_sample": DO_SAMPLE,
            "temperature": TEMPERATURE,
            "max_length": MAX_LENGTH if MAX_LENGTH is not None else decoder_max,
            "min_new_tokens": MIN_NEW_TOKENS if MIN_NEW_TOKENS is not None else num_codebooks + 1,
        }

        with torch.inference_mode():
            generation = active_model.generate(
                input_ids=desc_tokens.input_ids,
                attention_mask=desc_tokens.attention_mask,
                prompt_input_ids=prompt_tokens.input_ids,
                prompt_attention_mask=prompt_tokens.attention_mask,
                **gen_kwargs,
            )

        audio_arr = generation.squeeze().float().cpu().numpy()
        sample_rate = active_model.config.sampling_rate

        if ADD_SILENCE_PADDING:
            pad = np.zeros(int(SILENCE_SECONDS * sample_rate), dtype=audio_arr.dtype)
            audio_arr = np.concatenate([audio_arr, pad])

        file_name = f"voice_{uuid.uuid4()}.wav"
        file_path = os.path.join("generated_audio", file_name)
        sf.write(file_path, audio_arr, sample_rate)

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