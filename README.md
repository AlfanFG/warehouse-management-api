# Warehouse Management API

RESTful API for shop warehouse management built with Spring Boot, PostgreSQL, MapStruct, and Docker.

Design decisions you made and why
I created a simple MVC architecture for this project, with the following layers:
- **Controller Layer**: Handles HTTP requests and responses.
- **Service Layer**: Contains business logic and orchestrates operations.
- **Repository Layer**: Handles data access operations.
- **DTO Layer**: Defines data transfer objects for request and response payloads.
- **Mapper Layer**: Uses MapStruct to map between entities and DTOs.
- **Entity Layer**: Represents the database entities and their relationships.
- **Exception Handling Layer**: Provides centralized exception handling and error responses.
- **Idempotency Handling**: Ensures that repeated requests with the same idempotency key do not result in duplicate processing.

Any assumptions you made
- The api is designed simple and stateless, with the help of mapstruct, and many annotation that mekes developer easier to implement the restful api.
- I added the idempotency key to prevent double request and prevent race condition

## Documentation
- Full API documentation: [API_DOCUMENTATION.md](API_DOCUMENTATION.md)

## How to Run

### Using Docker Compose
```bash
docker compose up -d --build
```

The service will be available at:
`http://localhost:8080/api/v1/inventory`
