import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

const navItems = [
  { label: "Dashboard", to: "/dashboard" },
  { label: "Profile", to: "/profile" },
  { label: "Start Quiz", to: "/quiz-config" },
  { label: "Results", to: "/results" },
  { label: "Analytics", to: "/analytics" }
];

export default function AppLayout() {
  const navigate = useNavigate();
  const { logout, user } = useAuth();

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="sidebar-top">
          <p className="sidebar-eyebrow">AI Quiz</p>
          <h1 className="sidebar-title">PrepFlow</h1>
          <p className="sidebar-copy">
            Personalized interview practice for {user?.targetRole || "your next role"}.
          </p>
        </div>
        <nav className="sidebar-nav">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) => `sidebar-link ${isActive ? "active" : ""}`}
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-profile glass-card">
          <p className="metric-label">Profile status</p>
          <strong>{user?.profileCompleted ? "Ready for guided practice" : "Complete profile first"}</strong>
          <button
            type="button"
            className="btn btn-secondary sidebar-logout"
            onClick={() => {
              logout();
              navigate("/");
            }}
          >
            Logout
          </button>
        </div>
      </aside>
      <main className="main-panel">
        <Outlet />
      </main>
    </div>
  );
}
