# NotesApp
## Overview

HubApp is a Spring Boot application that provides a RESTful API for managing products. It uses MongoDB as the database and includes Swagger for API documentation.

## Prerequisites

- [Docker](https://www.docker.com/products/docker-desktop)
- [Docker Compose](https://docs.docker.com/compose/install/)
- [Maven](https://maven.apache.org/install.html)

## Getting Started

### Build the Project

1. Open a terminal in the root directory of the project.
2. Run the following command to build the project using Gradle:
   ```bash
   mvn clean isntall
   ```
   
### Build and Run Docker Containers

Use Docker Compose to build the Docker image and start the containers. Run the following command:
   ```bash 
   docker-compose up --build
   ```

### Access the Application
The application will be accessible at - http://localhost:8080. 
You can use Postman (see the attached postman collection).

Swagger UI for API documentation will be available at - http://localhost:8080/swagger-ui/index.html

OpenAPI Definition can be found at - http://localhost:8080/v3/api-docs

### Technical Notes

#### Endpoints

- **POST /api/v1/products**: Create a new product (Admin only).
- **PUT /api/v1/products/{id}**: Update an existing product (Admin only).
- **PATCH /api/v1/products/{id}/price**: Update the price of a product (Admin only).
- **DELETE /api/v1/products/{id}**: Delete a product by its ID (Admin only).
- **GET /api/v1/products/{id}**: Get a product by its ID (User and Admin).
- **GET /api/v1/products**: Get all products with pagination (User and Admin).

#### Security

The application uses Spring Security for authentication and authorization.

- **In-Memory Authentication**: Users are defined in the `SecurityConfig` class.
- **Roles**: `USER` and `ADMIN`.
- **Endpoints Protection**: Endpoints are protected based on roles using `@PreAuthorize` annotations in the controller.

## License

This project is licensed under the MIT License.

## Contact

For any questions or support, please contact: h.eduardgabor@gmail.com
