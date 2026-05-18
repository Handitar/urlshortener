# URL Shortener

REST API сервіс для скорочення URL-адрес, побудований на **Spring Boot**, із підтримкою **JWT-аутентифікації**, **PostgreSQL**, **Flyway**, **Swagger/OpenAPI**, **Docker**, **Testcontainers** та **GitHub Actions CI**.

## Основний функціонал

- реєстрація користувачів
- логін і отримання JWT токена
- створення коротких посилань
- перегляд усіх посилань поточного користувача
- перегляд активних посилань
- перегляд конкретного посилання за id
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
│   ├── config
│   ├── exception
│   └── security
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

## Запуск проєкту

### Варіант 1. Запуск через Docker Compose

Цей варіант піднімає:
- PostgreSQL
- застосунок

```bash
docker compose up --build
```

Після запуску будуть доступні:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

---

### Варіант 2. Локальний запуск застосунку

#### Крок 1. Підняти PostgreSQL

```bash
docker compose up -d postgres
```

#### Крок 2. Запустити застосунок

### Windows
```bash
gradlew.bat bootRun
```

### Linux / macOS
```bash
./gradlew bootRun
```

Після запуску будуть доступні:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## Як протестувати проєкт вручну

### 1. Відкрити Swagger
Перейди в браузері на:

```text
http://localhost:8080/swagger-ui.html
```

---

### 2. Зареєструвати користувача
Endpoint:

```text
POST /api/v1/auth/register
```

Приклад body:

```json
{
  "username": "admin",
  "password": "Password123"
}
```

---

### 3. Увійти в систему
Endpoint:

```text
POST /api/v1/auth/login
```

Приклад body:

```json
{
  "username": "admin",
  "password": "Password123"
}
```

У відповідь буде JWT токен.

---

### 4. Авторизуватись у Swagger
Для захищених endpoint-ів потрібно передавати токен у форматі:

```text
Bearer <token>
```

---

### 5. Створити коротке посилання
Endpoint:

```text
POST /api/v1/links
```

Приклад body:

```json
{
  "originalUrl": "https://www.google.com",
  "expiresAt": "2026-12-31T23:59:59"
}
```

---

### 6. Перевірити список посилань
Endpoint:

```text
GET /api/v1/links
```

---

### 7. Перевірити активні посилання
Endpoint:

```text
GET /api/v1/links/active
```

---

### 8. Перевірити конкретне посилання за id
Endpoint:

```text
GET /api/v1/links/{id}
```

---

### 9. Перевірити redirect
Скопіюй `shortCode` зі створеного посилання і відкрий у браузері:

```text
http://localhost:8080/r/{shortCode}
```

або виконай GET-запит на цей endpoint.

---

### 10. Видалити посилання
Endpoint:

```text
DELETE /api/v1/links/{id}
```

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

Публічні endpoint-и:
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `GET /r/{shortCode}`
- Swagger/OpenAPI endpoints

---

## Основні endpoint-и

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

---

## Тестування

Для integration tests використовується Testcontainers, тому перед запуском тестів має бути доступний Docker.

### Windows
```bash
gradlew.bat clean test

### Linux / macOS
```bash
./gradlew clean test

```

> Якщо Docker недоступний або Docker Desktop не запущений, integration tests не стартують.

---

## Покриття тестами

Для генерації JaCoCo-звіту:

### Windows
```bash
gradlew.bat jacocoTestReport
```

### Linux / macOS
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
- користувач може переглядати та видаляти лише власні посилання

