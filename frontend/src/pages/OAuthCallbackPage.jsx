import { useEffect } from "react";
import { Navigate, useNavigate, useSearchParams } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

export default function OAuthCallbackPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { acceptExternalSession, isAuthenticated } = useAuth();

  useEffect(() => {
    const token = searchParams.get("token");
    const userId = searchParams.get("userId");

    if (!token || !userId) {
      navigate("/login?oauthError=callback_failed", { replace: true });
      return;
    }

    acceptExternalSession({
      accessToken: token,
      userId: Number(userId),
      name: searchParams.get("name") || "Developer",
      email: searchParams.get("email") || "",
      profileCompleted: searchParams.get("profileCompleted") === "true",
      focusDomain: searchParams.get("focusDomain") || "",
      targetRole: searchParams.get("targetRole") || "",
      authProvider: searchParams.get("authProvider") || "GOOGLE"
    });

    navigate(searchParams.get("profileCompleted") === "true" ? "/dashboard" : "/profile", { replace: true });
  }, [acceptExternalSession, navigate, searchParams]);

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  return (
    <div className="page-state">
      <div className="glass-card state-card">
        <p className="page-kicker">Completing sign-in</p>
        <h2>Finishing your Google login and loading your workspace.</h2>
      </div>
    </div>
  );
}
