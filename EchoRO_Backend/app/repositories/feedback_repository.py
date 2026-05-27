from typing import Optional

from app.db.database import get_connection


def save(
    user_id: int,
    audio_url: str,
    model_type: str,
    intelligibility: int,
    naturalness: int,
    accent: int,
    word_accuracy: float,
    gender_respected: bool,
    comments: str,
) -> None:
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute(
        """
        INSERT INTO feedback
            (user_id, audio_url, model_type, intelligibility, naturalness, accent,
             word_accuracy, gender_respected, comments)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """,
        (
            user_id, audio_url, model_type, intelligibility, naturalness, accent,
            word_accuracy, 1 if gender_respected else 0, comments,
        ),
    )
    conn.commit()
    conn.close()


def get_average_mos() -> float:
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT AVG((intelligibility + naturalness + accent) / 3.0) FROM feedback")
    result = cursor.fetchone()[0]
    conn.close()
    return round(result, 1) if result else 0.0


def get_model_stats() -> list:
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute("""
        SELECT
            model_type,
            AVG(intelligibility)         AS avg_intelligibility,
            AVG(naturalness)             AS avg_naturalness,
            AVG(accent)                  AS avg_accent,
            AVG(word_accuracy)           AS avg_accuracy,
            AVG(gender_respected) * 100  AS gender_match_pct
        FROM feedback
        GROUP BY model_type
    """)
    rows = cursor.fetchall()
    conn.close()
    return [dict(row) for row in rows]


def get_trend(start_date: Optional[str], end_date: Optional[str]) -> list:
    conn = get_connection()
    cursor = conn.cursor()

    date_filter = ""
    params = []
    if start_date and end_date:
        date_filter = "WHERE date(created_at) BETWEEN date(?) AND date(?)"
        params = [start_date, end_date]

    cursor.execute(
        f"""
        SELECT
            date(created_at) AS day,
            model_type,
            AVG((intelligibility + naturalness + accent) / 3.0)  AS daily_mos
        FROM feedback
        {date_filter}
        GROUP BY day, model_type
        ORDER BY day ASC
        """,
        params,
    )
    rows = cursor.fetchall()
    conn.close()
    return [dict(row) for row in rows]
