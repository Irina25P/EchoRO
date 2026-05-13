import os
import uuid
import torch
import re
import sqlite3
from fastapi import FastAPI, HTTPException
from fastapi.staticfiles import StaticFiles
from pydantic import BaseModel
from transformers import AutoTokenizer
from parler_tts import ParlerTTSForConditionalGeneration
import soundfile as sf
from num2words import num2words

app = FastAPI(title="EchoRO Backend AI")

if not os.path.exists("generated_audio"):
    os.makedirs("generated_audio")

app.mount("/static", StaticFiles(directory="generated_audio"), name="static")

print("Încărcăm modelul Parler-TTS... (poate dura)")
device = "cuda:0" if torch.cuda.is_available() else "cpu"
model_dir = "../../DATADRIVEN_MINI/output_dir_training"

try:
    model = ParlerTTSForConditionalGeneration.from_pretrained(model_dir).to(device)
    tokenizer = AutoTokenizer.from_pretrained(model_dir)
    print(f"Modelul a fost încărcat cu succes pe {device}!")
except Exception as e:
    print(f"Eroare la încărcarea modelului: {e}")
    model = None

def normalizare_text_romana(text):
    def inlocuieste_numar(match):
        numar = int(match.group(0))
        return num2words(numar, lang='ro')

    return re.sub(r'\b\d+\b', inlocuieste_numar, text)


class GenerateRequest(BaseModel):
    user_id: int
    text: str
    description: str
    model_type: str


@app.post("/generate-voice")
async def generate_voice(request: GenerateRequest):
    if model is None:
        raise HTTPException(status_code=500, detail="Modelul AI nu este încărcat pe server.")

    try:
        text_normalizat = normalizare_text_romana(request.text)

        input_ids = tokenizer(request.description, return_tensors="pt").input_ids.to(device)
        prompt_input_ids = tokenizer(text_normalizat, return_tensors="pt").input_ids.to(device)

        with torch.no_grad():
            generation = model.generate(input_ids=input_ids, prompt_input_ids=prompt_input_ids)

        audio_arr = generation.cpu().numpy().squeeze()

        file_name = f"voice_{uuid.uuid4()}.wav"
        file_path = os.path.join("generated_audio", file_name)
        sf.write(file_path, audio_arr, model.config.sampling_rate)

        conn = sqlite3.connect("echoro.db")
        cursor = conn.cursor()
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                text_original TEXT,
                file_name TEXT,
                model_type TEXT,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """)
        cursor.execute("INSERT INTO history (user_id, text_original, file_name, model_type) VALUES (?, ?, ?, ?)",
                       (request.user_id, request.text, file_name, request.model_type))
        conn.commit()
        conn.close()

        return {
            "status": "success",
            "audio_url": f"/static/{file_name}",
            "text_used": text_normalizat
        }


    except Exception as e:
        import traceback

        print(f"\n======================================")

        print(f"EROARE FATALĂ LA GENERARE:")

        print(traceback.format_exc())

        print(f"DATE PRIMITE: {request}")

        print(f"======================================\n")

        raise HTTPException(status_code=500, detail=f"Eroare la generare: {str(e)}")

def init_db():
    conn = sqlite3.connect("echoro.db")
    cursor = conn.cursor()
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            full_name TEXT NOT NULL,
            email TEXT UNIQUE NOT NULL,
            password TEXT NOT NULL,
            role TEXT NOT NULL
        )
    """)

    cursor.execute("SELECT * FROM users WHERE email='admin@echoro.com'")
    if not cursor.fetchone():
        cursor.execute("""
            INSERT INTO users (full_name, email, password, role) 
            VALUES ('Administrator', 'admin@echoro.com', 'admin', 'admin')
        """)

    conn.commit()
    conn.close()


init_db()


class RegisterRequest(BaseModel):
    full_name: str
    email: str
    password: str


class LoginRequest(BaseModel):
    email: str
    password: str

@app.post("/register")
def register_user(request: RegisterRequest):
    conn = sqlite3.connect("echoro.db")
    cursor = conn.cursor()

    try:
        cursor.execute("""
            INSERT INTO users (full_name, email, password, role)
            VALUES (?, ?, ?, 'pro')
        """, (request.full_name, request.email, request.password))

        user_id = cursor.lastrowid
        conn.commit()
        return {
            "status": "success",
            "message": "Cont creat cu succes",
            "user": {
                "id": user_id,
                "full_name": request.full_name,
                "email": request.email,
                "role": "pro"
            }
        }
    except sqlite3.IntegrityError:
        raise HTTPException(status_code=400, detail="Acest email este deja folosit!")
    finally:
        conn.close()


@app.post("/login")
def login_user(request: LoginRequest):
    conn = sqlite3.connect("echoro.db")
    cursor = conn.cursor()

    cursor.execute("""
        SELECT id, full_name, email, role 
        FROM users 
        WHERE email = ? AND password = ?
    """, (request.email, request.password))

    user = cursor.fetchone()
    conn.close()

    if user:
        return {
            "status": "success",
            "user": {
                "id": user[0],
                "full_name": user[1],
                "email": user[2],
                "role": user[3]
            }
        }
    else:
        raise HTTPException(status_code=401, detail="Email sau parolă incorecte!")


@app.get("/")
def read_root():
    return {"message": "EchoRO API is running!"}


class FeedbackRequest(BaseModel):
    user_id: int
    audio_url: str
    model_type: str
    intelligibility: int
    naturalness: int
    accent: int
    word_accuracy: float
    gender_respected: bool
    comments: str


@app.post("/feedback")
async def submit_feedback(request: FeedbackRequest):
    try:
        conn = sqlite3.connect("echoro.db")
        cursor = conn.cursor()

        cursor.execute("""
            CREATE TABLE IF NOT EXISTS feedback (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                audio_url TEXT,
                model_type TEXT,
                intelligibility INTEGER,
                naturalness INTEGER,
                accent INTEGER,
                word_accuracy REAL,
                gender_respected INTEGER,
                comments TEXT,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """)

        cursor.execute("""
            INSERT INTO feedback 
            (user_id, audio_url, model_type, intelligibility, naturalness, accent, word_accuracy, gender_respected, comments) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, (
            request.user_id,
            request.audio_url,
            request.model_type,
            request.intelligibility,
            request.naturalness,
            request.accent,
            request.word_accuracy,
            1 if request.gender_respected else 0,
            request.comments
        ))

        conn.commit()
        conn.close()

        return {"status": "success", "message": "Feedback salvat cu succes."}

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Eroare la salvarea feedback-ului: {str(e)}")


@app.get("/admin/stats")
def get_admin_stats(start_date: str = None, end_date: str = None):
    try:
        conn = sqlite3.connect("echoro.db")
        conn.row_factory = sqlite3.Row
        cursor = conn.cursor()

        # 1. Gestionarea filtrelor de timp (DOAR PENTRU GRAFICUL DE TREND)
        date_filter = ""
        params = []
        if start_date and end_date:
            date_filter = "WHERE date(created_at) BETWEEN date(?) AND date(?)"
            params = [start_date, end_date]

        # 2. Total Generări (TOATĂ BAZA DE DATE - fără filtru)
        cursor.execute("SELECT COUNT(*) as total FROM history")
        total_generations = cursor.fetchone()["total"]

        # 3. Media Generală MOS (TOATĂ BAZA DE DATE - fără filtru)
        cursor.execute("""
            SELECT AVG((intelligibility + naturalness + accent) / 3.0) as avg_mos
            FROM feedback
        """)
        avg_mos_row = cursor.fetchone()
        overall_mos = round(avg_mos_row["avg_mos"], 1) if avg_mos_row["avg_mos"] else 0.0

        # 4. Statistici per Model (TOATĂ BAZA DE DATE - fără filtru)
        cursor.execute("""
            SELECT 
                model_type,
                AVG(intelligibility) as avg_intelligibility,
                AVG(naturalness) as avg_naturalness,
                AVG(accent) as avg_accent,
                AVG(word_accuracy) as avg_accuracy,
                AVG(gender_respected) * 100 as gender_match_pct
            FROM feedback 
            GROUP BY model_type
        """)
        model_stats_raw = cursor.fetchall()

        model_stats = {
            "Mini": {"intelligibility": 0.0, "naturalness": 0.0, "accent": 0.0, "word_accuracy": 0.0, "gender_match": 0.0},
            "Large": {"intelligibility": 0.0, "naturalness": 0.0, "accent": 0.0, "word_accuracy": 0.0, "gender_match": 0.0}
        }

        for row in model_stats_raw:
            m_type = row["model_type"]
            if m_type in model_stats:
                model_stats[m_type] = {
                    "intelligibility": round(row["avg_intelligibility"] or 0, 1),
                    "naturalness": round(row["avg_naturalness"] or 0, 1),
                    "accent": round(row["avg_accent"] or 0, 1),
                    "word_accuracy": round(row["avg_accuracy"] or 0, 1),
                    "gender_match": round(row["gender_match_pct"] or 0, 1)
                }

        # 5. Evoluția în Timp (FILTRATĂ CU date_filter)
        cursor.execute(f"""
            SELECT 
                date(created_at) as day,
                model_type,
                AVG((intelligibility + naturalness + accent) / 3.0) as daily_mos
            FROM feedback
            {date_filter}
            GROUP BY day, model_type
            ORDER BY day ASC
        """, params)
        trend_raw = cursor.fetchall()

        trend_data = {}
        for row in trend_raw:
            day = row["day"]
            m_type = row["model_type"]
            mos = round(row["daily_mos"] or 0, 1)

            if day not in trend_data:
                trend_data[day] = {"Mini": 0.0, "Large": 0.0}
            trend_data[day][m_type] = mos

        dates = list(trend_data.keys())
        mini_trend = [trend_data[d]["Mini"] for d in dates]
        large_trend = [trend_data[d]["Large"] for d in dates]

        conn.close()

        return {
            "status": "success",
            "total_generations": total_generations,
            "overall_mos": overall_mos,
            "models": model_stats,
            "trend": {
                "dates": dates,
                "mini": mini_trend,
                "large": large_trend
            }
        }

    except Exception as e:
        import traceback
        print("EROARE ADMIN STATS:")
        print(traceback.format_exc())
        raise HTTPException(status_code=500, detail=f"Eroare: {str(e)}")