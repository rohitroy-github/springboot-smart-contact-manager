# Smart Contact Manager

**Smart Contact Manager** is a Spring Boot-based application that helps users manage their personal and professional contacts with ease. It features secure user authentication and provides a seamless interface to add, update, delete, and view contacts.

## Features

- **User Management**: Create, update, and manage user profiles.
- **Contact Management**: Add, edit, delete, and list contacts.
- **Authentication and Authorization**: Secure login and user verification.
- **RESTful APIs**: Easy integration with other systems.
- **Search and Filter**: Quickly find contacts by name, email, or phone number.
- **Profile Management**: Update personal information, profile picture, and settings.

---

## Tech Stack

- **Backend**: Spring Boot
- **Database**: MySQL
- **Frontend**: Thymeleaf
- **Build Tool**: Maven
- **Language**: Java
- **ORM**: JPA / Hibernate

---

## Snapshots

| ![Snapshot 1](https://github.com/user-attachments/assets/7027107d-c867-4d10-8039-a1a367366666) | ![Snapshot 2](https://github.com/user-attachments/assets/d3bf2f29-575b-43a8-bc08-dc196c87c247) |
|--------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------|
| ![Snapshot 3](https://github.com/user-attachments/assets/0100794e-45bd-42f4-96a6-d232fa9d5fcd) | ![Snapshot 4](https://github.com/user-attachments/assets/c30cc102-10b2-430f-aec8-462bbbc7a270) |

---

## Getting Started

### Prerequisites

- **Java**: JDK 11 or higher
- **MySQL**: Installed and running
- **Maven**: Installed
- **IDE**: IntelliJ IDEA, Eclipse, or any Java IDE 

### Installation

- First, clone the repository to your local system using the following command:

```
git clone <repository-url>
```

- Navigate into the project directory using the cd command:

```
cd scm
```

- Install the required frontend dependencies for the project by running:

```
npm install
```

- Create a new database named scm_db by running the following SQL command:

```
CREATE DATABASE scm_db;
```

- Make a copy of the [.env.example] file and name it [.env] and open the [.env] file and replace all the placeholder values (like GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET, etc.) with your actual credentials.

- Run the Spring Boot application. Start the project using Maven by running: 

```
./mvnw spring-boot:run
```

- Once the application is running, open your browser and go to the following URL:

```
http://localhost:8081
```