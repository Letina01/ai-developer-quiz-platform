import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { fetchRecommendations } from "../api/recommendationApi";
import { fetchResults } from "../api/resultApi";
import { getPlaybook } from "../data/studyResources";
import { useAuth } from "../hooks/useAuth";

export default function DashboardPage() {
  const { user } = useAuth();
  const [results, setResults] = useState([]);
  const [recommendations, setRecommendations] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!user?.id) {
      return;
    }

    Promise.all([fetchResults(user.id), fetchRecommendations(user.id)])
      .then(([resultResponse, recommendationResponse]) => {
        setResults(resultResponse.data || []);
        setRecommendations(recommendationResponse.data || null);
      })
      .catch(() => setError("Unable to load full dashboard data. You can still continue practicing."));
  }, [user?.id]);

  const attempts = results.length;
  const totalQuestions = results.reduce((sum, item) => sum + (item.totalQuestions || 0), 0);
  const totalScore = results.reduce((sum, item) => sum + (item.score || 0), 0);
  const avgScore = totalQuestions > 0 ? Math.round((totalScore / totalQuestions) * 100) : 0;
  const playbook = getPlaybook(user?.focusDomain);

  return (
    <div className="dashboard-stack">
      <header className="page-header">
        <p className="page-kicker">Dashboard</p>
        <h2>Welcome back, {user?.name || "Developer"}.</h2>
        <p>Track progress and launch your next topic-focused quiz.</p>
      </header>

      {error ? <div className="form-error">{error}</div> : null}

      <section className="summary-strip">
        <article className="metric-item">
          <span className="metric-label">Attempts</span>
          <strong className="metric-value">{attempts}</strong>
        </article>
        <article className="metric-item">
          <span className="metric-label">Questions solved</span>
          <strong className="metric-value">{totalQuestions}</strong>
        </article>
        <article className="metric-item">
          <span className="metric-label">Average score</span>
          <strong className="metric-value">{avgScore}%</strong>
        </article>
        <article className="metric-item">
          <span className="metric-label">Focus domain</span>
          <strong className="metric-value">{user?.focusDomain || "Not set"}</strong>
        </article>
      </section>

      <section className="dashboard-grid">
        <article className="glass-card panel-card">
          <p className="page-kicker">Next action</p>
          <h3>Start an adaptive quiz set</h3>
          <p>Pick topic and difficulty. The quiz is generated using your selected domain.</p>
          <Link className="btn btn-primary" to="/quiz-config">
            Configure quiz
          </Link>
        </article>

        <article className="glass-card insights-card">
          <p className="page-kicker">Recommended roles</p>
          <h3>Role direction from your focus area</h3>
          <ul className="insight-list">
            {(playbook.roles || []).map((role) => (
              <li key={role}>{role}</li>
            ))}
          </ul>
        </article>

        <article className="glass-card chart-card">
          <p className="page-kicker">Weak areas</p>
          <h3>Suggested topics to revisit</h3>
          <ul className="insight-list">
            {(recommendations?.weakAreas?.length ? recommendations.weakAreas : playbook.materials).map((item) => (
              <li key={item}>{item}</li>
            ))}
          </ul>
        </article>
      </section>
    </div>
  );
}
