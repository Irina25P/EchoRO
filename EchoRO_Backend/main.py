import os
from typing import Optional

import torch
from fastapi import FastAPI, Depends
from fastapi.staticfiles import StaticFiles
from pydantic import BaseModel
from transformers import AutoTokenizer
from parler_tts import ParlerTTSForConditionalGeneration

from app.core.security import get_current_user, get_optional_user, get_current_admin
from app.db.database import init_db
from app.services import auth_service, voice_service, feedback_service, stats_service

app = FastAPI(title="EchoRO Backend AI")

if not os.path.exists("generated_audio"):
    os.makedirs("generated_audio")

app.mount("/static", StaticFiles(directory="generated_audio"), name="static")


print("Încărcăm modelele Parler-TTS... (poate dura)")
device = "cuda:0" if torch.cuda.is_available() else "cpu"

mini_dir = "../../DATADRIVEN_MINI/output_dir_training"
large_dir = "../../DATADRIVEN_LARGE_5E-6/output_dir_training"

models = {"Mini": None, "Large": None}
tokenizers = {"Mini": None, "Large": None}

try:
    print("Se încarcă modelul Mini...")
    models["Mini"] = ParlerTTSForConditionalGeneration.from_pretrained(
        mini_dir, torch_dtype=torch.float16
    ).to(device)
    tokenizers["Mini"] = AutoTokenizer.from_pretrained(mini_dir)
    print(f"Modelul Mini a fost încărcat cu succes pe {device}!")
except Exception as e:
    print(f"Eroare la încărcarea modelului Mini: {e}")

try:
    print("Se încarcă modelul Large...")
    models["Large"] = ParlerTTSForConditionalGeneration.from_pretrained(
        large_dir, torch_dtype=torch.float16
    ).to(device)
    tokenizers["Large"] = AutoTokenizer.from_pretrained(large_dir)
    print(f"Modelul Large a fost încărcat cu succes pe {device}!")
except Exception as e:
    print(f"Eroare la încărcarea modelului Large: {e}")


init_db()


class RegisterRequest(BaseModel):
    full_name: str
    email: str
    password: str


class LoginRequest(BaseModel):
    email: str
    password: str


class GenerateRequest(BaseModel):
    text: str
    description: str
    model_type: str


class FeedbackRequest(BaseModel):
    audio_url: str
    model_type: str
    intelligibility: int
    naturalness: int
    accent: int
    word_accuracy: float
    gender_respected: bool
    comments: str


@app.post("/register")
def register(request: RegisterRequest):
    return auth_service.register(request.full_name, request.email, request.password)


@app.post("/login")
def login(request: LoginRequest):
    return auth_service.login(request.email, request.password)


@app.post("/generate-voice")
async def generate_voice(
    request: GenerateRequest,
    current_user: Optional[dict] = Depends(get_optional_user),
):
    user_id = int(current_user["sub"]) if current_user else 0
    return voice_service.generate(
        user_id=user_id,
        text=request.text,
        description=request.description,
        model_type=request.model_type,
        models=models,
        tokenizers=tokenizers,
        device=device,
    )


@app.post("/feedback")
async def submit_feedback(
    request: FeedbackRequest,
    current_user: Optional[dict] = Depends(get_optional_user),
):
    user_id = int(current_user["sub"]) if current_user else 0
    return feedback_service.submit(
        user_id=user_id,
        audio_url=request.audio_url,
        model_type=request.model_type,
        intelligibility=request.intelligibility,
        naturalness=request.naturalness,
        accent=request.accent,
        word_accuracy=request.word_accuracy,
        gender_respected=request.gender_respected,
        comments=request.comments,
    )


@app.get("/admin/stats/overview")
def stats_overview(_admin: dict = Depends(get_current_admin)):
    return stats_service.get_overview()


@app.get("/admin/stats/models")
def stats_models(_admin: dict = Depends(get_current_admin)):
    return stats_service.get_models()


@app.get("/admin/stats/trend")
def stats_trend(
    start_date: Optional[str] = None,
    end_date: Optional[str] = None,
    _admin: dict = Depends(get_current_admin),
):
    return stats_service.get_trend(start_date, end_date)


@app.get("/")
def read_root():
    return {"message": "EchoRO API is running!"}
