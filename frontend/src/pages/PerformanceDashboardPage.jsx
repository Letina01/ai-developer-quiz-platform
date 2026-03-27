import { useEffect, useMemo, useState } from "react";
import { fetchResults } from "../api/resultApi";
import { useAuth } from "../hooks/useAuth";

function formatDate(isoValue) {
  if (!isoValue) {
    return "-";
  }
  return new Date(isoValue).toLocaleDateString();
}

export default function PerformanceDashboardPage() {
  const { user } = useAuth();
  const [results, setResults] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!user?.id) {
      return;
    }

    fetchResults(user.id)
      .then((response) => setResults(response.data || []))
      .catch(() => setError("Unable to load analytics right now."));
  }, [user?.id]);

  const metrics = useMemo(() => {
    const attempts = results.length;
    const totalScored = results.reduce((sum, item) => sum + (item.score || 0), 0);
    const totalQuestions = results.reduce((sum, item) => sum + (item.totalQuestions || 0), 0);
    const avgAccuracy = totalQuestions ? Math.round((totalScored / totalQuestions) * 100) : 0;
    return { attempts, totalScored, totalQuestions, avgAccuracy };
  }, [results]);

  return (
    <div className="dashboard-stack">
      <header className="page-header">
        <p className="page-kicker">Performance analytics</p>
        <h2>Progress summary for {user?.name || "your account"}.</h2>
      </header>

      {error ? <div className="form-error">{error}</div> : null}

      <section className="summary-strip">
        <article className="metric-item">
          <span className="metric-label">Attempts</span>
          <strong className="metric-value">{metrics.attempts}</strong>
        </article>
        <article className="metric-item">
          <span className="metric-label">Correct answers</span>
          <strong className="metric-value">{metrics.totalScored}</strong>
        </article>
        <article className="metric-item">
          <span className="metric-label">Total questions</span>
          <strong className="metric-value">{metrics.totalQuestions}</strong>
        </article>
        <article className="metric-item">
          <span className="metric-label">Avg accuracy</span>
          <strong className="metric-value">{metrics.avgAccuracy}%</strong>
        </article>
      </section>

      <section className="glass-card panel-card">
        <p className="page-kicker">Attempt history</p>
        {results.length === 0 ? (
          <p>No quiz attempts available yet.</p>
        ) : (
          <div className="dashboard-grid">
            {results.map((item) => {
              const scorePercent = item.totalQuestions ? Math.round((item.score / item.totalQuestions) * 100) : 0;
              return (
                <article className="glass-card panel-card" key={item.id}>
                  <h3>{item.topic}</h3>
                  <p>
                    Score {item.score}/{item.totalQuestions} ({scorePercent}%)
                  </p>
                  <p>Attempted on {formatDate(item.createdAt)}</p>
                </article>
              );
            })}
          </div>
        )}
      </section>
    </div>
  );
}
