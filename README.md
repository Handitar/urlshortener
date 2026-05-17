# URL Shortener

REST API сервіс для скорочення URL-адрес із підтримкою JWT-аутентифікації, статистики переходів, PostgreSQL, Flyway, Swagger/OpenAPI, Docker та Docker Compose.

## Основний функціонал

- реєстрація користувачів
- аутентифікація через JWT
- створення коротких URL
- перегляд усіх створених URL
- перегляд активних URL
- перегляд конкретного URL за id
- видалення URL
- redirect за short code
- підрахунок кількості переходів
- обробка прострочених посилань

## Технологічний стек

- Java 21
- Spring Boot 3
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- OpenAPI / Swagger
- JUnit 5
- Mockito
- Testcontainers
- Docker
- Docker Compose
- GitHub Actions

## Структура проєкту

Проєкт організований за принципом `package-as-feature`.

```text
src/main/java/com/example/urlshortener
├── AppLauncher.java
├── auth
├── user
├── link
└── common
```

## Environment Variables

Для запуску застосунку використовуються такі змінні оточення:

| Variable | Description | Example |
|---|---|---|
| DB_URL | URL підключення до PostgreSQL | jdbc:postgresql://localhost:5433/urlshortener |
| DB_USERNAME | username PostgreSQL | postgres |
| DB_PASSWORD | password PostgreSQL | postgres |
| JWT_SECRET | секрет для підпису JWT | VerySecretKeyVerySecretKeyVerySecretKey123 |
| JWT_EXPIRATION | час життя токена в мілісекундах | 3600000 |

## Запуск локально з IntelliJ IDEA

### 1. Запустити PostgreSQL через Docker Compose
```bash
docker compose up -d postgres
```

### 2. Додати змінні оточення в Run/Debug Configuration

```text
DB_URL=jdbc:postgresql://localhost:5433/urlshortener
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=VerySecretKeyVerySecretKeyVerySecretKey123
JWT_EXPIRATION=3600000
```

### 3. Запустити клас `AppLauncher`

## Запуск через Docker Compose

```bash
docker compose up --build
```

Після запуску:
- application: `http://localhost:8080`
- swagger: `http://localhost:8080/swagger-ui.html`

## Swagger / OpenAPI

Swagger UI доступний за адресою:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

## Основні endpoints

### Auth
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`

### Links
- `POST /api/v1/links`
- `GET /api/v1/links`
- `GET /api/v1/links/active`
- `GET /api/v1/links/{id}`
- `DELETE /api/v1/links/{id}`

### Redirect
- `GET /r/{shortCode}`

## Приклади запитів

### Register
```json
{
  "username": "admin",
  "password": "Password123"
}
```

### Login
```json
{
  "username": "admin",
  "password": "Password123"
}
```

### Create short link
```json
{
  "originalUrl": "https://www.google.com",
  "expiresAt": "2026-12-31T23:59:59"
}
```

## Запуск тестів

```bash
./gradlew test
```

Для Windows:
```bash
gradlew.bat test
```

## Покриття тестами

Після запуску тестів JaCoCo report буде доступний у:

```text
build/reports/jacoco/test/html/index.html
```

## Міграції бази даних

Flyway міграції розташовані в:

```text
src/main/resources/db/migration
```

## Примітки

- Для доступу до захищених endpoints потрібен JWT token
- Прострочені посилання повертають `410 Gone`
- Redirect endpoint доступний без авторизації