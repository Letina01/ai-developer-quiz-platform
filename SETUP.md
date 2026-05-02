# Local Development Setup Guide

## Prerequisites

1. **Java 17+** - Required for Spring Boot 3.x
2. **Node.js 18+** - For frontend development
3. **MySQL 8.0+** - Database
4. **Maven 3.9+** - For backend build

## Quick Start

### Option 1: Using Docker (Recommended)

```bash
# Start all services
docker compose up --build -d

# Check logs
docker compose logs -f
```

### Option 2: Manual Start (Without Docker)

#### Step 1: Setup MySQL Database

```sql
CREATE DATABASE IF NOT EXISTS ai_dev_quiz;
```

#### Step 2: Set Environment Variables

Create a `.env` file in the project root:

```env
# Database
DB_URL=jdbc:mysql://localhost:3306/ai_dev_quiz
DB_USERNAME=root
DB_PASSWORD=your_password

# JWT Secret (use a strong 256-bit key)
JWT_SECRET=your_super_secure_secret_key_at_least_256_bits_long

# AI Service (Groq API)
GROQ_API_KEY=your_groq_api_key
GROQ_BASE_URL=https://api.groq.com/openai
GROQ_MODEL=llama-3.1-8b-instant

# Services URLs
AUTH_SERVICE_URI=http://localhost:8081
QUIZ_SERVICE_URI=http://localhost:8082
AI_SERVICE_URI=http://localhost:8083
RESULT_SERVICE_URI=http://localhost:8084
RECOMMENDATION_SERVICE_URI=http://localhost:8085
EMAIL_SERVICE_URI=http://localhost:8086
```

#### Step 3: Start Backend Services (in order)

```bash
# 1. Start Service Registry (Eureka)
cd backend/service-registry
mvn spring-boot:run

# 2. Start Auth Service
cd backend/auth-service
mvn spring-boot:run

# 3. Start AI Service
cd backend/ai-service
mvn spring-boot:run

# 4. Start Quiz Service
cd backend/quiz-service
mvn spring-boot:run

# 5. Start Result Service
cd backend/result-service
mvn spring-boot:run

# 6. Start Recommendation Service
cd backend/recommendation-service
mvn spring-boot:run

# 7. Start API Gateway
cd backend/api-gateway
mvn spring-boot:run
```

#### Step 4: Start Frontend

```bash
cd frontend
npm install
npm run dev
```

## Access Points

| Service | URL |
|---------|-----|
| Frontend | http://localhost:5173 |
| API Gateway | http://localhost:8080 |
| Service Registry (Eureka) | http://localhost:8761 |
| Auth Service | http://localhost:8081 |
| Quiz Service | http://localhost:8082 |
| AI Service | http://localhost:8083 |
| Result Service | http://localhost:8084 |
| Recommendation Service | http://localhost:8085 |

## API Documentation (Swagger)

Once services are running, access Swagger UI:

| Service | Swagger URL |
|---------|-------------|
| Auth Service | http://localhost:8081/swagger-ui.html |
| Quiz Service | http://localhost:8082/swagger-ui.html |
| Result Service | http://localhost:8084/swagger-ui.html |
| Recommendation Service | http://localhost:8085/swagger-ui.html |
| AI Service | http://localhost:8083/swagger-ui.html |

## Running Tests

```bash
# Run all tests
cd backend
mvn test

# Run tests for specific service
cd backend/auth-service
mvn test
```

## Common Issues

### 1. Database Connection Error

```
Access denied for user 'root'@'localhost'
```

**Solution**: Check your MySQL credentials in `.env` file and ensure the database exists.

### 2. Eureka Connection Refused

```
Connect to http://localhost:8761 failed
```

**Solution**: Ensure Service Registry is running before other services.

### 3. Port Already in Use

```
Port 8080 is already in use
```

**Solution**: Kill the process using the port or change the port in `application.yml`.

### 4. JWT Token Issues

If you get 401 Unauthorized errors, ensure:
- JWT_SECRET is set correctly
- Auth service is running
- Token is being sent in Authorization header

## Logs

All services write logs to:
- Console (development mode)
- `./logs/{service-name}.log` files

## Performance Tips

1. Use Docker for consistent environments
2. Enable connection pooling (HikariCP) - already configured
3. Use Redis for caching (optional, in docker-compose)
4. Increase JVM heap for production:
   ```bash
   export JAVA_OPTS="-Xmx2g -Xms512m"
   ```
