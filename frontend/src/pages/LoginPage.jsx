import { useState } from "react";
import { Link, Navigate, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

export default function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login, isAuthenticated, user } = useAuth();
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  if (isAuthenticated) {
    return <Navigate to={user?.profileCompleted ? "/dashboard" : "/profile"} replace />;
  }

  const handleSubmit = async (event) => {
    event.preventDefault();
    setIsSubmitting(true);
    setError("");

    try {
      const session = await login(form);
      const nextRoute = location.state?.from?.pathname;
      navigate(session.user.profileCompleted ? nextRoute || "/dashboard" : "/profile");
    } catch (requestError) {
      setError(requestError.response?.data?.message || "Login failed. Check your credentials and try again.");
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleGoogleLogin = () => {
    const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
    window.location.href = `${apiBaseUrl}/oauth2/authorization/google`;
  };

  return (
    <div className="auth-page">
      <form className="glass-card auth-card" onSubmit={handleSubmit}>
        <p className="page-kicker">Welcome back</p>
        <h2>Sign in to continue your prep plan.</h2>
        <p className="auth-helper">
          Your dashboard, quiz history, and recommendations will be restored after login.
        </p>
        <input
          className="form-control"
          placeholder="Email"
          value={form.email}
          onChange={(event) => setForm({ ...form, email: event.target.value })}
        />
        <input
          className="form-control"
          type="password"
          placeholder="Password"
          value={form.password}
          onChange={(event) => setForm({ ...form, password: event.target.value })}
        />
        {error ? <div className="form-error">{error}</div> : null}
        <button className="btn btn-primary w-100" type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Signing in..." : "Sign in"}
        </button>

        <div className="auth-separator">
          <span>OR</span>
        </div>

        <button
          type="button"
          className="btn btn-outline w-100 google-btn"
          onClick={handleGoogleLogin}
          disabled={isSubmitting}
        >
          <img
            src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg"
            alt="Google"
            className="google-icon"
          />
          Sign in with Google
        </button>

        <p className="auth-helper">
          New here? <Link to="/register">Create an account</Link>
        </p>
      </form>
    </div>
  );
}
