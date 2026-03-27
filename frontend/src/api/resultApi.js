import axiosClient from "./axiosClient";

export const submitResult = (payload) => axiosClient.post("/api/results", payload);
export const fetchResults = (userId) => axiosClient.get(`/api/results/users/${userId}`);
