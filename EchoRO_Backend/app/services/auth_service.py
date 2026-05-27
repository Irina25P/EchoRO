import sqlite3

from fastapi import HTTPException

from app.core.security import hash_password, verify_password, create_access_token
from app.repositories import user_repository


def register(full_name: str, email: str, password: str) -> dict:
    hashed = hash_password(password)
    try:
        user = user_repository.create_user(full_name, email, hashed)
    except sqlite3.IntegrityError:
        raise HTTPException(status_code=400, detail="Acest email este deja folosit!")

    token = create_access_token(
        {"sub": str(user["id"]), "email": user["email"], "role": user["role"]}
    )
    return {
        "status": "success",
        "message": "Cont creat cu succes",
        "access_token": token,
        "token_type": "bearer",
        "user": user,
    }


def login(email: str, password: str) -> dict:
    user = user_repository.find_by_email(email)
    if not user or not verify_password(password, user["password"]):
        raise HTTPException(status_code=401, detail="Email sau parolă incorecte!")

    token = create_access_token(
        {"sub": str(user["id"]), "email": user["email"], "role": user["role"]}
    )
    return {
        "status": "success",
        "access_token": token,
        "token_type": "bearer",
        "user": {
            "id": user["id"],
            "full_name": user["full_name"],
            "email": user["email"],
            "role": user["role"],
        },
    }
