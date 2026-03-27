import axiosClient from "./axiosClient";

export const generateQuiz = (payload) => axiosClient.post("/api/quizzes/generate", payload);
export const fetchQuizzes = () => axiosClient.get("/api/quizzes");
