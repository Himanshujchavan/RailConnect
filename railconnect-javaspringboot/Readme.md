# 🚆 RailConnect Backend

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![JWT](https://img.shields.io/badge/JWT-Authentication-red)
![Maven](https://img.shields.io/badge/Maven-Build-blue)
![Docker](https://img.shields.io/badge/Docker-Containerization-2496ED)
![License](https://img.shields.io/badge/License-MIT-green)

### Enterprise Railway Reservation Platform Backend

*A production-style modular monolith backend built with Java, Spring Boot, PostgreSQL, and JWT Authentication.*

</div>

---

# 📖 Overview

RailConnect is an enterprise-grade Railway Reservation backend that powers a modern train booking platform similar to IRCTC.

The backend is designed using a **Modular Monolith Architecture**, allowing each module to be independently maintained while remaining part of a single Spring Boot application. The architecture follows enterprise software development practices and can later be migrated to Microservices with minimal changes.

---

# 🎯 Objectives

- Build secure REST APIs
- Demonstrate Core Java concepts
- Follow enterprise architecture
- Implement JWT Authentication
- Showcase Spring Boot best practices
- Design scalable PostgreSQL database
- Implement Railway Reservation workflow

---

# 🚀 Tech Stack

## Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- Maven
- PostgreSQL
- JWT Authentication
- Bean Validation
- Swagger/OpenAPI
- Lombok
- Docker
- JUnit 5
- Mockito

---

# 🏗 Architecture

```
                  Client (React)

                        │

                 REST API (JSON)

                        │

────────────────────────────────────────────

Authentication Module

User Module

Train Module

Reservation Engine

Seat Allocation Engine

Journey Module

PNR Module

Payment Module

Notification Module

Review Module

Analytics Module

Admin Module

────────────────────────────────────────────

Business Service Layer

────────────────────────────────────────────

Repository Layer

────────────────────────────────────────────

PostgreSQL Database
```

---



# 📦 Modules

## Authentication

- Register
- Login
- Logout
- Refresh Token
- Forgot Password
- Reset Password
- JWT Authentication
- Role Based Access

---

## User

- Profile
- Saved Passengers
- Addresses
- Preferences
- Booking History

---

## Train

- Train Search
- Routes
- Schedules
- Coaches
- Seat Availability

---

## Reservation Engine

- Ticket Booking
- Booking Validation
- Cancellation
- Refund
- Booking History

---

## Seat Allocation Engine

- Seat Allocation
- Berth Preference
- Family Allocation
- Waiting List
- RAC (Future)

---

## Journey

- Journey Details
- Upcoming Trips
- Current Journey
- Route Timeline

---

## PNR

- Generate PNR
- Search PNR
- Booking Status
- Passenger Status

---

## Payment

- UPI
- Card
- Wallet
- Coupons
- Refund
- Invoice

---

## Notification

- Booking Confirmation
- Refund Notification
- Journey Reminder
- Email Service

---

## Reviews

- Ratings
- Comments
- Journey Feedback

---

## Analytics

- Revenue
- Occupancy
- Popular Routes
- Booking Statistics

---

## Admin

- Train Management
- Route Management
- Booking Management
- Passenger Management
- Reports
- Dashboard

---

# 🗄 Database

## Authentication

- users
- roles
- refresh_tokens

---

## User

- passengers
- addresses
- preferences

---

## Train

- stations
- trains
- routes
- schedules
- coaches
- seats

---

## Reservation

- bookings
- booking_history
- seat_allocations
- pnr

---

## Payment

- payments
- refunds
- coupons
- invoices
- wallets

---

## Notification

- notifications

---

## Review

- reviews

---

## Admin

- audit_logs

---

# 🔐 Security

- JWT Authentication
- Refresh Token
- BCrypt Password Encryption
- Role Based Authorization
- Protected Endpoints
- Input Validation
- Global Exception Handling

---

# 📡 REST APIs

## Authentication

```
POST   /api/v1/auth/register

POST   /api/v1/auth/login

POST   /api/v1/auth/logout

POST   /api/v1/auth/refresh

POST   /api/v1/auth/forgot-password

POST   /api/v1/auth/reset-password
```

---

## User

```
GET    /api/v1/users/profile

PUT    /api/v1/users/profile

GET    /api/v1/users/passengers

POST   /api/v1/users/passengers

PUT    /api/v1/users/passengers/{id}

DELETE /api/v1/users/passengers/{id}
```

---

## Train

```
GET /api/v1/trains/search

GET /api/v1/trains/{id}

GET /api/v1/trains/{id}/availability

GET /api/v1/trains/routes
```

---

## Reservation

```
POST /api/v1/bookings

PUT /api/v1/bookings/{id}

DELETE /api/v1/bookings/{id}

GET /api/v1/bookings

GET /api/v1/bookings/{id}
```

---

## PNR

```
GET /api/v1/pnr/{pnrNumber}
```

---

## Payment

```
POST /api/v1/payments

GET /api/v1/payments

POST /api/v1/refunds
```

---

## Notification

```
GET /api/v1/notifications

PUT /api/v1/notifications/read
```

---

## Admin

```
GET /api/v1/admin/dashboard

GET /api/v1/admin/revenue

GET /api/v1/admin/bookings

POST /api/v1/admin/trains

PUT /api/v1/admin/trains/{id}

DELETE /api/v1/admin/trains/{id}
```

---

# 💻 Core Java Concepts

✔ Object-Oriented Programming

✔ Encapsulation

✔ Abstraction

✔ Inheritance

✔ Polymorphism

✔ Interfaces

✔ Collections Framework

✔ Streams API

✔ Lambda Expressions

✔ Optional

✔ Generics

✔ Comparable

✔ Comparator

✔ Custom Exceptions

✔ Enums

✔ Multithreading

✔ File Handling

✔ SOLID Principles

---

# 🌱 Spring Boot Concepts

- Spring MVC
- Spring Security
- Spring Data JPA
- Hibernate ORM
- Bean Validation
- REST API Development
- DTO Pattern
- Mapper Pattern
- Dependency Injection
- Repository Pattern
- Global Exception Handling
- Pagination
- Sorting
- Transaction Management
- Swagger Documentation
- Logging
- Docker Deployment

---

# 🎨 Design Patterns

| Pattern | Usage |
|----------|------|
| Singleton | Configuration Beans |
| Factory | Reservation Creation |
| Builder | Booking Request Builder |
| Strategy | Payment & Fare Calculation |
| Observer | Notification Service |

---

# 🧪 Testing

- Unit Testing
- Service Testing
- Repository Testing
- Controller Testing
- Integration Testing

---

# 📈 Future Scope

- Microservices Architecture
- Kafka Event Streaming
- Redis Caching
- Spring Cloud Gateway
- Eureka Discovery Server
- Docker Compose
- Kubernetes
- Payment Gateway Integration
- Live Train Tracking
- AI Seat Recommendation

---

# ▶ Running the Project

## Clone Repository

```bash
git clone https://github.com/yourusername/railconnect-backend.git
```

---

## Navigate

```bash
cd backend
```

---

## Configure Database

Update

```
application.yml
```

```
spring.datasource.url

spring.datasource.username

spring.datasource.password
```

---

## Run

```bash
mvn spring-boot:run
```

---

## Swagger

```
http://localhost:8080/swagger-ui/index.html
```

---

# 📌 Project Highlights

- Enterprise Modular Monolith Architecture
- Production-Level Folder Structure
- Secure JWT Authentication
- Railway Reservation Workflow
- Seat Allocation Engine
- PNR Generation
- RESTful API Design
- PostgreSQL Relational Database
- Global Exception Handling
- Docker Ready
- Swagger Documentation
- Unit & Integration Testing
- Clean Code & SOLID Principles

---

# 👨‍💻 Author

**Himanshu Chavan**

Java Full Stack Developer | Spring Boot | React | PostgreSQL

---

## ⭐ If you found this project useful, consider giving it a star!