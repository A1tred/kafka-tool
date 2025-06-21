# kafka-tool

**kafka-tool** — внутренний REST-инструмент для ручной отправки Avro-сообщений в Kafka через API.

## Возможности MVP

- Управление Avro-схемами через REST (загрузка, просмотр, удаление)
- Отправка сообщений в Kafka в формате Avro (JSON → Avro → Kafka)
- Валидация сообщений по Avro-схеме
- Логирование успешных/неуспешных попыток отправки
- Генерация примера JSON для схемы
- Документация API через Swagger/OpenAPI

## Быстрый старт

1. **Создай базу данных PostgreSQL**:

    ```sql
    CREATE DATABASE kafkatool;
    CREATE USER yourusername WITH PASSWORD 'yourpassword';
    GRANT ALL PRIVILEGES ON DATABASE kafkatool TO yourusername;
    ```

2. **Настрой приложение**  
   Отредактируй `src/main/resources/application.yml`:

    ```yaml
    spring:
      datasource:
        url: jdbc:postgresql://localhost:5432/kafkatool
        driver-class-name: org.postgresql.Driver
        username: yourusername
        password: yourpassword
      jpa:
        hibernate:
          ddl-auto: update
        show-sql: true

    kafka:
      bootstrap-servers: localhost:9092
    ```

3. **Собери и запусти проект:**

    ```bash
    ./gradlew build
    ./gradlew bootRun
    ```

4. **Swagger UI доступен по адресу:**
    ```
    http://localhost:8080/swagger-ui.html
    ```

## Структура проекта

- `/src/main/java/com/isimple/kafkatool` — исходный код
- `/src/main/resources` — ресурсы и конфигурации
- `/build.gradle` — зависимости Gradle
- `/README.md` — эта документация

## Для разработки

- Java 17+
- Gradle 7.5+
- PostgreSQL 13+
- Kafka 3.x+ (локально или в Docker)

## TODO

- [ ] Добавить поддержку Schema Registry
- [ ] UI-интерфейс (опционально)
- [ ] Улучшенная генерация примеров сообщений

---

**Проект разрабатывается для внутренних нужд QA и backend-команды.**
