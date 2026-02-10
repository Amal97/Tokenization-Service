# Tokenization Service

A Java Spring Boot service for tokenizing and detokenizing sensitive data (e.g. account numbers). This service ensures data security by replacing sensitive account numbers with randomly generated tokens while maintaining a reversible mapping.

## Features

- **Tokenization**: Convert a list of account numbers into unique, secure tokens.
- **Detokenization**: Retrieve original account numbers from provided tokens.
- **Persistence**: Token mappings are stored in an H2 in-memory database.
- **Collision Handling**: Automatic retry mechanism for token generation collisions.
- **Input Validation**: Rejects empty or malformed requests.

## Prerequisites

- Java 25
- Maven

## API Documentation

### Tokenize
Converts account numbers to tokens.

- **URL**: `/tokenize`
- **Method**: `POST`
- **Body**: `List<String>` (Account Numbers)
- **Success Response**: `200 OK` with `List<String>` (Tokens)

**Example**:
```bash
curl --location 'http://localhost:3000/tokenize' \
--header 'Content-Type: application/json' \
--data '[
"4111-1111-1111-1111",
"4444-3333-2222-1111",
"4444-1111-2222-3333"
]'
```

### Detokenize
Converts tokens back to account numbers.

- **URL**: `/detokenize`
- **Method**: `POST`
- **Body**: `List<String>` (Tokens)
- **Success Response**: `200 OK` with `List<String>` (Account Numbers)

**Example**: (The tokens needs to be replaced with the actual tokens from /tokenize)

```bash
curl --location 'http://localhost:3000/detokenize' \
--header 'Content-Type: application/json' \
--data '[
    "84jjHMRaQoxh22X0IDCQ-Elw9NDpXZt7",
    "Pa76RgBanL-eymWlpZ6-hWbtqfEXPLrD",
    "QROQlZJlvkGcvI5gOc8hLxPov3s5XcS1"
]'
```

## Getting Started

### Run the Application
```bash
./mvnw spring-boot:run
```
The service will be available at `http://localhost:3000`.

### Run Tests
```bash
./mvnw test
```

## Docker

### Build the Image
```bash
docker build -t tokenization-service .
```

### Run the Container
```bash
docker run -p 3000:3000 tokenization-service
```
The service will be available at `http://localhost:3000`.

## Implementation Details

- **Database**: H2 (In-memory)
- **Security**: Uses `SecureRandom` for token generation.
- **Error Handling**: Standardized error responses for bad requests and internal failures.
