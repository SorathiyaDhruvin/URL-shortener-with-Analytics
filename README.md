# URL Shortener with Analytics

A production-quality URL Shortener with Analytics web application. This project converts long URLs into short, trackable codes and provides an interactive dashboard with rich analytics.

## Features

- **Custom URL Shortening:** Convert long URLs into short links.
- **Custom Aliases:** Create personalized short links (e.g., `/my-portfolio`).
- **Link Expiration:** Set expiration dates for short links.
- **QR Code Generation:** Automatically generate QR codes for your short links.
- **Rich Analytics:** Track clicks over time, geographic data, device types, operating systems, browsers, and referrers.
- **Role-Based Access Control:** Secure authentication and authorization with JWT.
- **Modern Dashboard:** A responsive, polished UI built with React and Tailwind CSS.

## Architecture

- **Frontend:** React, TypeScript, Tailwind CSS, Recharts
- **Backend:** Java, Spring Boot, Spring Data JPA, Spring Security (JWT)
- **Database:** PostgreSQL (for relational data storage)
- **Caching:** Redis (for fast short-link resolution)
- **API Documentation:** Swagger / OpenAPI

### URL Shortening Algorithm

The application uses **Base62 Encoding**. A unique sequential ID from the database is converted into a Base62 string (using characters `A-Z, a-z, 0-9`), guaranteeing collision-free, short, and unique identifiers.

### Analytics

Click events are tracked asynchronously to avoid slowing down the redirect process. Important details such as IP hash, User Agent (Browser, OS, Device Type), and Referrer are stored. The data is then aggregated into daily statistics and presented in the Analytics dashboard via Recharts.

### Caching

When a short code is accessed, the application first checks the Redis cache. If found, it immediately redirects the user. If not, it fetches the URL from PostgreSQL, updates the cache, and then redirects. Cache invalidation happens whenever a URL is edited or disabled.

### Authentication

The backend is secured using Spring Security and JSON Web Tokens (JWT). Passwords are securely hashed with BCrypt. Protected endpoints require a valid Bearer token.

## Installation

### Prerequisites

- Docker and Docker Compose
- Java 21+
- Node.js 20+

### Setup and Running with Docker (Recommended)

1. Rename `backend/src/main/resources/application.properties` parameters or just use the `.env` if provided. By default, Docker compose values match the properties.
2. Run Docker Compose to start PostgreSQL and Redis:
   ```bash
   docker compose up -d
   ```
3. Start the Spring Boot backend:
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
4. Start the React frontend:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

### API Documentation

Once the backend is running, you can access the Swagger UI documentation at:
`http://localhost:8080/swagger-ui.html`

## Environment Variables

**Backend (`backend/src/main/resources/application.properties`):**
- `DATABASE_URL`: PostgreSQL connection URL
- `DB_USERNAME`: Database username
- `DB_PASSWORD`: Database password
- `JWT_SECRET`: Secret key for JWT generation (must be secure and long)
- `JWT_EXPIRATION`: Token expiration time in milliseconds
- `REDIS_HOST`: Redis host address
- `REDIS_PORT`: Redis port
- `BASE_URL`: Base URL for generated short links

## Future Improvements

- Implementing Kafka/RabbitMQ for high-volume click event queues
- Advanced bot detection and mitigation
- Automated link preview generation
- Custom domains integration
- Team workspaces and bulk URL creation
