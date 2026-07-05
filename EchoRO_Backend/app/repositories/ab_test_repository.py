from typing import List, Optional
import math
import random

from app.db.database import get_connection

AB_MODELS = ["Eagle", "Reindeer", "Sparrow", "Wolf"]
AB_MEASURES = ["naturalness", "intelligibility", "accent", "word_accuracy"]
NUM_BOOTSTRAP = 500


def save_session(user_id: int, total_count: int) -> int:
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute(
        "INSERT INTO ab_test_session (user_id, total_count) VALUES (?, ?)",
        (user_id, total_count),
    )
    session_id = cursor.lastrowid
    conn.commit()
    conn.close()
    return session_id


def save_result(
    session_id: int,
    page_index: int,
    model_a: str,
    model_b: str,
    naturalness: str,
    intelligibility: str,
    accent: str,
    word_accuracy: str,
) -> None:
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute(
        """
        INSERT INTO ab_test_result
            (session_id, page_index, model_a, model_b,
             naturalness, intelligibility, accent, word_accuracy)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """,
        (session_id, page_index, model_a, model_b,
         naturalness, intelligibility, accent, word_accuracy),
    )
    conn.commit()
    conn.close()


def get_all_sessions() -> list:
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute("""
        SELECT s.id, s.user_id, s.total_count, s.completed_at,
               COUNT(r.id) AS result_count
        FROM ab_test_session s
        LEFT JOIN ab_test_result r ON r.session_id = s.id
        GROUP BY s.id
        ORDER BY s.completed_at DESC
    """)
    rows = cursor.fetchall()
    conn.close()
    return [dict(row) for row in rows]


def get_session_results(session_id: int) -> list:
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute(
        "SELECT * FROM ab_test_result WHERE session_id = ? ORDER BY page_index",
        (session_id,),
    )
    rows = cursor.fetchall()
    conn.close()
    return [dict(row) for row in rows]


def get_aggregate_stats() -> dict:
    conn = get_connection()
    cursor = conn.cursor()

    cursor.execute("SELECT COUNT(*) FROM ab_test_result")
    total = cursor.fetchone()[0]

    def pct(column: str, value: str) -> float:
        if not total:
            return 0.0
        cursor.execute(
            f"SELECT COUNT(*) * 100.0 / ? FROM ab_test_result WHERE {column} = ?",
            (total, value),
        )
        result = cursor.fetchone()[0]
        return round(result, 1) if result else 0.0

    stats = {
        "total_results": total,
        "naturalness_voice_a_pct": pct("naturalness", "VOICE_A"),
        "naturalness_voice_b_pct": pct("naturalness", "VOICE_B"),
        "naturalness_equal_pct": pct("naturalness", "EQUAL"),
        "intelligibility_voice_a_pct": pct("intelligibility", "VOICE_A"),
        "intelligibility_voice_b_pct": pct("intelligibility", "VOICE_B"),
        "intelligibility_equal_pct": pct("intelligibility", "EQUAL"),
        "accent_voice_a_pct": pct("accent", "VOICE_A"),
        "accent_voice_b_pct": pct("accent", "VOICE_B"),
        "accent_equal_pct": pct("accent", "EQUAL"),
        "word_accuracy_voice_a_pct": pct("word_accuracy", "VOICE_A"),
        "word_accuracy_voice_b_pct": pct("word_accuracy", "VOICE_B"),
        "word_accuracy_equal_pct": pct("word_accuracy", "EQUAL"),
    }

    conn.close()
    return stats


# ---------------------------------------------------------------------------
# Bradley-Terry ranking helpers
# ---------------------------------------------------------------------------

def _build_wins_matrix(trials: list, measure: str) -> dict:
    """
    Build wins[i][j] = number of times model i beat model j for the given measure.
    VOICE_A = model_a won, VOICE_B = model_b won, EQUAL = split 0.5 each.
    """
    wins = {m: {n: 0.0 for n in AB_MODELS} for m in AB_MODELS}
    for row in trials:
        ma = row["model_a"]
        mb = row["model_b"]
        choice = row[measure]
        if ma not in AB_MODELS or mb not in AB_MODELS:
            continue
        if choice == "VOICE_A":
            wins[ma][mb] += 1.0
        elif choice == "VOICE_B":
            wins[mb][ma] += 1.0
        elif choice == "EQUAL":
            wins[ma][mb] += 0.5
            wins[mb][ma] += 0.5
    return wins


def _bradley_terry(wins: dict, models: list, max_iter: int = 100) -> dict:
    """Maximum-likelihood Bradley-Terry strength estimation."""
    strengths = {m: 1.0 for m in models}
    for _ in range(max_iter):
        new_strengths = {}
        for i in models:
            total_wins_i = sum(wins[i][j] for j in models if j != i)
            if total_wins_i == 0:
                new_strengths[i] = 1e-10
                continue
            denom = sum(
                (wins[i][j] + wins[j][i]) / (strengths[i] + strengths[j])
                for j in models
                if j != i and (wins[i][j] + wins[j][i]) > 0
            )
            new_strengths[i] = total_wins_i / denom if denom > 0 else 1e-10
        total = sum(new_strengths.values())
        for m in models:
            strengths[m] = new_strengths[m] * len(models) / total
    return strengths


def _to_elo(strengths: dict) -> dict:
    return {m: round(400 * math.log10(max(s, 1e-10)) + 1000, 1)
            for m, s in strengths.items()}


def _win_rate_matrix(wins: dict, models: list) -> tuple:
    """
    Returns (win_rates, significance) dicts.
    win_rates[i][j] = % of times i beat j (ignoring EQUAL halves).
    significance[i][j] = True if binomial p < 0.05.
    """
    rates: dict = {m: {} for m in models}
    sig: dict = {m: {} for m in models}
    for i in models:
        for j in models:
            if i == j:
                continue
            w = wins[i][j]
            l = wins[j][i]
            n = w + l
            if n == 0:
                rates[i][j] = 50.0
                sig[i][j] = False
            else:
                rates[i][j] = round(w * 100.0 / n, 1)
                # Simple binomial test: significant if outside [40%, 60%] approximately
                # For small n, use 1.96 * sqrt(0.25/n) margin
                margin = 1.96 * math.sqrt(0.25 / n) * 100
                sig[i][j] = abs(w * 100.0 / n - 50.0) > margin
    return rates, sig


def get_rankings() -> dict:
    """Compute per-measure Bradley-Terry rankings with bootstrap CIs."""
    conn = get_connection()
    cursor = conn.cursor()
    cursor.execute(
        "SELECT model_a, model_b, naturalness, intelligibility, accent, word_accuracy "
        "FROM ab_test_result"
    )
    rows = cursor.fetchall()
    conn.close()
    trials = [dict(r) for r in rows]

    if not trials:
        empty_rankings = [
            {"model": m, "elo": 1000.0, "ci_low": 1000.0, "ci_high": 1000.0}
            for m in AB_MODELS
        ]
        empty_win_rates = {m: {n: 50.0 for n in AB_MODELS if n != m} for m in AB_MODELS}
        empty_sig = {m: {n: False for n in AB_MODELS if n != m} for m in AB_MODELS}
        measures_data = {}
        for measure in AB_MEASURES:
            measures_data[measure] = {
                "rankings": empty_rankings,
                "win_rates": empty_win_rates,
                "significance": empty_sig,
            }
        return {"total_trials": 0, "measures": measures_data}

    measures_data = {}
    for measure in AB_MEASURES:
        wins = _build_wins_matrix(trials, measure)
        strengths = _bradley_terry(wins, AB_MODELS)
        elo_scores = _to_elo(strengths)
        win_rates, significance = _win_rate_matrix(wins, AB_MODELS)

        # Bootstrap CIs
        elo_samples = {m: [] for m in AB_MODELS}
        for _ in range(NUM_BOOTSTRAP):
            resampled = random.choices(trials, k=len(trials))
            w2 = _build_wins_matrix(resampled, measure)
            s2 = _bradley_terry(w2, AB_MODELS)
            e2 = _to_elo(s2)
            for m in AB_MODELS:
                elo_samples[m].append(e2[m])

        rankings = []
        for m in AB_MODELS:
            sorted_samples = sorted(elo_samples[m])
            ci_low = sorted_samples[int(0.025 * NUM_BOOTSTRAP)]
            ci_high = sorted_samples[int(0.975 * NUM_BOOTSTRAP)]
            rankings.append({
                "model": m,
                "elo": elo_scores[m],
                "ci_low": round(ci_low, 1),
                "ci_high": round(ci_high, 1),
            })
        rankings.sort(key=lambda x: x["elo"], reverse=True)

        measures_data[measure] = {
            "rankings": rankings,
            "win_rates": win_rates,
            "significance": significance,
        }

    return {"total_trials": len(trials), "measures": measures_data}