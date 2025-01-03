# Microservices Architecture Documentation

This repository outlines the architecture, database structures, and API specifications for a microservices-based application comprising three primary services:

1. [Authentication Service](#1-authentication-service)
2. [Message Service](#2-message-service)
3. [Encryption Service](#3-encryption-service)

Each service is responsible for distinct functionalities, ensuring a modular and scalable system. Below is a comprehensive overview of each service, including their database schemas and API endpoints.

---

## Table of Contents

1. [Authentication Service](#1-authentication-service)
    - [Database Structure](#11-database-structure)
    - [API Endpoints](#12-api-endpoints)
2. [Message Service](#2-message-service)
    - [Database Structure](#21-database-structure)
    - [API Endpoints](#22-api-endpoints)
3. [Encryption Service](#3-encryption-service)
    - [API Endpoints](#32-api-endpoints)
5. [Setup and Configuration](#5-setup-and-configuration)


---

## 1. Authentication Service

The **Authentication Service** handles user registration, authentication, and JWT token management. It ensures secure access control across the microservices architecture.

### 1.1. Database Structure

#### **User Table**

| Column Name  | Data Type     | Constraints                         | Description                          |
|--------------|---------------|-------------------------------------|--------------------------------------|
| `id`         | BIGINT        | PRIMARY KEY, AUTO_INCREMENT         | Unique identifier for each user.     |
| `username`   | VARCHAR(50)   | UNIQUE, NOT NULL                    | User's unique username.              |
| `passwordHash` | VARCHAR(255) | NOT NULL                            | Hashed password using BCrypt.        |




#### **User Roles Table**

roles are stored in a separate table for normalization.

| Column Name | Data Type   | Constraints                     | Description                   |
|-------------|-------------|---------------------------------|-------------------------------|
| `user_id`   | BIGINT      | FOREIGN KEY REFERENCES User(id) | Identifier linking to User.   |
| `role`      | VARCHAR(50) | NOT NULL                        | Role assigned to the user.    |

### 1.2. API Endpoints

#### **1.2.1. Register User**

- **Endpoint:** `/api/auth/register`
- **Method:** `POST`
- **Description:** Registers a new user with a username, password, and role.
- **Authentication:** **None** (Public Endpoint)

##### **Request Body**

```json
{
  "username": "newuser",
  "password": "SecurePass123",
  "role": "message_writer"
}
```

- **Fields:**
    - `username` (string, required): Desired unique username.
    - `password` (string, required): Password (minimum 6 characters).
    - `role` (string, required): Role to assign (`message_writer` or `message_reader`).

##### **Responses**

- **Success (201 Created):**

  ```json
  {
    "message": "User registered successfully",
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
  ```

    - **Fields:**
        - `message` (string): Success message.
        - `token` (string): JWT token for authenticated access.

- **Conflict (409 Conflict):**

  ```json
  {
    "message": "Username is already taken"
  }
  ```

- **Validation Errors (400 Bad Request):**

  ```json
  {
    "timestamp": "2025-01-01T12:00:00",
    "status": 400,
    "errors": {
      "username": "Username is mandatory",
      "password": "Password is mandatory",
      "role": "Role must be either message_writer or message_reader"
    }
  }
  ```

#### **1.2.2. Authenticate User (Obtain JWT Token with)**

- **Endpoint:** `/api/auth/token`
- **Method:** `POST`
- **Description:** Authenticates a user and issues a JWT token.
- **Authentication:** **None** (Public Endpoint)

##### **Request Body**

```json
{
  "username": "existinguser",
  "password": "Password123"
}
```

- **Fields:**
    - `username` (string, required): Existing username.
    - `password` (string, required): User's password.

##### **Responses**

- **Success (200 OK):**

  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
  ```

    - **Fields:**
        - `token` (string): JWT token for authenticated access.

- **Unauthorized (401 Unauthorized):**

  ```json
  {
    "message": "Invalid username or password"
  }
  ```

- **User Not Found (404 Not Found):**

  ```json
  {
    "message": "User not found"
  }
  ```

---

## 2. Message Service

The **Message Service** manages sending and retrieving encrypted messages. It interacts with the Encryption Service to handle encryption and decryption processes.

### 2.1. Database Structure

#### **Message Table**

| Column Name        | Data Type | Constraints                         | Description                              |
|--------------------|-----------|-------------------------------------|------------------------------------------|
| `id`               | BIGINT    | PRIMARY KEY, AUTO_INCREMENT         | Unique identifier for each message.      |
| `originalMessage`  | TEXT    | NOT NULL                            | unencrypted content of the message.    |
| `encryptedMessage` | TEXT    | NOT NULL                            | The encrypted content of the message.    |
| `encryptionType`   | VARCHAR(10) | NOT NULL                          | Type of encryption used (`AES` or `RSA`). |
| `userId`           | BIGINT    | NOT NULL, FOREIGN KEY REFERENCES User(id) | Identifier of the user who sent the message. |
| `createdAt`        | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP           | Time when the message was sent.          |

**Notes:**
- **Foreign Key:** `userId` links to the `User` table in the Authentication Service.
- **Encryption Types:** Currently supports `AES` and `RSA`.

### 2.2. API Endpoints

#### **2.2.1. Send Encrypted Message**

- **Endpoint:** `/api/message`
- **Method:** `POST`
- **Description:** Sends a message by encrypting it using the specified encryption type.
- **Authentication:** **Required** (`message_writer` role)

##### **Request Headers**

- `Authorization: Bearer <JWT_TOKEN>`

##### **Request Body**

```json
{
  "message": "Hello, World!",
  "encryptionType": "AES"
}
```

- **Fields:**
    - `message` (string, required): The plaintext message to be encrypted.
    - `encryptionType` (string, required): Type of encryption (`AES` or `RSA`).

##### **Responses**

- **Success (200 OK):**

  ```json
  {
    "status": "Success",
    "encryptedMessage": "Base64EncodedEncryptedMessage",
    "messageId": 1
  }
  ```

    - **Fields:**
        - `status` (string): Operation status.
        - `encryptedMessage` (string): The encrypted message in Base64 format.
        - `messageId` (long): Unique identifier of the stored message.

- **Bad Request (400 Bad Request):**

  ```json
  {
    "status": "Failure",
    "encryptedMessage": null,
    "messageId": null,
    "error": "Encryption type must be AES or RSA"
  }
  ```

- **Unauthorized (401 Unauthorized):**

  ```json
  {
    "message": "Full authentication is required to access this resource"
  }
  ```

#### **2.2.2. Retrieve Encrypted Message**

- **Endpoint:** `/api/message/{id}`
- **Method:** `GET`
- **Description:** Retrieves an encrypted message by its ID.
- **Authentication:** **Required** (`message_reader` role)

##### **Request Headers**

- `Authorization: Bearer <JWT_TOKEN>`

##### **Path Parameters**

- `id` (long, required): Unique identifier of the message to retrieve.

##### **Responses**

- **Success (200 OK):**

  ```json
  {
    "messageId": 1,
    "encryptedMessage": "Base64EncodedEncryptedMessage"
  }
  ```

    - **Fields:**
        - `messageId` (long): Unique identifier of the message.
        - `encryptedMessage` (string): The encrypted message in Base64 format.

- **Not Found (404 Not Found):**

  ```json
  {
    "messageId": null,
    "encryptedMessage": null,
    "error": "Message not found"
  }
  ```

- **Unauthorized (401 Unauthorized):**

  ```json
  {
    "message": "Full authentication is required to access this resource"
  }
  ```

- **Forbidden (403 Forbidden):**

  ```json
  {
    "timestamp": "2025-01-01T12:00:00",
    "status": 403,
    "error": "Forbidden",
    "message": "Access is denied",
    "path": "/api/message/1"
  }
  ```

---

## 3. Encryption Service

The **Encryption Service** is responsible for encrypting and decrypting messages. It ensures that message content is securely transformed based on the specified encryption type.


### 3.1. API Endpoints

#### **3.1.1. Encrypt Message**

- **Endpoint:** `/api/encrypt`
- **Method:** `POST`
- **Description:** Encrypts a plaintext message using the specified encryption type.
- **Authentication:** **Required** (`message_writer` role)

##### **Request Headers**

- `Authorization: Bearer <JWT_TOKEN>`

##### **Request Body**

```json
{
  "message": "Hello, World!",
  "encryptionType": "AES"
}
```

- **Fields:**
    - `message` (string, required): The plaintext message to be encrypted.
    - `encryptionType` (string, required): Type of encryption (`AES` or `RSA`).

##### **Responses**

- **Success (200 OK):**

  ```json
  {
    "encryptedMessage": "Base64EncodedEncryptedMessage"
  }
  ```

    - **Fields:**
        - `encryptedMessage` (string): The encrypted message in Base64 format.

- **Bad Request (400 Bad Request):**

  ```json
  {
    "status": "Failure",
    "encryptedMessage": null,
    "messageId": null,
    "error": "Encryption type must be AES or RSA"
  }
  ```

- **Unauthorized (401 Unauthorized):**

  ```json
  {
    "message": "Full authentication is required to access this resource"
  }
  ```

#### **3.1.2. Decrypt Message**

- **Endpoint:** `/api/encrypt/decrypt`
- **Method:** `POST`
- **Description:** Decrypts an encrypted message using the specified encryption type.
- **Authentication:** **Required** (`message_reader` role)

##### **Request Headers**

- `Authorization: Bearer <JWT_TOKEN>`

##### **Request Body**

```json
{
  "encryptedMessage": "Base64EncodedEncryptedMessage",
  "encryptionType": "AES"
}
```

- **Fields:**
    - `encryptedMessage` (string, required): The encrypted message in Base64 format.
    - `encryptionType` (string, required): Type of encryption used (`AES` or `RSA`).

##### **Responses**

- **Success (200 OK):**

  ```json
  {
    "decryptedMessage": "Hello, World!"
  }
  ```

    - **Fields:**
        - `decryptedMessage` (string): The original plaintext message.

- **Bad Request (400 Bad Request):**

  ```json
  {
    "status": "Failure",
    "decryptedMessage": null,
    "error": "Invalid encryption type or malformed encrypted message"
  }
  ```

- **Unauthorized (401 Unauthorized):**

  ```json
  {
    "message": "Full authentication is required to access this resource"
  }
  ```

- **Forbidden (403 Forbidden):**

  ```json
  {
    "timestamp": "2025-01-01T12:00:00",
    "status": 403,
    "error": "Forbidden",
    "message": "Access is denied",
    "path": "/api/encrypt/decrypt"
  }
  ```

---

## 4. Common Components

### 4.1. JWT Token Structure

JWT (JSON Web Token) is used for securing API endpoints across all services. Each token includes the following claims:

- **Subject (`sub`):** Username of the authenticated user.
- **Roles (`roles`):** Comma-separated list of roles assigned to the user (e.g., `MESSAGE_WRITER, MESSAGE_READER`).
- **User ID (`userId`):** Unique identifier of the user.
- **Issued At (`iat`):** Timestamp when the token was issued.
- **Expiration (`exp`):** Token expiration timestamp.

**Example Payload:**

```json
{
  "sub": "john_doe",
  "roles": "MESSAGE_WRITER,MESSAGE_READER",
  "userId": 1,
  "iat": 1705720645,
  "exp": 1705807045
}
```

### 4.2. Roles and Permissions

Roles define the permissions a user has within the system:

- **`MESSAGE_WRITER`:** Allows the user to send (write) messages.
- **`MESSAGE_READER`:** Allows the user to retrieve (read) messages.

**Role Assignment Rules:**

- **Registration:** Users can be assigned one of the predefined roles during registration.
- **Authorization:** API endpoints are protected based on roles using `@PreAuthorize` annotations.

**Examples:**

- **Send Message Endpoint:**

  ```java
  @PreAuthorize("hasRole('MESSAGE_WRITER')")
  ```

- **Retrieve Message Endpoint:**

  ```java
  @PreAuthorize("hasRole('MESSAGE_READER')")
  ```

---

## 5. Setup and Configuration

### 5.1. Prerequisites

- **Java Development Kit (JDK) 17 or higher**
- **Maven Build Tool**
- **Databases:**
    - **Authentication Service:** Relational Database (PostgreSQL)
    - **Message Service:** Relational Database (PostgreSQL)
- **Environment Variables:**
    - `JWT_SECRET`,`JWT_EXPIRATION_MS`: Secret key for signing JWT tokens, Validity of token
    - `ENCRYPTION_SERVICE_URL`: URL of the Encryption Service.
    - `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`: Database connection details.
    - `ENCRYPTION_AES_KEY`,`ENCRYPTION_RSA_PUBLIC_KEY`,`ENCRYPTION_RSA_PRIVATE_KEY` : Encryption keys

### 5.2. Running the Services

Each service can be run independently. Ensure that the necessary environment variables are set before starting each service.

**Example Using Maven:**

```bash
# Navigate to Authentication Service directory
cd authentication-service
mvn spring-boot:run

# Navigate to Message Service directory
cd message-service
mvn spring-boot:run

# Navigate to Encryption Service directory
cd encryption-service
mvn spring-boot:run
```

**Note:** Replace `mvn` with `./mvnw`  if using Maven Wrapper .

### 5.3. Configuring Feign Clients

The **Message Service** uses Feign clients to communicate with the **Encryption Service**. Ensure that the Feign clients are correctly configured with the `FeignClientConfig` to include JWT tokens in requests.

**Feign Client Example:**

```java
@FeignClient(
    name = "encryption-service",
    url = "${encryption.service.url}",
    configuration = FeignClientConfig.class
)
public interface EncryptionClient {
    @PostMapping("/api/encrypt")
    EncryptionResponse encryptMessage(@RequestBody EncryptionRequest request);

    @PostMapping("/api/encrypt/decrypt")
    DecryptionResponse decryptMessage(@RequestBody DecryptionRequest request);
}
```
