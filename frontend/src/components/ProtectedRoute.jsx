import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

export default function ProtectedRoute({ children, requireCompletedProfile = false }) {
  const { isAuthenticated, isBootstrapping, user } = useAuth();
  const location = useLocation();

  if (isBootstrapping) {
    return (
      <div className="page-state">
        <div className="glass-card state-card">
          <p className="page-kicker">Loading workspace</p>
          <h2>Syncing your account and latest progress.</h2>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  if (requireCompletedProfile && !user?.profileCompleted) {
    return <Navigate to="/profile" replace />;
  }

  return children;
}
