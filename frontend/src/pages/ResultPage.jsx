import { Link } from "react-router-dom";

export default function ResultPage() {
  const raw = sessionStorage.getItem("lastQuizResult");
  let result = null;

  if (raw) {
    try {
      result = JSON.parse(raw);
    } catch {
      result = null;
    }
  }

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

  const percentage = result.totalQuestions ? Math.round((result.score / result.totalQuestions) * 100) : 0;
  const review = Array.isArray(result.review) ? result.review : [];

  return (
    <div className="dashboard-stack">
      <header className="page-header">
        <p className="page-kicker">Quiz result</p>
        <h2>
          {result.topic} | {result.domain}
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
        {review.map((item, index) => (
          <article key={`${item.question}-${index}`} className="glass-card result-card">
            <p className="page-kicker">Question {index + 1}</p>
            <h4>{item.question}</h4>
            <p>
              Your answer: <strong className={item.correct ? "text-success" : "text-error"}>{item.selectedAnswer || "Not answered"}</strong>
              {item.correct ? <span className="text-success"> Correct</span> : <span className="text-error"> Wrong</span>}
            </p>
            {!item.correct && (
              <p>
                Correct answer: <strong className="text-success">{item.correctAnswer}</strong>
              </p>
            )}
            {item.explanation ? (
              <div className="explanation-box">
                <strong>Explanation:</strong>
                <p>{item.explanation}</p>
              </div>
            ) : null}
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
