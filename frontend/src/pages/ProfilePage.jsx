import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { domains } from "../data/domains";
import { useAuth } from "../hooks/useAuth";

const levels = ["Beginner", "Intermediate", "Advanced"];

export default function ProfilePage() {
  const navigate = useNavigate();
  const { user, updateProfile } = useAuth();
  const [form, setForm] = useState({
    name: "",
    focusDomain: "",
    targetRole: "",
    experienceLevel: "Intermediate",
    currentSkills: "",
    studyGoal: ""
  });
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (!user) {
      return;
    }

    setForm({
      name: user.name || "",
      focusDomain: user.focusDomain || "",
      targetRole: user.targetRole || "",
      experienceLevel: user.experienceLevel || "Intermediate",
      currentSkills: user.currentSkills || "",
      studyGoal: user.studyGoal || ""
    });
  }, [user]);

  const handleChange = (key) => (event) => {
    setForm((previous) => ({ ...previous, [key]: event.target.value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setIsSubmitting(true);

    if (form.currentSkills.trim().length < 10) {
      setError("Current skills must be at least 10 characters.");
      setIsSubmitting(false);
      return;
    }

    if (form.studyGoal.trim().length < 10) {
      setError("Study goal must be at least 10 characters.");
      setIsSubmitting(false);
      return;
    }

    try {
      await updateProfile(form);
      navigate("/dashboard");
    } catch (requestError) {
      const backendMessage =
        requestError.response?.data?.message ||
        requestError.response?.data?.error ||
        requestError.response?.data?.detail;
      setError(backendMessage || "Unable to save profile. Please check all fields and try again.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="dashboard-stack">
      <header className="page-header">
        <p className="page-kicker">Your preparation profile</p>
        <h2>Tell us your current focus so quiz generation can stay targeted.</h2>
      </header>

      <form className="glass-card form-card profile-form" onSubmit={handleSubmit}>
        <label>
          Full name
          <input
            className="form-control"
            placeholder="Your name"
            value={form.name}
            onChange={handleChange("name")}
            required
          />
        </label>

        <label>
          Focus domain
          <select className="form-select" value={form.focusDomain} onChange={handleChange("focusDomain")}>
            <option value="">Select a domain</option>
            {domains.map((item) => (
              <option key={item.title} value={item.title}>
                {item.title}
              </option>
            ))}
          </select>
        </label>

        <label>
          Target role
          <input
            className="form-control"
            placeholder="Backend Engineer"
            value={form.targetRole}
            onChange={handleChange("targetRole")}
          />
        </label>

        <label>
          Experience level
          <select
            className="form-select"
            value={form.experienceLevel}
            onChange={handleChange("experienceLevel")}
          >
            {levels.map((level) => (
              <option key={level} value={level}>
                {level}
              </option>
            ))}
          </select>
        </label>

        <label>
          Current skills
          <textarea
            className="form-control form-textarea"
            placeholder="Spring Boot, Java 17, REST APIs, SQL"
            value={form.currentSkills}
            onChange={handleChange("currentSkills")}
            minLength={10}
            required
          />
        </label>

        <label>
          Study goal
          <textarea
            className="form-control form-textarea"
            placeholder="Crack backend interviews in 8 weeks"
            value={form.studyGoal}
            onChange={handleChange("studyGoal")}
            minLength={10}
            required
          />
        </label>

        {error ? <div className="form-error">{error}</div> : null}

        <button className="btn btn-primary" type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Saving profile..." : "Save and continue"}
        </button>
      </form>
    </div>
  );
}
