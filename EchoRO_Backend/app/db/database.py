import os
import sqlite3

_BASE_DIR = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
DB_PATH = os.path.join(_BASE_DIR, "echoro.db")


def get_connection() -> sqlite3.Connection:
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    return conn


def init_db() -> None:
    from app.core.security import hash_password, is_hashed

    conn = get_connection()
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
        CREATE TABLE IF NOT EXISTS ab_test_session (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            total_count INTEGER NOT NULL,
            completed_at DATETIME DEFAULT CURRENT_TIMESTAMP
        )
    """)

    cursor.execute("""
        CREATE TABLE IF NOT EXISTS ab_test_result (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            session_id INTEGER NOT NULL,
            page_index INTEGER NOT NULL,
            model_a TEXT NOT NULL DEFAULT 'Eagle',
            model_b TEXT NOT NULL DEFAULT 'Reindeer',
            naturalness TEXT NOT NULL,
            intelligibility TEXT NOT NULL,
            accent TEXT NOT NULL,
            word_accuracy TEXT NOT NULL,
            FOREIGN KEY (session_id) REFERENCES ab_test_session(id)
        )
    """)

    # Migration: add model_a / model_b if upgrading from older schema
    for col, default in [("model_a", "Eagle"), ("model_b", "Reindeer")]:
        try:
            cursor.execute(
                f"ALTER TABLE ab_test_result ADD COLUMN {col} TEXT NOT NULL DEFAULT '{default}'"
            )
        except Exception:
            pass

    cursor.execute("SELECT id, password FROM users WHERE email = 'admin@echoro.com'")
    admin = cursor.fetchone()
    if not admin:
        cursor.execute(
            "INSERT INTO users (full_name, email, password, role) VALUES (?, ?, ?, ?)",
            ("Administrator", "admin@echoro.com", hash_password("admin"), "admin"),
        )
    elif not is_hashed(admin["password"]):
        cursor.execute(
            "UPDATE users SET password = ? WHERE email = 'admin@echoro.com'",
            (hash_password(admin["password"]),),
        )

    cursor.execute("SELECT id, password FROM users WHERE email != 'admin@echoro.com'")
    for user in cursor.fetchall():
        if not is_hashed(user["password"]):
            cursor.execute(
                "UPDATE users SET password = ? WHERE id = ?",
                (hash_password(user["password"]), user["id"]),
            )

    conn.commit()
    conn.close()
