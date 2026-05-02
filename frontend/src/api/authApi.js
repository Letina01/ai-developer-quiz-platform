import axiosClient from "./axiosClient";

export const login = (payload) => axiosClient.post("/auth/login", payload);
export const register = (payload) => axiosClient.post("/auth/register", payload);
export const fetchProfile = () => axiosClient.get("/auth/me");
export const updateProfile = (payload) => axiosClient.put("/auth/profile", payload);
export const forgotPassword = (payload) =>
  axiosClient.post("/auth/forgot-password", payload);
