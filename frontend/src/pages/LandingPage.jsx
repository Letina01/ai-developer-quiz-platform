import { Link, Navigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

export default function LandingPage() {
  const { isAuthenticated, user } = useAuth();

  if (isAuthenticated) {
    return <Navigate to={user?.profileCompleted ? "/dashboard" : "/profile"} replace />;
  }

  return (
    <div className="landing-page">
      <section className="glass-card landing-hero">
        <div className="hero-copy">
          <span className="hero-badge">AI Interview Practice</span>
          <h1 className="hero-title">
            Build a focused prep plan for your <span className="text-primary">next developer role</span>.
          </h1>
          <p className="hero-subtitle">
            Generate topic-wise quizzes, track performance, and get recommendation-driven study guidance.
          </p>
          <div className="hero-actions">
            <Link to="/register" className="btn btn-primary">
              Create account
            </Link>
            <Link to="/login" className="btn btn-secondary">
              Sign in
            </Link>
          </div>
        </div>
        <div className="glass-card hero-panel">
          <div className="hero-panel-row">
            <span>Step 1</span>
            <strong>Set your profile</strong>
            <p>Pick your domain, role, and current level.</p>
          </div>
          <div className="hero-panel-row">
            <span>Step 2</span>
            <strong>Practice adaptive quizzes</strong>
            <p>Generate quiz sets by topic and difficulty.</p>
          </div>
          <div className="hero-panel-row">
            <span>Step 3</span>
            <strong>Review weak areas</strong>
            <p>Use score trends and recommendations for next sessions.</p>
          </div>
        </div>
      </section>
    </div>
  );
}
