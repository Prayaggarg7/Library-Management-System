# Library Management System

A full-stack Library Management System built using Java Spring Boot, MySQL, and Angular.

## Features

### Authentication
- User Login
- JWT Authentication
- Role-based Authorization

### Book Management
- Add Book
- Update Book
- Delete Book
- View Book Details
- Search Books
- Pagination Support

### Member Management
- Add Member
- Update Member
- Deactivate Member
- View Member Details
- Pagination Support

### Issue / Return Management
- Issue Book
- Return Book
- Due Date Tracking
- Transaction History
- Available Copy Validation
- Duplicate Issue Prevention

### Dashboard
- Library Statistics
- Active Members
- Available Books
- Issued Books Summary

### Additional Features
- Swagger Documentation
- Flyway Database Migration
- Global Exception Handling
- Caching Support
- Scheduled Jobs
- Audit Fields

---

# Technology Stack

## Backend

- Java 17
- Spring Boot 3
- Spring Security
- Spring Data JPA
- MySQL
- Flyway
- Swagger/OpenAPI
- JWT Authentication

## Frontend

- Angular 17
- TypeScript
- Bootstrap 5
- PrimeNG
- Reactive Forms

---

# Project Structure

## Backend

```
src/main/java
│
├── controller
├── service
├── repository
├── entity
├── dto
├── config
├── exception
└── security
```

## Frontend

```
src/app
│
├── components
├── pages
│   ├── books
│   ├── members
│   ├── transactions
│   └── dashboard
├── services
├── interceptors
├── pipes
└── models
```

---

# Database Setup

Create database:

```sql
CREATE DATABASE library_management;
```

Update backend configuration:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/library_management
spring.datasource.username=root
spring.datasource.password=your_password
```

---

# Backend Setup

Navigate to backend project:

```bash
cd library-management-backend
```

Build project:

```bash
mvn clean install
```

Run application:

```bash
mvn spring-boot:run
```

Backend runs on:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

---

# Frontend Setup

Navigate to frontend:

```bash
cd library-management-frontend
```

Install dependencies:

```bash
npm install
```

Run application:

```bash
ng serve
```

Frontend runs on:

```text
http://localhost:4200
```

---

# Business Rules

## Book Issue Rules

- Book can be issued only if available copies are greater than zero.
- Same member cannot issue the same book twice without returning it.
- Available copies decrease after issue.

## Book Return Rules

- Returned book increases available copies.
- Already returned records cannot be returned again.
- Returned date is recorded automatically.

---

# API Documentation

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

---

# Future Enhancements

- Fine Calculation
- Email Notifications
- Report Export (PDF/Excel)
- Advanced Analytics Dashboard
- Unit and Integration Tests

---

# Author

Developed using Java Spring Boot, Angular and MySQL.