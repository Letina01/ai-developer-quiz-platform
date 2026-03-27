import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { generateQuiz } from "../api/quizApi";
import { domains } from "../data/domains";
import { useAuth } from "../hooks/useAuth";

const difficulties = ["Easy", "Medium", "Hard"];

export default function QuizConfigPage() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [form, setForm] = useState({
    domain: user?.focusDomain || domains[0]?.title || "Java",
    topic: "",
    difficulty: "Medium",
    numberOfQuestions: 5
  });
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleChange = (key) => (event) => {
    const value = key === "numberOfQuestions" ? Number(event.target.value) : event.target.value;
    setForm((previous) => ({ ...previous, [key]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setIsSubmitting(true);

    try {
      const response = await generateQuiz(form);
      sessionStorage.setItem("activeQuiz", JSON.stringify(response.data));
      sessionStorage.removeItem("quizAnswers");
      navigate("/quiz");
    } catch (requestError) {
      setError(requestError.response?.data?.message || "Quiz generation failed. Please retry.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="dashboard-stack">
      <header className="page-header">
        <p className="page-kicker">Quiz setup</p>
        <h2>Create your next practice set.</h2>
      </header>

      <form className="glass-card form-card config-form" onSubmit={handleSubmit}>
        <label>
          Domain
          <select className="form-select" value={form.domain} onChange={handleChange("domain")}>
            {domains.map((item) => (
              <option key={item.title} value={item.title}>
                {item.title}
              </option>
            ))}
          </select>
        </label>

        <label>
          Topic
          <input
            className="form-control"
            placeholder="Example: JWT security, JPA joins, caching strategies"
            value={form.topic}
            onChange={handleChange("topic")}
            required
          />
        </label>

        <label>
          Difficulty
          <select className="form-select" value={form.difficulty} onChange={handleChange("difficulty")}>
            {difficulties.map((difficulty) => (
              <option key={difficulty} value={difficulty}>
                {difficulty}
              </option>
            ))}
          </select>
        </label>

        <label>
          Number of questions
          <input
            className="form-control"
            type="number"
            min={1}
            max={20}
            value={form.numberOfQuestions}
            onChange={handleChange("numberOfQuestions")}
          />
        </label>

        {error ? <div className="form-error">{error}</div> : null}

        <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
          {isSubmitting ? "Generating..." : "Start quiz"}
        </button>
      </form>
    </div>
  );
}
