# Leave Management System

A simple leave management web app where:
- HR admin creates user, set and adjust employee leave credits and approve or reject leave requests.
- Managers approve or reject leave requests and apply own leave.
- Employees file leave requests.

## Tech Stack
- Backend: Spring Boot
- Frontend: Angular
- Database: PostgreSQL

## How it works
- HR creates user and sets a user’s leave credits, view active leave requests leave history reviews and approves or rejects.
- Employee submits a leave request and view own leave.
- The system computes the request’s `numberOfDays` and marks it as `PENDING`.
- Manager reviews and approves or rejects his/her employee and view own leave.
- On application, leave credits are deducted; on rejection, credits restore.

## Prerequisites
- [Install JDK 21](https://docs.oracle.com/en/java/javase/21/install/index.html)
- [Install Node.js 18](https://nodejs.org/en/download/) and [Angular CLI](https://angular.dev/tools/cli/setup-local) for the frontend
- [Install PostgreSQL 17](https://www.tigerdata.com/blog/how-to-install-psql-on-mac-ubuntu-debian-windows)
- [Install Gradle](https://gradle.org/install/)

## Project layout (backend)
- Code: `src/main/java/com/synacy/trainee/leavemanagementsystem`
- Config: `src/main/resources/application.properties`
- Build: `build.gradle`
- Entry point: `LeaveManagementSystemApplication.java`

## Database setup (PostgreSQL)
Create a database.

```bash
# As the postgres user
sudo -U postgres psql

CREATE DATABASE leave_management;
\q
```

Configure the backend to use PostgreSQL.

Spring datasource and JPA settings.
```properties
# file: 'src/main/resources/application.properties'
spring.application.name=leave-management-system
spring.datasource.url=jdbc:postgresql://localhost:5432/leave_management
spring.datasource.username=postgres
spring.datasource.password=root
spring.jpa.show-sql=true

# Hibernate ddl auto (create, create-drop, validate, update)
spring.jpa.hibernate.ddl-auto=create
```

## Run the backend
Build and run with Gradle wrapper.
```bash
chmod +x ./gradlew
./gradlew clean build
./gradlew bootRun
```
## API overview

- Users
  - `GET /api/v1/user/paginated` \- get all paginated list of users
  - `GET /api/v1/user` \- get all list of users
  - `GET /api/v1/user` \- get all list of users
  - `POST /api/v1/user/{id}` \- get user by id
    - Body: `name`, `role`, `leaveCredits`, `managerId`
  - `PUT /api/v1/user/{id}` \- update user
  - `GET /api/v1/user/managers` \- get all managers

- Leave Applications
  - `POST /api/v1/leave-application` \- create a leave application
    - Body: `userId`, `startDate`, `endDate`, `reason`
  - `GET /api/v1/leave-application` \- list leave application
  - `GET /api/v1/leave-application/active` \- get pageable leave application by status
  - `GET /api/v1/leave-application/history` \- get pageable leave application by status not(status)
  - `GET /api/v1/leave-application/{managerId}/active` \- get pageable leave application by userId and status
  - `GET /api/v1/leave-application/{managerId}/history` \- get pageable leave application by userId and status not(status)
  - `PUT /api/v1/leave-application/{id}/status` \- update leave application by leaveId                                                                                    
  - `GET /api/v1/leave-application/{userId}` \- get pageable leave application by userId

## Testing
Run backend tests.
```bash
./gradlew test or ./gradlew build
```

## Link the Angular frontend to the backend
- [Frontend repository](https://github.com/troy-synacy/synacy-trainee-leave-system.git)
