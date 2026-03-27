const SESSION_KEY = "authSession";

export function readSession() {
  const raw = localStorage.getItem(SESSION_KEY);
  if (!raw) {
    return null;
  }

  try {
    return JSON.parse(raw);
  } catch {
    clearSession();
    return null;
  }
}

export function persistSession(payload) {
  const session = {
    token: payload.accessToken,
    user: {
      id: payload.userId,
      name: payload.name,
      email: payload.email,
      focusDomain: payload.focusDomain || "",
      targetRole: payload.targetRole || "",
      profileCompleted: Boolean(payload.profileCompleted)
    }
  };

  localStorage.setItem("token", session.token);
  localStorage.setItem("userId", String(session.user.id));
  localStorage.setItem("userName", session.user.name);
  localStorage.setItem(SESSION_KEY, JSON.stringify(session));
  return session;
}

export function persistProfile(profile) {
  const current = readSession();
  if (!current) {
    return null;
  }

  const nextSession = {
    ...current,
    user: {
      ...current.user,
      ...profile,
      profileCompleted: Boolean(profile.profileCompleted)
    }
  };

  localStorage.setItem("userName", nextSession.user.name || "");
  localStorage.setItem(SESSION_KEY, JSON.stringify(nextSession));
  return nextSession;
}

export function clearSession() {
  localStorage.removeItem("token");
  localStorage.removeItem("userId");
  localStorage.removeItem("userName");
  localStorage.removeItem(SESSION_KEY);
  sessionStorage.removeItem("activeQuiz");
  sessionStorage.removeItem("quizAnswers");
}
