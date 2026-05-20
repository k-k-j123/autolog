

# 🚗 Autolog – Vehicle Management & Tracking System

Autolog is a **Spring Boot-based vehicle management and mileage tracking application** designed to help users manage vehicles, track usage, and monitor travel history securely.

This project is built using **Java, Spring Boot, JPA, Security, JWT, Thymeleaf, and MySQL**.

---

## 📌 Tech Stack

* **Backend:** Java + Spring Boot
* **Database:** MySQL
* **ORM:** Spring Data JPA / Hibernate
* **Security:** Spring Security + JWT Authentication
* **Frontend (Template Engine):** Thymeleaf
* **Build Tool:** Maven
* **Utilities:** Lombok

---

## 📂 Project Structure

```
Autolog/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │
│   └── test/
│
├── pom.xml
├── HELP.md
└── target/
```

---

## ⚙️ Features

* 🔐 User Authentication using JWT
* 🚘 Vehicle Registration & Management
* 📊 Mileage / Usage Tracking
* 🗄️ Database integration with MySQL
* 🛡️ Secured endpoints using Spring Security

---

## 🚀 How to Run the Project

### 1️⃣ Clone the Repository

```bash
git clone -b Spring https://github.com/k-k-j123/autolog.git
cd autolog
```

---

### 2️⃣ Configure Database

Create a MySQL database:

```sql
CREATE DATABASE autolog;
```

Update your `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/autolog
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

### 3️⃣ Build the Project

```bash
mvn clean install
```

---

### 4️⃣ Run the Application

```bash
mvn spring-boot:run
```

OR

```bash
java -jar target/Autolog-0.0.1-SNAPSHOT.jar
```

---

### 5️⃣ Access the Application

Open your browser:

```
http://localhost:8080
```

---

## 🔐 Authentication Flow

* User registers / logs in
* JWT token is generated
* Token is used to access secured endpoints
* Spring Security validates the token

---

## 🛠️ Future Improvements

* 📊 Dashboard with analytics & charts
* 📍 GPS integration for live tracking
* 📄 Export vehicle logs as PDF/CSV
* ☁️ Deployment to cloud (AWS / Render / Railway)

---

## 👨‍💻 Author

**Kaushik Krishnakumar Joshi**
B.Sc. Computer Science

---

## 📜 License

This project is for academic and learning purposes.

