from typing import List

from fastapi import HTTPException

from app.repositories import ab_test_repository


def submit_session(user_id: int, total_count: int, results: list) -> dict:
    """
    Save a complete A/B testing session with all per-page results.
    `results` is a list of dicts with keys:
        page_index, model_a, model_b, naturalness, intelligibility, accent, word_accuracy
    """
    try:
        session_id = ab_test_repository.save_session(user_id, total_count)
        for item in results:
            ab_test_repository.save_result(
                session_id=session_id,
                page_index=item["page_index"],
                model_a=item.get("model_a", "Eagle"),
                model_b=item.get("model_b", "Reindeer"),
                naturalness=item["naturalness"],
                intelligibility=item["intelligibility"],
                accent=item["accent"],
                word_accuracy=item["word_accuracy"],
            )
        return {
            "status": "success",
            "message": "Rezultatele A/B testing au fost salvate.",
            "session_id": session_id,
        }
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Eroare la salvarea sesiunii A/B: {str(e)}",
        )


def get_sessions() -> dict:
    try:
        sessions = ab_test_repository.get_all_sessions()
        return {"status": "success", "sessions": sessions}
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Eroare la preluarea sesiunilor: {str(e)}",
        )


def get_session_detail(session_id: int) -> dict:
    try:
        results = ab_test_repository.get_session_results(session_id)
        return {"status": "success", "results": results}
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Eroare la preluarea rezultatelor: {str(e)}",
        )


def get_stats() -> dict:
    try:
        stats = ab_test_repository.get_aggregate_stats()
        return {"status": "success", "stats": stats}
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Eroare la calculul statisticilor: {str(e)}",
        )


def get_rankings() -> dict:
    """Return per-measure Bradley-Terry Elo rankings + win-rate matrices."""
    try:
        data = ab_test_repository.get_rankings()
        return {"status": "success", **data}
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Eroare la calculul rankingurilor Bradley-Terry: {str(e)}",
        )