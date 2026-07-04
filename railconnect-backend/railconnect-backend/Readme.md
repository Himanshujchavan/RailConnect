# 🚆 RailConnect Backend

Enterprise-grade Railway Reservation and Journey Management Platform built using **Spring Boot 3**, **Java 21**, and **PostgreSQL**. The backend provides secure REST APIs for authentication, train management, ticket booking, automated seat allocation, PNR generation, and journey management.

---

## 📌 Features

- 🔐 JWT Authentication & Authorization
- 👥 Role-Based Access Control (Admin & Passenger)
- 🚆 Train Management
- 📅 Train Schedule Management
- 🪑 Automated Coach & Seat Generation
- 🎟️ Smart Seat Allocation
- 📍 Booking Management
- 🔍 PNR Generation & Tracking
- ❌ Ticket Cancellation
- 📖 Booking History
- 📄 Input Validation
- 📚 Swagger/OpenAPI Documentation
- 🗄 PostgreSQL Database Integration
- 🏗 Layered Architecture (Controller → Service → Repository)

---

# 🛠 Tech Stack

| Technology | Version |
|------------|---------|
| Java | 21 |
| Spring Boot | 3.5.4 |
| Spring Security | Latest |
| Spring Data JPA | Latest |
| PostgreSQL | Latest |
| JWT | 0.12.6 |
| MapStruct | 1.6.3 |
| Lombok | Latest |
| Swagger OpenAPI | 2.8.9 |
| Maven | Latest |

---

# 📂 Project Structure

```
src
├── main
│   ├── java
│   │   └── com.railconnect
│   │       ├── config
│   │       ├── controller
│   │       ├── dto
│   │       ├── entity
│   │       ├── exception
│   │       ├── mapper
│   │       ├── repository
│   │       ├── security
│   │       ├── service
│   │       ├── util
│   │       └── RailConnectApplication.java
│   │
│   └── resources
│       ├── application.properties
│       └── application.yml
│
└── test
```

---

# ⚙️ Prerequisites

Before running the project ensure you have installed:

- Java 21
- Maven 3.9+
- PostgreSQL
- IntelliJ IDEA / VS Code

---

# 🚀 Getting Started

## Clone Repository

```bash
git clone https://github.com/yourusername/railconnect-backend.git

cd railconnect-backend
```

---

## Configure Database

Create PostgreSQL database

```sql
CREATE DATABASE railconnect;
```

Update `application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/railconnect
spring.datasource.username=postgres
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## Build Project

```bash
mvn clean install
```

---

## 🚀 Run the Application

Using the Maven Wrapper (recommended):

```powershell
.\mvnw.cmd -q spring-boot:run
```

Alternatively, if Maven is installed globally:

```bash
mvn spring-boot:run
```

The backend server will start at:

```
http://localhost:8080
```

You should see output similar to:

```text
Started RailConnectApplication in XX.XXX seconds
Tomcat started on port 8080 (http)
```

# 📚 API Documentation

Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

OpenAPI

```
http://localhost:8080/v3/api-docs
```

---

# 🔐 Authentication

RailConnect uses **JWT Authentication**.

Workflow:

```
Register
      ↓
Login
      ↓
Receive JWT Token
      ↓
Add Token in Authorization Header

Bearer <JWT_TOKEN>
```

---

# 📦 Core Modules

## Authentication

- User Registration
- Login
- JWT Generation
- JWT Validation

---

## Passenger

- Profile Management
- Booking History
- Cancel Ticket
- PNR Status
- Search Trains

---

## Admin

- Add Train
- Update Train
- Delete Train
- Manage Coaches
- Manage Schedules
- View Bookings

---

## Booking

- Search Available Trains
- Seat Availability
- Smart Seat Allocation
- Booking Confirmation
- Ticket Cancellation
- Fare Calculation
- PNR Generation

---

## Seat Management

- Coach Creation
- Seat Layout Generation
- Dynamic Seat Allocation
- Availability Tracking

---

# 🏛 Architecture

```
Client
   │
REST API
   │
Controllers
   │
Services
   │
Repositories
   │
PostgreSQL
```

---

# 🔒 Security

- Spring Security
- JWT Authentication
- Password Encryption
- Role-Based Authorization
- Request Validation
- Exception Handling

---

# 📊 Database

Main entities include:

- User
- Train
- TrainSchedule
- Coach
- Seat
- Booking
- Passenger
- Ticket
- Payment

---

# 🧪 Testing

Run all tests

```bash
mvn test
```

---

# 📈 Future Enhancements

- Online Payment Gateway
- Email Notifications
- SMS Alerts
- Waiting List Prediction
- Live Train Tracking
- QR Code Tickets
- AI Seat Recommendation
- Analytics Dashboard
- Docker Deployment
- Redis Caching
- Microservices Architecture

---

# 👨‍💻 Built With

- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT
- MapStruct
- Lombok
- Swagger/OpenAPI
- Maven

---

