import sqlite3
from typing import Optional

from app.db.database import get_connection


def find_by_email(email: str) -> Optional[dict]:
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute(
        "SELECT id, full_name, email, password, role FROM users WHERE email = ?",
        (email,),
    )
    row = cursor.fetchone()
    conn.close()
    return dict(row) if row else None


def create_user(full_name: str, email: str, hashed_password: str, role: str = "pro") -> dict:
    conn = get_connection()
    cursor = conn.cursor()
    try:
        cursor.execute(
            "INSERT INTO users (full_name, email, password, role) VALUES (?, ?, ?, ?)",
            (full_name, email, hashed_password, role),
        )
        user_id = cursor.lastrowid
        conn.commit()
        return {"id": user_id, "full_name": full_name, "email": email, "role": role}
    except sqlite3.IntegrityError:
        raise
    finally:
        conn.close()
