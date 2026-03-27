import axiosClient from "./axiosClient";

export const fetchRecommendations = (userId) =>
  axiosClient.get(`/api/recommendations/users/${userId}`);
