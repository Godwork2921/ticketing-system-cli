# Ticketing System CLI

A Java-based Command Line Interface (CLI) application for managing venues, events, seats, and reservations. The project was developed as part of a backend engineering learning challenge to practice Java fundamentals, Object-Oriented Programming, Collections, Exception Handling, JSON processing, database persistence, and automated testing.

---

# Overview

This application allows administrators to create venues, events, and seats, while customers can reserve seats, view reservations, and cancel reservations.

The system supports:

* User authentication
* Role-based access (Admin & Customer)
* PostgreSQL database persistence
* JSON import/export functionality
* Reservation management
* Automated testing using JUnit

---

# Features

## User Management

* User registration
* User login
* Role-based access control

## Venue Management

* Create venues
* View venues

## Event Management

* Create events
* View events
* Associate events with venues

## Seat Management

* Add seats to events
* View available seats
* View reserved seats

## Reservation Management

* Reserve seats
* Cancel reservations
* View reservations
* Prevent double-booking

## Data Persistence

* PostgreSQL database storage
* JSON export
* JSON import

## Testing

* Unit tests using JUnit 5
* Service layer testing
* Reservation validation testing

---

# Technologies Used

* Java 24
* Maven
* PostgreSQL
* Jackson Databind
* Jackson JSR310 (Java Time API Support)
* JUnit 5
* Git
* GitHub

---

# Project Structure

```text
src
├── main
│   └── java
│       └── com.ticketing
│           ├── dao
│           ├── database
│           ├── enums
│           ├── exception
│           ├── model
│           ├── repository
│           ├── service
│           ├── session
│           ├── storage
│           ├── ui
│           └── util
│
└── test
    └── java
        └── service
```

---

# Database Tables

The application uses PostgreSQL and contains the following tables:

* users
* venues
* events
* seats
* reservations

---

# Setup Instructions

## Prerequisites

Install the following:

* Java JDK 24
* Maven
* PostgreSQL
* Git

Verify installation:

```bash
java --version
mvn --version
psql --version
```

---

## Clone Repository

```bash
git clone https://github.com/Godwork2921/ticketing-system-cli.git

cd ticketing-system-cli
```

---

## Configure Database

Create a PostgreSQL database:

```sql
CREATE DATABASE ticketing_system;
```

Update database credentials in:

```text
src/main/java/com/ticketing/database/DBConnection.java
```

Example:

```java
private static final String URL =
    "jdbc:postgresql://localhost:5432/ticketing_system";

private static final String USER = "postgres";

private static final String PASSWORD = "your_password";
```

---

## Build Project

```bash
mvn clean install
```

---

## Run Application

```bash
mvn exec:java
```

or

```bash
java -cp target/classes com.ticketing.Main
```

---

# Running Tests

Run all tests:

```bash
mvn test
```

Sample output:

```text
Tests run: 10
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

---

# JSON Files

The application supports exporting and importing data using:

```text
users.json
venues.json
events.json
seats.json
reservations.json
```

These files are generated in the project root directory.

---

# Design Notes

The application follows a layered architecture:

## Model Layer

Represents domain entities:

* User
* Venue
* Event
* Seat
* Reservation

## DAO Layer

Responsible for database access and CRUD operations.

Examples:

* UserDAO
* EventDAO
* SeatDAO
* ReservationDAO

## Service Layer

Contains business logic and validation.

Examples:

* AuthService
* EventService
* SeatService
* ReservationService

## Storage Layer

Handles JSON export and import operations.

Examples:

* JsonExporter
* JsonImporter
* JsonStorageService

## UI Layer

Provides command-line interaction through menus and controllers.

---

# Assumptions

The following assumptions were made during development:

* Each seat belongs to exactly one event.
* A seat cannot be reserved more than once simultaneously.
* Reservations require a valid customer account.
* Events must be associated with an existing venue.
* JSON files are assumed to contain valid data structures.
* PostgreSQL is available and configured correctly before application startup.

---

# Challenges Encountered

During development, the following challenges were addressed:

* Preventing duplicate seat reservations.
* Synchronizing JSON export/import with database records.
* Handling LocalDateTime serialization and deserialization using Jackson.
* Managing relationships between events, venues, seats, and reservations.
* Creating automated tests for service-layer functionality.

---

# Future Improvements

Planned future enhancements include:

* Spring Boot migration
* REST API development
* Docker containerization
* JWT authentication
* Kafka integration
* Keycloak integration
* MinIO integration
* Concurrency handling
* Audit logging
* Monitoring and observability tooling

---

# Test Results

Current automated test status:

* Total Tests: 10
* Failures: 0
* Errors: 0
* Status: PASSING

---

