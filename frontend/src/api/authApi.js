import axiosClient from "./axiosClient";

export const login = (payload) => axiosClient.post("/api/auth/login", payload);
export const register = (payload) => axiosClient.post("/api/auth/register", payload);
export const fetchProfile = () => axiosClient.get("/api/auth/me");
export const updateProfile = (payload) => axiosClient.put("/api/auth/profile", payload);
