import os
import json
import random
from typing import Optional

import torch
from fastapi import FastAPI, Depends
from fastapi.staticfiles import StaticFiles
from pydantic import BaseModel
from transformers import AutoTokenizer
from parler_tts import ParlerTTSForConditionalGeneration

from app.core.security import get_current_user, get_optional_user, get_current_admin
from app.db.database import init_db
from app.services import auth_service, voice_service, feedback_service, stats_service, ab_test_service

app = FastAPI(title="EchoRO Backend AI")

if not os.path.exists("generated_audio"):
    os.makedirs("generated_audio")

AB_TEST_DIR = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "ab_test_clean_new")
AB_TEST_DIR = os.path.normpath(AB_TEST_DIR)

app.mount("/static", StaticFiles(directory="generated_audio"), name="static")
if os.path.isdir(AB_TEST_DIR):
    app.mount("/ab-static", StaticFiles(directory=AB_TEST_DIR), name="ab-static")


print("Încărcăm modelele Parler-TTS... (poate dura)")
device = "cuda:0" if torch.cuda.is_available() else "cpu"

MODEL_DIRS = {
    "Eagle":    "D:/UTCN/4/PWEB/Eagle/checkpoint-41550-epoch-49",
    "Wolf":     "D:/UTCN/4/PWEB/Wolf/checkpoint-14400-epoch-49",
    "Reindeer": "D:/UTCN/4/PWEB/Reindeer/checkpoint-13500-epoch-46",
    "Sparrow":  "D:/UTCN/4/PWEB/Sparrow/checkpoint-21000-epoch-22",
}

models = {name: None for name in MODEL_DIRS}
tokenizers = {name: None for name in MODEL_DIRS}

for model_name, model_dir in MODEL_DIRS.items():
    try:
        print(f"Se încarcă modelul {model_name}...")
        models[model_name] = ParlerTTSForConditionalGeneration.from_pretrained(
            model_dir, trust_remote_code=True, torch_dtype=torch.float16
        ).to(device)
        tok = AutoTokenizer.from_pretrained(model_dir, trust_remote_code=True)
        if tok.pad_token is None and tok.eos_token is not None:
            tok.pad_token = tok.eos_token
        tokenizers[model_name] = tok
        print(f"Modelul {model_name} a fost încărcat cu succes pe {device}!")
    except Exception as e:
        print(f"Eroare la încărcarea modelului {model_name}: {e}")


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


class ABTestPageResult(BaseModel):
    page_index: int
    model_a: str = "Eagle"
    model_b: str = "Reindeer"
    naturalness: str
    intelligibility: str
    accent: str
    word_accuracy: str


class ABTestRequest(BaseModel):
    total_count: int
    results: list[ABTestPageResult]


AB_PAIRS: list[tuple[str, str]] = [
    ("Eagle",    "Reindeer"),
    ("Eagle",    "Sparrow"),
    ("Eagle",    "Wolf"),
    ("Reindeer", "Sparrow"),
    ("Reindeer", "Wolf"),
    ("Sparrow",  "Wolf"),
]

# Load sentences metadata once at startup
_SENTENCES_PATH = os.path.join(AB_TEST_DIR, "100_sentences.json")
_SENTENCES: list[dict] = []
if os.path.isfile(_SENTENCES_PATH):
    with open(_SENTENCES_PATH, "r", encoding="utf-8") as _f:
        _SENTENCES = json.load(_f)
    print(f"Loaded {len(_SENTENCES)} sentences from sentences.json")


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


@app.post("/ab-test")
async def submit_ab_test(
    request: ABTestRequest,
    current_user: Optional[dict] = Depends(get_optional_user),
):
    import traceback as _tb
    try:
        user_id = int(current_user["sub"]) if current_user else 0
        results_dicts = [r.model_dump() for r in request.results]
        return ab_test_service.submit_session(user_id, request.total_count, results_dicts)
    except Exception as _e:
        _tb.print_exc()
        raise


@app.get("/admin/ab-test/sessions")
def ab_test_sessions(_admin: dict = Depends(get_current_admin)):
    return ab_test_service.get_sessions()


@app.get("/admin/ab-test/sessions/{session_id}")
def ab_test_session_detail(session_id: int, _admin: dict = Depends(get_current_admin)):
    return ab_test_service.get_session_detail(session_id)


@app.get("/admin/ab-test/stats")
def ab_test_stats(_admin: dict = Depends(get_current_admin)):
    return ab_test_service.get_stats()


@app.get("/admin/ab-test/rankings")
def ab_test_rankings(_admin: dict = Depends(get_current_admin)):
    return ab_test_service.get_rankings()


@app.get("/ab-test/pair/{page_index}")
def get_ab_test_pair(page_index: int):
    """Kept for backwards compatibility — prefer /ab-test/session."""
    pair = AB_PAIRS[page_index % len(AB_PAIRS)]
    model_a, model_b = pair
    sentence_num = (page_index // len(AB_PAIRS)) % 100 + 1
    sentence_id = f"{sentence_num:03d}"
    audio_url_a = f"/ab-static/{model_a.lower()}/{model_a.lower()}_{sentence_id}.wav"
    audio_url_b = f"/ab-static/{model_b.lower()}/{model_b.lower()}_{sentence_id}.wav"
    return {
        "page_index": page_index,
        "model_a": model_a,
        "model_b": model_b,
        "audio_url_a": audio_url_a,
        "audio_url_b": audio_url_b,
        "sentence_id": sentence_id,
    }


@app.get("/ab-test/session")
def get_ab_test_session(count: int = 10):
    """
    Creates a full AB test session upfront.
    Picks one random model pair, then picks `count` random sentence IDs.
    Returns all items at once so the mobile app can page through them
    without further API calls.
    """
    count = max(1, min(count, 100))
    pair = random.choice(AB_PAIRS)
    model_a, model_b = pair

    # Sample `count` sentence numbers (1–100) without replacement
    sentence_nums = random.sample(range(1, 101), count)

    items = []
    for i, num in enumerate(sentence_nums):
        sentence_id = f"{num:03d}"
        audio_url_a = f"/ab-static/{model_a.lower()}/{model_a.lower()}_{sentence_id}.wav"
        audio_url_b = f"/ab-static/{model_b.lower()}/{model_b.lower()}_{sentence_id}.wav"

        # Look up text/description if sentences.json was loaded
        sentence_meta = next(
            (s for s in _SENTENCES if s.get("id") == f"text_{sentence_id}"),
            None
        )
        text = sentence_meta["text"] if sentence_meta else ""
        description = sentence_meta["description"] if sentence_meta else ""

        items.append({
            "page_index": i,
            "sentence_id": sentence_id,
            "audio_url_a": audio_url_a,
            "audio_url_b": audio_url_b,
            "text": text,
            "description": description,
        })

    return {
        "model_a": model_a,
        "model_b": model_b,
        "total_count": count,
        "items": items,
    }


@app.get("/")
def read_root():
    return {"message": "EchoRO API is running!"}
