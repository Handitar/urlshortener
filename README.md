# URL Shortener

REST API сервіс для скорочення URL-адрес, побудований на **Spring Boot**, із підтримкою **JWT-аутентифікації**, **PostgreSQL**, **Flyway**, **Swagger/OpenAPI**, **Docker**, **GitHub Actions CI** та **тестування**.

## Основний функціонал

- реєстрація користувачів
- логін і отримання JWT токена
- створення коротких посилань
- перегляд усіх посилань поточного користувача
- перегляд лише активних посилань
- видалення посилань
- redirect за short code
- підрахунок кількості переходів
- обробка прострочених посилань
- Swagger/OpenAPI документація

---

## Технологічний стек

- Java 21
- Spring Boot 3
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- JWT
- Swagger / springdoc-openapi
- JUnit 5
- Mockito
- Testcontainers
- Docker
- Docker Compose
- GitHub Actions
- Gradle

---

## Структура проєкту

Проєкт організований за принципом `package-as-feature`.

```text
src/main/java/com/example/urlshortener
├── AppLauncher.java
├── auth
├── common
├── link
└── user
```

---

## Змінні оточення

Застосунок підтримує конфігурацію через environment variables.

| Змінна | Опис | Значення за замовчуванням |
|---|---|---|
| `DB_URL` | URL підключення до PostgreSQL | `jdbc:postgresql://localhost:5433/urlshortener` |
| `DB_USERNAME` | користувач PostgreSQL | `postgres` |
| `DB_PASSWORD` | пароль PostgreSQL | `postgres` |
| `JWT_SECRET` | секрет для підпису JWT | `VerySecretKeyVerySecretKeyVerySecretKey123` |
| `JWT_EXPIRATION` | час життя токена в мілісекундах | `3600000` |

Основна конфігурація застосунку розташована в:

```text
src/main/resources/application.yml
```

---

## Запуск через Docker Compose

### Запуск усього застосунку

```bash
docker compose up --build
```

Після запуску будуть доступні:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## Локальний запуск без Docker Compose для app

### 1. Підняти PostgreSQL

```bash
docker compose up -d postgres
```

### 2. Запустити застосунок

#### Windows
```bash
gradlew.bat bootRun
```

#### Linux / macOS
```bash
./gradlew bootRun
```

Після запуску:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## Swagger / OpenAPI

Swagger UI доступний за адресою:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

---

## Аутентифікація

Для доступу до захищених endpoint-ів потрібен JWT токен у заголовку:

```text
Authorization: Bearer <token>
```

Типовий сценарій роботи:

1. Зареєструвати користувача через `POST /api/v1/auth/register`
2. Увійти через `POST /api/v1/auth/login`
3. Отримати JWT токен
4. Передавати його у `Authorization` header для захищених endpoint-ів

---

## Основні endpoint-и

### Auth
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`

### Links
- `POST /api/v1/links`
- `GET /api/v1/links`
- `GET /api/v1/links/active`
- `DELETE /api/v1/links/{id}`

### Redirect
- `GET /r/{shortCode}`

---

## Приклади запитів

### Реєстрація

```json
{
  "username": "admin",
  "password": "Password123"
}
```

### Логін

```json
{
  "username": "admin",
  "password": "Password123"
}
```

### Створення короткого посилання

```json
{
  "originalUrl": "https://www.google.com",
  "expiresAt": "2026-12-31T23:59:59"
}
```

---

## Тестування

### Запуск усіх тестів

#### Windows
```bash
gradlew.bat clean test
```

#### Linux / macOS
```bash
./gradlew clean test
```

---

## Локальний запуск інтеграційних тестів

За замовчуванням локально інтеграційні тести використовують PostgreSQL на `localhost:5433`.

Перед запуском тестів підніми базу:

```bash
docker compose up -d postgres
gradlew.bat clean test
```

---

## Запуск інтеграційних тестів через Testcontainers

Проєкт також підтримує запуск інтеграційних тестів через Testcontainers:

```bash
gradlew.bat clean test -DuseTestcontainers=true
```

> Примітка: на частині Windows + Docker Desktop конфігурацій Testcontainers можуть працювати нестабільно через особливості локального Docker environment.  
> У такому випадку для локального запуску використовуй режим із PostgreSQL на `localhost:5433`.

---

## Покриття тестами

Для генерації JaCoCo звіту:

#### Windows
```bash
gradlew.bat jacocoTestReport
```

#### Linux / macOS
```bash
./gradlew jacocoTestReport
```

HTML-звіт буде доступний у:

```text
build/reports/jacoco/test/html/index.html
```

---

## Міграції бази даних

Flyway міграції знаходяться в:

```text
src/main/resources/db/migration
```

Міграції застосовуються автоматично під час старту застосунку.

---

## CI

У проєкті налаштований GitHub Actions workflow:

```text
.github/workflows/ci.yml
```

CI виконує:
- збірку проєкту
- запуск тестів
- перевірку покриття

---

## Особливості реалізації

- посилання видаляються через **soft delete**
- активними вважаються лише **непрострочені** і **невидалені** посилання
- redirect endpoint доступний **без авторизації**
- прострочені посилання повертають `410 Gone`
- кількість переходів збільшується при переході за short link
- користувач може видаляти лише власні посилання


- Для доступу до захищених endpoints потрібен JWT token
- Прострочені посилання повертають `410 Gone`
- Redirect endpoint доступний без авторизації
