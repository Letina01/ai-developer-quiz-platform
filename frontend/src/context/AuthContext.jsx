import { createContext, useEffect, useState } from "react";
import { fetchProfile, login as loginRequest, register as registerRequest, updateProfile as updateProfileRequest } from "../api/authApi";
import { clearSession, persistProfile, persistSession, readSession } from "../utils/session";

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [session, setSession] = useState(() => readSession());
  const [isBootstrapping, setIsBootstrapping] = useState(true);

  useEffect(() => {
    const existingSession = readSession();
    if (!existingSession?.token) {
      setIsBootstrapping(false);
      return;
    }

    fetchProfile()
      .then((response) => {
        const nextSession = persistProfile(mapProfile(response.data));
        setSession(nextSession);
      })
      .catch(() => {
        clearSession();
        setSession(null);
      })
      .finally(() => setIsBootstrapping(false));
  }, []);

  const handleAuthResponse = (payload) => {
    const nextSession = persistSession(payload);
    setSession(nextSession);
    return nextSession;
  };

  const acceptExternalSession = (payload) => handleAuthResponse(payload);

  const login = async (payload) => {
    const response = await loginRequest(payload);
    return handleAuthResponse(response.data);
  };

  const register = async (payload) => {
    const response = await registerRequest(payload);
    return handleAuthResponse(response.data);
  };

  const refreshProfile = async () => {
    const response = await fetchProfile();
    const nextSession = persistProfile(mapProfile(response.data));
    setSession(nextSession);
    return response.data;
  };

  const updateProfile = async (payload) => {
    const response = await updateProfileRequest(payload);
    const nextSession = persistProfile(mapProfile(response.data));
    setSession(nextSession);
    return response.data;
  };

  const logout = () => {
    clearSession();
    setSession(null);
  };

  return (
    <AuthContext.Provider
      value={{
        token: session?.token || null,
        user: session?.user || null,
        isAuthenticated: Boolean(session?.token),
        isBootstrapping,
        login,
        register,
        acceptExternalSession,
        refreshProfile,
        updateProfile,
        logout
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

function mapProfile(profile) {
  return {
    id: profile.id,
    name: profile.name,
    email: profile.email,
    authProvider: profile.authProvider,
    focusDomain: profile.focusDomain || "",
    targetRole: profile.targetRole || "",
    experienceLevel: profile.experienceLevel || "",
    currentSkills: profile.currentSkills || "",
    studyGoal: profile.studyGoal || "",
    profileCompleted: profile.profileCompleted
  };
}
