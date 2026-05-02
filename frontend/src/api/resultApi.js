import axiosClient from "./axiosClient";

export const submitResult = (payload) => axiosClient.post("/results", payload);
export const fetchResults = () => axiosClient.get("/results/users/me");
