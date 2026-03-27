import { Link } from "react-router-dom";

export default function ResultPage() {
  const raw = sessionStorage.getItem("lastQuizResult");
  const result = raw ? JSON.parse(raw) : null;

  if (!result) {
    return (
      <div className="dashboard-stack">
        <header className="page-header">
          <p className="page-kicker">Results</p>
          <h2>No recent quiz result found.</h2>
        </header>
        <Link className="btn btn-primary" to="/quiz-config">
          Start a quiz
        </Link>
      </div>
    );
  }

  const percentage = Math.round((result.score / result.totalQuestions) * 100);

  return (
    <div className="dashboard-stack">
      <header className="page-header">
        <p className="page-kicker">Quiz result</p>
        <h2>
          {result.topic} • {result.domain}
        </h2>
        <p>
          Score: {result.score}/{result.totalQuestions} ({percentage}%)
        </p>
      </header>

      <section className="summary-strip">
        <article className="metric-item">
          <span className="metric-label">Difficulty</span>
          <strong className="metric-value">{result.difficulty}</strong>
        </article>
        <article className="metric-item">
          <span className="metric-label">Correct</span>
          <strong className="metric-value">{result.score}</strong>
        </article>
        <article className="metric-item">
          <span className="metric-label">Incorrect</span>
          <strong className="metric-value">{result.totalQuestions - result.score}</strong>
        </article>
        <article className="metric-item">
          <span className="metric-label">Accuracy</span>
          <strong className="metric-value">{percentage}%</strong>
        </article>
      </section>

      <section>
        {result.review.map((item, index) => (
          <article
            key={`${item.question}-${index}`}
            className={`glass-card result-card ${item.isCorrect ? "correct-border" : "wrong-border"}`}
          >
            <p className="page-kicker">Question {index + 1}</p>
            <h4>{item.question}</h4>
            <p>
              Your answer: <strong>{item.selected || "Not answered"}</strong>
            </p>
            <p>
              Correct answer: <strong>{item.correctAnswer}</strong>
            </p>
            {item.explanation ? <p>{item.explanation}</p> : null}
          </article>
        ))}
      </section>

      <div className="inline-actions">
        <Link className="btn btn-secondary" to="/quiz-config">
          Practice again
        </Link>
        <Link className="btn btn-primary" to="/analytics">
          View analytics
        </Link>
      </div>
    </div>
  );
}
