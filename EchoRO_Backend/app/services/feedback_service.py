from fastapi import HTTPException

from app.repositories import feedback_repository


def submit(
    user_id: int,
    audio_url: str,
    model_type: str,
    intelligibility: int,
    naturalness: int,
    accent: int,
    word_accuracy: float,
    gender_respected: bool,
    comments: str,
) -> dict:
    try:
        feedback_repository.save(
            user_id, audio_url, model_type, intelligibility,
            naturalness, accent, word_accuracy, gender_respected, comments,
        )
        return {"status": "success", "message": "Feedback salvat cu succes."}
    except Exception as e:
        raise HTTPException(
            status_code=500, detail=f"Eroare la salvarea feedback-ului: {str(e)}"
        )
