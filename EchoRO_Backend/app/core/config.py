import os

SECRET_KEY = os.getenv("SECRET_KEY", "echoro-secret-key-must-be-changed-in-production")
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 60 * 24 * 14
