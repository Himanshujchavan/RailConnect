# 🚆 RailConnect – Smart Railway Reservation & Ticket Booking System

## Project Overview

RailConnect is a full-stack railway reservation platform inspired by modern railway booking systems. It provides passengers with a seamless experience for searching trains, checking seat availability, booking tickets, making secure payments, tracking PNR status, and managing journeys. The system also includes a comprehensive admin portal for managing stations, trains, routes, coaches, schedules, fares, and bookings.

The project is built using a scalable layered architecture with Spring Boot microservice-ready design principles, JWT-based authentication, PostgreSQL database, and a modern React.js frontend.

---

# Problem Statement

Traditional railway reservation systems often have limited user experience, delayed seat allocation, and complex booking workflows. RailConnect addresses these issues by providing:

* Secure user authentication
* Fast train search
* Intelligent seat allocation
* Real-time seat availability
* Digital ticket generation
* Booking history management
* Admin management portal

---

# Technology Stack

### Frontend

* React.js
* React Router
* Axios
* Tailwind CSS
* ShadCN/UI
* Framer Motion
* React Hook Form
* Zod Validation
* React Query (TanStack Query)
* React Hot Toast
* Lucide Icons
* Recharts
* Vite

---

### Backend

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* JWT Authentication
* Bean Validation
* Lombok
* PostgreSQL
* Maven
* Swagger/OpenAPI

---

### Database

PostgreSQL

Major tables include

* Users
* Roles
* Stations
* Trains
* Coaches
* Seats
* Routes
* Schedules
* Passengers
* Bookings
* BookingPassengers
* SeatAllocations
* PNR
* Payments
* Notifications

---

# Frontend Modules

## Authentication

* Login
* Registration
* Forgot Password
* Reset Password
* JWT Authentication
* Role-based Navigation

---

## Landing Page

* Hero Section
* Search Train
* Popular Routes
* Features
* Statistics
* Testimonials
* Footer

---

## User Dashboard

* Profile
* Saved Passengers
* Booking History
* Upcoming Journeys
* Notifications
* Wallet
* Settings

---

## Train Search

Users can

* Select source station
* Select destination station
* Journey date
* Search trains
* Filter trains

---

## Train Details

Displays

* Train Information
* Running Days
* Available Classes
* Journey Duration
* Fare
* Seat Availability

---

## Seat Availability

Displays

* Coach Layout
* Available Seats
* Occupied Seats
* Coach Selection
* Berth Types

---

## Booking Module

Passenger Details

* Name
* Age
* Gender
* Berth Preference

Booking Summary

* Train
* Coach
* Fare
* Taxes
* Total Amount

---

## Payment Module

* Payment Gateway
* Booking Confirmation
* Transaction Status
* Refund Status

---

## Ticket Page

Displays

* Ticket
* QR Code
* PNR
* Passenger Details
* Journey Details
* Download Ticket

---

## Booking History

Users can

* View Tickets
* Download Ticket
* Cancel Booking
* Track Refund

---

## PNR Status

Search booking using

* PNR Number

Displays

* Booking Status
* Coach
* Seat Number
* Journey Status

---

## Admin Dashboard

Admin can manage

* Stations
* Trains
* Coaches
* Routes
* Schedules
* Seat Generation
* Fare
* Bookings
* Users
* Reports

---

# Backend Modules

## Authentication Module

Features

* JWT Authentication
* Refresh Token
* Role Based Authorization
* Password Encryption
* Forgot Password
* Reset Password

---

## User Module

* User Registration
* Profile
* Passenger Management

---

## Station Module

* Add Station
* Update Station
* Delete Station
* Search Station

---

## Train Module

* Add Train
* Update Train
* Coach Assignment
* Train Status

---

## Coach Module

Supports

* Sleeper
* AC Chair Car
* AC 3 Tier
* AC 2 Tier
* First AC

Automatically generates seats.

---

## Seat Generation Module

Automatically generates

* Seat Numbers
* Berth Type
* Seat Labels

according to coach configuration.

---

## Seat Availability Engine

Calculates

* Available Seats
* Occupied Seats
* Remaining Capacity

based on

* Schedule
* Journey Date
* Existing Bookings

---

## Reservation Engine

Core business logic

Workflow

Search Train

↓

Choose Schedule

↓

Choose Coach

↓

Enter Passenger Details

↓

Allocate Seats

↓

Calculate Fare

↓

Create Booking

↓

Generate PNR

↓

Payment

↓

Ticket Generation

---

## Seat Allocation Engine

Implements

* Family Seat Allocation
* Consecutive Seat Allocation
* Preferred Berth Allocation
* Lower Berth Priority (planned)
* RAC (planned)
* Waiting List (planned)

---

## Fare Engine

Calculates

* Base Fare
* Coach Charges
* Reservation Charges
* GST
* Discounts

---

## PNR Module

Generates

Unique 10-digit PNR

Supports

* PNR Lookup
* Journey Status

---

## Booking Module

Creates

* Booking
* Booking Passenger
* Seat Allocation

Maintains booking lifecycle

* Confirmed
* Cancelled
* Completed

---

## Payment Module

Supports

* Payment Initiation
* Payment Confirmation
* Refund
* Transaction History

---

## Notification Module

Sends

* Booking Confirmation
* Cancellation
* Refund
* Journey Reminder

---

## Admin Module

Admin can manage

* Master Data
* Booking Reports
* Revenue
* Passenger Analytics

---

# Security

* JWT Authentication
* BCrypt Password Encryption
* Role-Based Authorization
* Stateless Session Management
* Input Validation
* Global Exception Handling

---

# Database Design

Contains approximately **15+ relational entities** with:

* One-to-One relationships
* One-to-Many relationships
* Many-to-One relationships
* Foreign Key Constraints
* Transactional consistency using Spring Data JPA

---

# Architecture

```text
React Frontend
        │
        ▼
REST API
        │
        ▼
Spring Security (JWT)
        │
        ▼
Controllers
        │
        ▼
Services
        │
        ▼
Booking Orchestrator
        │
        ▼
Repositories (JPA)
        │
        ▼
PostgreSQL Database
```

---

# Key Features

* Secure JWT Authentication
* Train Search & Schedule Management
* Real-Time Seat Availability
* Automatic Seat Generation
* Intelligent Seat Allocation
* Digital Ticket with PNR
* Booking History
* Role-Based Admin Panel
* RESTful API Architecture
* Swagger API Documentation
* Layered Spring Boot Architecture

---

# Future Enhancements

* RAC & Waiting List Automation
* Live Train Tracking
* Payment Gateway Integration (Razorpay/Stripe)
* Email & SMS Notifications
* Redis Caching
* Docker Deployment
* Microservices Architecture
* CI/CD Pipeline
* Analytics Dashboard
* AI-Based Seat Recommendation
