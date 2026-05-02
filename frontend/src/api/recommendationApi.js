import axiosClient from "./axiosClient";

export const fetchRecommendations = () =>
  axiosClient.get("/recommendations/users/me");
