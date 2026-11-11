# NodeWatch

Простой сервис мониторинга доступности узлов (HTTP-эндпоинтов). Периодически проверяет указанные цели и сохраняет историю проверок.

## Возможности

- 🎯 Добавление целей мониторинга через REST API
- 🔄 Фоновая проверка доступности через корутины Kotlin
- 📊 Хранение истории проверок в PostgreSQL
- 🐳 Полная контейнеризация через Docker Compose
- ✅ Покрытие тестами

## Стек технологий

- **Kotlin** + **Ktor 3** – серверный фреймворк
- **PostgreSQL 16** – база данных
- **Exposed** – типобезопасный ORM от JetBrains
- **HikariCP** – пул соединений с БД
- **kotlinx.serialization** – JSON-сериализация
- **Docker** + **Docker Compose** – контейнеризация
- **JUnit** + **ktor-server-test-host** – тестирование

## Быстрый старт

### Требования

- Docker и Docker Compose

### Запуск

```bash
git clone https://github.com/BlastCloudDi/nodewatch.git
cd nodewatch
docker-compose up --build
```

Сервер будет доступен на `http://localhost:8080`.

### Остановка

```bash
    docker-compose down
```


## API

- `GET /health` – проверка работоспособности
- `GET /targets` – список целей
- `POST /targets` – добавить цель
- `GET /targets/{id}` – получить цель
- `DELETE /targets/{id}` – удалить цель
- `GET /targets/{id}/history` – история проверок

## Архитектура

    src/main/kotlin/
    ├── main.kt                  # Точка входа
    ├── Routing.kt               # HTTP-маршруты
    ├── Serialization.kt         # Настройка JSON
    ├── Databases.kt             # Инициализация БД + жизненный цикл
    ├── Database.kt              # Описание таблиц Exposed
    ├── Target.kt                # Модель цели
    ├── TargetDtos.kt            # DTO для запросов/ответов API
    ├── TargetRepository.kt      # Работа с целями (БД / in-memory)
    ├── CheckResult.kt           # Модель результата проверки
    ├── CheckResultRepository.kt # Работа с историей проверок
    ├── HealthChecker.kt         # Логика пинга цели
    └── BackgroundWorker.kt      # Фоновый воркер на корутинах

## Тесты
```bash
    ./gradlew test
```
Покрытие:

- Эндпоинт Health
- CRUD API для целей
- `HealthChecker` с мокированием HTTP-клиента
- Эндпоинт истории проверок
