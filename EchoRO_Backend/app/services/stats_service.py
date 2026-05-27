import traceback
from typing import Optional

from fastapi import HTTPException

from app.repositories import feedback_repository, history_repository


def get_overview() -> dict:
    try:
        total_generations = history_repository.get_total_count()
        overall_mos = feedback_repository.get_average_mos()
        return {
            "status": "success",
            "total_generations": total_generations,
            "overall_mos": overall_mos,
        }
    except Exception as e:
        traceback.print_exc()
        raise HTTPException(status_code=500, detail=f"Eroare: {str(e)}")


def get_models() -> dict:
    try:
        raw_stats = feedback_repository.get_model_stats()
        model_stats = {
            "Mini": {"intelligibility": 0.0, "naturalness": 0.0, "accent": 0.0, "word_accuracy": 0.0, "gender_match": 0.0},
            "Large": {"intelligibility": 0.0, "naturalness": 0.0, "accent": 0.0, "word_accuracy": 0.0, "gender_match": 0.0},
        }
        for row in raw_stats:
            m_type = row["model_type"]
            if m_type in model_stats:
                model_stats[m_type] = {
                    "intelligibility": round(row["avg_intelligibility"] or 0, 1),
                    "naturalness": round(row["avg_naturalness"] or 0, 1),
                    "accent": round(row["avg_accent"] or 0, 1),
                    "word_accuracy": round(row["avg_accuracy"] or 0, 1),
                    "gender_match": round(row["gender_match_pct"] or 0, 1),
                }
        return {"status": "success", "models": model_stats}
    except Exception as e:
        traceback.print_exc()
        raise HTTPException(status_code=500, detail=f"Eroare: {str(e)}")


def get_trend(start_date: Optional[str] = None, end_date: Optional[str] = None) -> dict:
    try:
        raw = feedback_repository.get_trend(start_date, end_date)
        trend_data: dict = {}
        for row in raw:
            day = row["day"]
            m_type = row["model_type"]
            mos = round(row["daily_mos"] or 0, 1)
            if day not in trend_data:
                trend_data[day] = {"Mini": 0.0, "Large": 0.0}
            trend_data[day][m_type] = mos

        dates = list(trend_data.keys())
        return {
            "status": "success",
            "trend": {
                "dates": dates,
                "mini": [trend_data[d]["Mini"] for d in dates],
                "large": [trend_data[d]["Large"] for d in dates],
            },
        }
    except Exception as e:
        traceback.print_exc()
        raise HTTPException(status_code=500, detail=f"Eroare: {str(e)}")
