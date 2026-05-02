import { useEffect } from "react";
import { Navigate, useNavigate, useSearchParams } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

function parseHashParams(hashValue) {
  const hash = hashValue.startsWith("#") ? hashValue.slice(1) : hashValue;
  return new URLSearchParams(hash);
}

export default function OAuthCallbackPage() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { acceptExternalSession, isAuthenticated } = useAuth();

  useEffect(() => {
    const hashParams = parseHashParams(window.location.hash || "");
    const params = hashParams.get("token") ? hashParams : searchParams;
    const token = params.get("token");
    const userId = params.get("userId");

    if (!token || !userId) {
      navigate("/login?oauthError=callback_failed", { replace: true });
      return;
    }

    acceptExternalSession({
      accessToken: token,
      userId: Number(userId),
      name: params.get("name") || "Developer",
      email: params.get("email") || "",
      profileCompleted: params.get("profileCompleted") === "true",
      focusDomain: params.get("focusDomain") || "",
      targetRole: params.get("targetRole") || "",
      authProvider: params.get("authProvider") || "GOOGLE"
    });

    navigate(params.get("profileCompleted") === "true" ? "/dashboard" : "/profile", { replace: true });
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
