from app.db.database import get_connection


def save(user_id: int, text_original: str, file_name: str, model_type: str) -> None:
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute(
        "INSERT INTO history (user_id, text_original, file_name, model_type) VALUES (?, ?, ?, ?)",
        (user_id, text_original, file_name, model_type),
    )
    conn.commit()
    conn.close()


def get_total_count() -> int:
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT COUNT(*) FROM history")
    count = cursor.fetchone()[0]
    conn.close()
    return count
