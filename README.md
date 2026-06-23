# 🎟️ Ticketing System CLI

A Java-based Command Line Interface (CLI) application for managing venues, events, seats, and reservations.

This project was developed as a backend engineering learning challenge to practice:
- Object-Oriented Programming (OOP)
- Design Patterns (Factory, Strategy)
- Data Validation
- Concurrency Control
- Idempotency
- Caching
- Database Persistence (PostgreSQL)
- JSON Import/Export
- Unit Testing (JUnit 5)

---

## 📌 Overview

The Ticketing System CLI simulates a real-world event booking platform.

### System Roles:
- 👨‍💼 Operator/Admin: manages venues, events, and seats
- 👤 Customer: reserves and manages tickets

The system ensures:
- No double booking
- Safe concurrent reservations
- Accurate pricing calculation
- Persistent data storage

---

## 🚀 Features

### 👤 User Management
- User registration
- Login system
- Role-based access control

---

### 🏟️ Venue Management
- Create venues
- List venues
- Store address and timezone

---

### 🎫 Event Management
- Create events linked to venues
- View events
- Prevent time conflicts
- Event status tracking (ACTIVE / CANCELLED / COMPLETED)

---

### 💺 Seat Management
- Add seats to events
- VIP / REGULAR classification
- Row-based seating system
- View available & reserved seats

---

### 📅 Reservation System
- Reserve seats
- Cancel reservations
- View reservation history
- Prevent duplicate bookings (idempotency)

---

### ⚡ Concurrency Control
- Transaction-based booking system
- Row-level locking (`SELECT FOR UPDATE`)
- Prevent race conditions during seat reservation

---

### 🔁 Idempotency
Prevents duplicate reservations using:

email + eventId + seatId


---

### 💰 Pricing System (Strategy Pattern)
Dynamic pricing strategies:

- Base pricing
- VIP pricing
- Early bird discounts
- Time-based pricing

---

### 🏭 Factory Pattern
Used for object creation:

- EventFactory
- SeatFactory
- UserFactory
- PricingStrategyFactory

---

### ⚡ Caching System
In-memory caching layer to reduce database calls:

- EventCache
- UserCache
- VenueCache
- SeatCache

---

### 📦 Data Validation
Input validation includes:

- ID validation
- Name validation
- Address validation
- Time validation
- Price validation
- Timezone validation

---

### 📤 JSON Import / Export
- Export system data to JSON files
- Import data from backups
- Supports system recovery and migration

---

### 🧪 Testing
- JUnit 5 tests
- Service layer testing
- Reservation validation tests

---

## 🧱 Project Structure


src
├── main
│ └── java/com/ticketing
│ ├── dao
│ ├── database
│ ├── dto
│ ├── enums
│ ├── exception
│ ├── factory
│ ├── model
│ ├── service
│ ├── strategy
│ ├── cache
│ ├── storage
│ ├── ui
│ ├── controller
│ └── util
│
└── test
└── java


---

## 🛠️ Technologies Used

- Java 24
- Maven
- PostgreSQL
- JDBC
- Jackson (JSON Processing)
- Jackson JSR310 (LocalDateTime support)
- JUnit 5
- BCrypt
- Git & GitHub

---

## ⚙️ Setup Instructions

### 1. Clone Repository

```bash
git clone https://github.com/Godwork2921/ticketing-system-cli.git
cd ticketing-system-cli
2. Configure Database

Create PostgreSQL database:

CREATE DATABASE ticketing_system;

Update DB credentials:

src/main/java/com/ticketing/database/DBConnection.java

Example:

jdbc:postgresql://localhost:5432/ticketing_system
username: postgres
password: your_password
3. Build Project
mvn clean install
4. Run Application
mvn exec:java

OR

java -cp target/classes com.ticketing.Main
📂 Database Tables
users
venues
events
seats
reservations
📤 JSON Export Files
users.json
venues.json
events.json
seats.json
reservations.json
🧠 Architecture Overview
Layered Architecture
Controller → Service → DAO → Database
Design Patterns Used
🏭 Factory Pattern

Used for object creation (Event, User, Seat, Pricing)

🎯 Strategy Pattern

Used for pricing logic:

VIP Pricing
Early Bird
Standard Pricing
⚡ Concurrency Control
Transaction management
Row-level locking
🔁 Idempotency
Prevent duplicate reservations
⚠️ Assumptions
Each seat belongs to one event only
A seat cannot be reserved twice
Events must belong to a venue
Valid JSON structure is required
PostgreSQL is configured before startup
🚧 Challenges Solved
Race condition handling in reservations
Event time overlap detection
Complex object relationships
JSON + database synchronization
Service layer testing
🚀 Future Improvements
Spring Boot migration
REST API development
JWT authentication
Redis caching
Kafka event streaming
Docker containerization
Monitoring & logging system
📊 Project Status
Feature	Status
OOP Design	✅ Done
Factory Pattern	✅ Done
Strategy Pattern	✅ Done
Concurrency	✅ Done
Idempotency	✅ Done
Caching	 ✅ Done
Validation	✅ Done
Lombok	✅ Done
