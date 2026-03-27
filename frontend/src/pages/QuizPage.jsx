import { useMemo, useState } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import { submitResult } from "../api/resultApi";
import { useAuth } from "../hooks/useAuth";

function getOptionLabel(index) {
  return ["A", "B", "C", "D"][index] || "A";
}

export default function QuizPage() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const activeQuiz = useMemo(() => {
    const raw = sessionStorage.getItem("activeQuiz");
    return raw ? JSON.parse(raw) : null;
  }, []);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answers, setAnswers] = useState(() => {
    const raw = sessionStorage.getItem("quizAnswers");
    return raw ? JSON.parse(raw) : {};
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");

  if (!activeQuiz?.questions?.length) {
    return <Navigate to="/quiz-config" replace />;
  }

  const question = activeQuiz.questions[currentIndex];
  const totalQuestions = activeQuiz.questions.length;
  const selectedAnswer = answers[currentIndex] || "";
  const isLastQuestion = currentIndex === totalQuestions - 1;

  const options = [question.optionA, question.optionB, question.optionC, question.optionD].filter(Boolean);

  const selectAnswer = (option) => {
    const nextAnswers = { ...answers, [currentIndex]: option };
    setAnswers(nextAnswers);
    sessionStorage.setItem("quizAnswers", JSON.stringify(nextAnswers));
  };

  const handleNext = async () => {
    if (!selectedAnswer) {
      setError("Please pick an option before moving ahead.");
      return;
    }
    setError("");

    if (!isLastQuestion) {
      setCurrentIndex((value) => value + 1);
      return;
    }

    setIsSubmitting(true);
    try {
      const score = activeQuiz.questions.reduce((total, item, index) => {
        return total + (answers[index] === item.correctAnswer ? 1 : 0);
      }, 0);

      await submitResult({
        userId: user.id,
        quizId: activeQuiz.id,
        domain: activeQuiz.domain,
        topic: activeQuiz.topic,
        difficulty: activeQuiz.difficulty,
        score,
        totalQuestions
      });

      const review = activeQuiz.questions.map((item, index) => ({
        question: item.question,
        selected: answers[index] || "",
        correctAnswer: item.correctAnswer,
        explanation: item.explanation || "",
        isCorrect: answers[index] === item.correctAnswer
      }));

      sessionStorage.setItem(
        "lastQuizResult",
        JSON.stringify({
          score,
          totalQuestions,
          domain: activeQuiz.domain,
          topic: activeQuiz.topic,
          difficulty: activeQuiz.difficulty,
          review
        })
      );
      sessionStorage.removeItem("activeQuiz");
      sessionStorage.removeItem("quizAnswers");
      navigate("/results");
    } catch (requestError) {
      setError(requestError.response?.data?.message || "Failed to submit result. Please retry.");
    } finally {
      setIsSubmitting(false);
    }
  };

  const progress = Math.round(((currentIndex + 1) / totalQuestions) * 100);

  return (
    <div className="quiz-container">
      <div className="glass-card quiz-card">
        <div className="quiz-progress-bar">
          <div className="quiz-progress-fill" style={{ width: `${progress}%` }} />
        </div>

        <div className="quiz-header">
          <span className="quiz-meta-badge">
            {activeQuiz.domain} • {activeQuiz.difficulty}
          </span>
          <p className="question-counter">
            Question <span>{currentIndex + 1}</span> of {totalQuestions}
          </p>
        </div>

        <h3 className="question-text">{question.question}</h3>

        <div className="option-grid">
          {options.map((option, index) => (
            <button
              key={option}
              type="button"
              className={`option-btn ${selectedAnswer === option ? "selected" : ""}`}
              onClick={() => selectAnswer(option)}
            >
              <span className="option-indicator" />
              {getOptionLabel(index)}. {option}
            </button>
          ))}
        </div>

        {error ? <div className="form-error">{error}</div> : null}

        <div className="quiz-footer">
          <button type="button" className="btn btn-primary" onClick={handleNext} disabled={isSubmitting}>
            {isSubmitting ? "Submitting..." : isLastQuestion ? "Submit quiz" : "Next question"}
          </button>
        </div>
      </div>
    </div>
  );
}
