export const domainPlaybooks = {
  Java: {
    roles: ["Backend Engineer", "Java Developer", "Platform Engineer"],
    materials: [
      "Review collections, concurrency, JVM memory, and exception design.",
      "Solve 2 medium DSA problems focused on hashing and trees.",
      "Write one service using clean layering and unit tests."
    ]
  },
  "Spring Boot": {
    roles: ["Spring Boot Developer", "Backend Engineer", "API Engineer"],
    materials: [
      "Practice REST validation, exception handling, and Spring Security basics.",
      "Build one CRUD API with DTO mapping and pagination.",
      "Revise JPA relationships, transactions, and query performance."
    ]
  },
  Microservices: {
    roles: ["Microservices Engineer", "Platform Engineer", "Backend Architect"],
    materials: [
      "Study API gateway, service discovery, fault tolerance, and observability.",
      "Explain eventual consistency and idempotency in your own words.",
      "Design one service interaction using async retry and fallback thinking."
    ]
  },
  AWS: {
    roles: ["Cloud Engineer", "Solutions Architect", "DevOps Engineer"],
    materials: [
      "Revise IAM, VPC basics, compute choices, and storage tradeoffs.",
      "Compare EC2, ECS, and Lambda for 3 sample workloads.",
      "Practice one architecture diagram with scaling and cost notes."
    ]
  },
  "AI/ML": {
    roles: ["ML Engineer", "Applied AI Engineer", "Data Scientist"],
    materials: [
      "Revise supervised vs unsupervised learning, bias-variance, and evaluation metrics.",
      "Practice one end-to-end pipeline: data prep, training, validation, and deployment.",
      "Explain model drift and retraining strategy with a practical example."
    ]
  },
  "Generative AI": {
    roles: ["LLM Engineer", "AI Application Engineer", "Prompt Engineer"],
    materials: [
      "Study prompt design, retrieval augmentation, embeddings, and context management.",
      "Build one mini RAG app and evaluate hallucination/factuality tradeoffs.",
      "Practice guardrails, safety filters, and token-cost optimization."
    ]
  },
  Cybersecurity: {
    roles: ["Security Engineer", "Application Security Engineer", "Cloud Security Engineer"],
    materials: [
      "Review OWASP Top 10, threat modeling, and secure authentication patterns.",
      "Practice identifying vulnerabilities in auth flows and input validation paths.",
      "Prepare one incident response walkthrough from detection to containment."
    ]
  },
  "Testing/QA": {
    roles: ["QA Engineer", "SDET", "Automation Engineer"],
    materials: [
      "Practice test pyramid strategy: unit, integration, and E2E responsibilities.",
      "Design stable API/UI automation with deterministic test data.",
      "Review flaky test root causes and mitigation strategies."
    ]
  },
  "Data Engineering": {
    roles: ["Data Engineer", "Analytics Engineer", "Platform Data Engineer"],
    materials: [
      "Revise ETL/ELT patterns, orchestration, and schema evolution handling.",
      "Design one batch and one streaming pipeline with failure recovery.",
      "Study partitioning, data quality checks, and warehouse optimization."
    ]
  },
  "Data Science": {
    roles: ["Data Scientist", "Applied Scientist", "Product Data Scientist"],
    materials: [
      "Refresh statistics fundamentals, A/B testing, and metric interpretation.",
      "Practice feature engineering and model explainability techniques.",
      "Prepare one case-study style problem with business impact framing."
    ]
  },
  React: {
    roles: ["Frontend Engineer", "React Developer", "UI Engineer"],
    materials: [
      "Review component composition, state management, and rendering performance.",
      "Practice building form-heavy flows with robust validation and error states.",
      "Study bundle optimization and runtime profiling basics."
    ]
  },
  "Node.js": {
    roles: ["Backend Engineer", "Node.js Developer", "API Engineer"],
    materials: [
      "Revise async patterns, event loop behavior, and runtime diagnostics.",
      "Build one production-style API with validation, auth, and pagination.",
      "Practice debugging memory leaks and slow endpoints."
    ]
  },
  Python: {
    roles: ["Python Developer", "Backend Engineer", "Automation Engineer"],
    materials: [
      "Review language internals, common pitfalls, and performance basics.",
      "Practice one API or automation project with testing and logging.",
      "Prepare interview answers around concurrency and packaging."
    ]
  },
  SQL: {
    roles: ["Backend Engineer", "Data Analyst", "Data Engineer"],
    materials: [
      "Practice joins, aggregations, window functions, and query tuning.",
      "Review indexing tradeoffs and execution plan reading.",
      "Design normalized vs denormalized schemas for a sample workload."
    ]
  },
  Kubernetes: {
    roles: ["Platform Engineer", "DevOps Engineer", "SRE"],
    materials: [
      "Study deployments, services, probes, autoscaling, and rollout strategies.",
      "Practice debugging pods and networking issues in a cluster.",
      "Review resource limits, observability, and production hardening."
    ]
  },
  "Site Reliability": {
    roles: ["SRE", "Production Engineer", "Platform Reliability Engineer"],
    materials: [
      "Revise SLO/SLA/SLI concepts and alerting strategy design.",
      "Practice incident handling with timelines, impact, and postmortem actions.",
      "Study capacity planning and reliability-focused architectural tradeoffs."
    ]
  },
  DevOps: {
    roles: ["DevOps Engineer", "SRE", "Platform Engineer"],
    materials: [
      "Study CI/CD pipelines, Docker image hygiene, and Kubernetes basics.",
      "Set up a local pipeline with lint, test, and build stages.",
      "Review observability signals: logs, metrics, traces, and alerts."
    ]
  },
  "Data Structures": {
    roles: ["Software Engineer", "Algorithmic Problem Solver", "Backend Engineer"],
    materials: [
      "Focus on arrays, recursion, trees, graphs, heaps, and complexity tradeoffs.",
      "Time-box 3 interview problems and review the failed ones carefully.",
      "Explain your solution aloud before coding to build interview fluency."
    ]
  },
  "System Design": {
    roles: ["Software Engineer II", "Backend Engineer", "System Design Candidate"],
    materials: [
      "Practice load estimation, API design, caching, and queue tradeoffs.",
      "Design one scalable read-heavy system and one write-heavy system.",
      "Review consistency, partitioning, and failure scenarios."
    ]
  }
};

export function getPlaybook(domain) {
  return domainPlaybooks[domain] || {
    roles: ["Software Engineer", "Backend Engineer", "Full Stack Developer"],
    materials: [
      "Review core fundamentals and convert weak quiz topics into short study blocks.",
      "Practice one mock interview round with timed answers.",
      "Write one small project feature instead of only reading theory."
    ]
  };
}
