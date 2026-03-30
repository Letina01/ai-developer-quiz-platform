# AI Developer Quiz Platform

Production-oriented microservices scaffold for an AI-powered developer quiz platform.

## Architecture

### Services

- `service-registry`: Eureka discovery server
- `api-gateway`: single entry point, routing, CORS, JWT resource-server enforcement
- `auth-service`: registration, login, JWT issuance, user lookup
- `quiz-service`: quiz lifecycle, question persistence, AI orchestration
- `ai-service`: Spring AI integration for dynamic question generation through OpenAI or Ollama
- `result-service`: stores quiz attempts and scores
- `recommendation-service`: derives weak areas and next recommended quizzes
- `frontend`: React dashboard application

### Communication

- Client -> `api-gateway`
- `api-gateway` -> downstream services through service discovery
- `quiz-service` -> `ai-service` through OpenFeign
- `recommendation-service` -> `result-service` through OpenFeign

### Security Model

- `auth-service` issues JWT access tokens
- `api-gateway` validates JWT for protected routes
- Public routes:
  - `POST /api/auth/register`
  - `POST /api/auth/login`
  - `GET /actuator/health`

### Storage

- MySQL for `users`, `quizzes`, `questions`, and `results`
- Redis optional for caching
- Kafka optional for events

## Folder Structure

```text
ai-developer-quiz-platform/
  backend/
    pom.xml
    service-registry/
    api-gateway/
    auth-service/
    quiz-service/
    ai-service/
    result-service/
    recommendation-service/
  frontend/
```

## Core APIs

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `POST /api/quizzes/generate`
- `GET /api/quizzes`
- `GET /api/quizzes/{quizId}`
- `POST /api/ai/quizzes/generate`
- `POST /api/results`
- `GET /api/results/users/{userId}`
- `GET /api/recommendations/users/{userId}`

## Example Quiz Flow

1. User logs in and receives JWT.
2. Frontend calls `POST /api/quizzes/generate`.
3. `quiz-service` calls `ai-service`.
4. `ai-service` uses Spring AI to generate quiz JSON.
5. `quiz-service` persists quiz and questions.
6. Frontend renders the quiz.
7. Frontend submits the result to `result-service`.
8. Frontend loads recommendations from `recommendation-service`.

## Environment Variables

```text
DB_URL=jdbc:mysql://localhost:3306/ai_dev_quiz
DB_USERNAME=root
DB_PASSWORD=change-me
JWT_SECRET=replace-with-strong-256-bit-secret
OPENAI_API_KEY=
OLLAMA_BASE_URL=http://localhost:11434
```

## Projects

### AI Developer Quiz Platform
- Dynamic AI-powered quiz generation using Spring AI and OpenAI/Ollama integration
- Microservices architecture with service discovery, API gateway, and JWT security
- Comprehensive learning analytics with quiz results tracking and personalized recommendations

## Docker Local Run

From project root:

```bash
docker compose up --build -d
```

Apps:
- Frontend: `http://localhost:5173`
- API Gateway: `http://localhost:8080`
- Eureka: `http://localhost:8761`

## GitHub Actions CI/CD

Workflow file: `.github/workflows/cicd.yml`

What it does:
- Build backend (`mvn package`) and frontend (`npm run build`)
- Build Docker images for all services
- Push images to GHCR:
  - `ghcr.io/<owner>/ai-quiz-service-registry`
  - `ghcr.io/<owner>/ai-quiz-api-gateway`
  - `ghcr.io/<owner>/ai-quiz-auth-service`
  - `ghcr.io/<owner>/ai-quiz-quiz-service`
  - `ghcr.io/<owner>/ai-quiz-ai-service`
  - `ghcr.io/<owner>/ai-quiz-result-service`
  - `ghcr.io/<owner>/ai-quiz-recommendation-service`
  - `ghcr.io/<owner>/ai-quiz-frontend`

### Required GitHub Secrets

Add these in repo settings:

```text
DEPLOY_HOST=
DEPLOY_USER=
DEPLOY_SSH_KEY=
DEPLOY_PORT=22
DEPLOY_PATH=/opt/ai-developer-quiz-platform
```

### Server Setup (One-time)

On your server:

1. Install Docker + Docker Compose plugin.
2. Create deploy folder, e.g. `/opt/ai-developer-quiz-platform`.
3. Place `docker-compose.prod.yml` and `.env` there.
4. Start stack:

```bash
docker compose -f docker-compose.prod.yml --env-file .env up -d
```

Use `.env.prod.example` as template for production `.env`.
