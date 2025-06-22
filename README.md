# kafka-tool

**kafka-tool** — это REST-инструмент для генерации и отправки Avro-сообщений в Apache Kafka. Сервис поддерживает как локальные Avro-схемы (хранящиеся в БД), так и схемы из Confluent Schema Registry. Основная задача — упростить ручное и массовое тестирование микросервисов, использующих Kafka.

---

## 🚀 Возможности

- CRUD для Avro-схем (создание, просмотр, удаление, хранение в БД)
- Получение схем из Confluent Schema Registry через REST
- Генерация валидных JSON-примеров по Avro-схеме (с учетом union-полей)
- Отправка сообщений в Kafka (raw Avro или через Registry) с поддержкой key и headers
- Генерация и отправка пачки сообщений “в один клик” (с уникальными данными)
- Автоматическое определение схемы по имени Kafka topic (если используется Schema Registry)
- Просмотр истории отправленных сообщений (с key, headers, ошибками)
- Получение списка Kafka-топиков через REST
- Swagger/OpenAPI-документация с примерами запросов
- Гибкая обработка ошибок и валидация параметров

---

## 🧑‍💻 Быстрый старт

1. **Создай базу данных PostgreSQL**
    ```sql
    CREATE DATABASE kafkatool;
    ```

2. **Запусти Kafka и Schema Registry (пример docker-compose):**
    ```yaml
    zookeeper:
      image: confluentinc/cp-zookeeper:7.4.0
      environment:
        ZOOKEEPER_CLIENT_PORT: 2181

    kafka:
      image: confluentinc/cp-kafka:7.4.0
      environment:
        KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
        KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092,PLAINTEXT_EXTERNAL://0.0.0.0:29092
        KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_EXTERNAL://localhost:29092
      ports:
        - "9092:9092"
        - "29092:29092"

    schema-registry:
      image: confluentinc/cp-schema-registry:7.4.0
      environment:
        SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS: PLAINTEXT://kafka:9092
        SCHEMA_REGISTRY_LISTENERS: http://0.0.0.0:8081
      ports:
        - "8081:8081"
    ```

3. **Настрой параметры подключения в `application.yml`:**
    ```yaml
    kafka:
      bootstrap-servers: localhost:29092
      schema-registry-url: http://localhost:8081

    spring:
      datasource:
        url: jdbc:postgresql://localhost:5432/kafkatool
        username: youruser
        password: yourpass
      jpa:
        hibernate:
          ddl-auto: update
        show-sql: true
    ```

4. **Собери и запусти проект:**
    ```bash
    ./gradlew build
    ./gradlew bootRun
    ```

5. **Swagger UI доступен по адресу:**
    ```
    http://localhost:8080/swagger-ui.html
    ```
    или
    ```
    http://localhost:8080/swagger-ui/index.html
    ```

---

## 📚 Примеры использования

### 1. Добавить Avro-схему
```http
POST /api/schemas
{
  "name": "MyEvent",
  "description": "Тестовая схема",
  "schemaJson": {
    "type": "record",
    "name": "MyEvent",
    "fields": [
      { "name": "id", "type": "long" }
    ]
  }
}
```

### 2. Сгенерировать и отправить 3 сообщения по схеме из Registry (по topic)
```http
POST /api/generate-send
{
  "topic": "my-events",
  "count": 3
}
```

### 3. Отправить сообщение вручную с key и headers
```http
POST /api/messages
{
  "schemaId": 1,
  "topic": "my-events",
  "key": "user-1",
  "headers": { "traceId": "abc-123" },
  "messageJsonList": [
    { "id": 42 }
  ]
}
```

### 4. Получить список топиков Kafka
```http
GET /api/topics
```

### 5. Сгенерировать пример JSON по схеме
```http
GET /api/examples/schema/1
GET /api/examples/subject/my-events-value
```

---

## 🔗 Важные эндпоинты

- `GET /api/schemas` — список локальных схем (БД)
- `GET /api/schema-registry/subjects` — список subject'ов в Schema Registry
- `GET /api/schema-registry/subjects/{subject}` — тело схемы Registry
- `GET /api/topics` — список Kafka-топиков
- `GET /api/logs` — история отправки сообщений
- `POST /api/generate` — генерация сообщений без отправки
- `POST /api/generate-send` — генерация и отправка сразу

---

## 🛠️ Требования

- Java 17+
- Gradle 7.5+
- PostgreSQL 13+
- Kafka 3.x+ (Confluent 7.x+, если нужен Registry)
- Docker (для локального запуска брокера и Registry)

---

## 🏗️ TODO / Идеи для развития

- [ ] Улучшенная генерация данных (шаблоны, seed, диапазоны)
- [ ] UI для ручного теста (web-интерфейс)
- [ ] Поддержка других форматов (JSON, Protobuf)
- [ ] Интеграция с внешними системами логирования/мониторинга


---

**Проект разрабатывается для внутренних нужд QA и backend-команды.**