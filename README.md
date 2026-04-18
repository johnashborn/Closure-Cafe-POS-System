# ☕ Closure Cafe — Point of Sale System

> *"Where endings taste a little sweeter"*

A desktop Point of Sale (POS) system built for **Closure Cafe**, developed as a school project for the **Bachelor of Science in Information Technology** program at **Bohol Island State University – Balilihan Campus**.

---

## 📋 Overview

Closure Cafe POS is a JavaFX-based desktop application that handles day-to-day cafe operations. It supports two user roles — **Admin** and **Cashier** — each with their own dedicated interface. Cashiers can take orders and process transactions, while admins manage the product catalog and review transaction history.

---

## ✨ Features

### 🧑‍💼 Authentication
- PIN-based employee login
- Role-based access control (`Admin` / `Cashier`)
- Session management with login/logout timestamps

### 🧾 Cashier — POS Menu
- Dynamic product menu loaded from the database
- Category filtering (Coffee, Frappes, Desserts, Pastries)
- Live search bar for quick item lookup
- Product cards with image, name, description, and price
- Add to order, adjust quantity, or remove items
- Real-time order summary with subtotal, 12% VAT, and total
- Place Order — saves full transaction to the database
- Void Order — clears the current order
- Auto-incrementing order number display

### 🛠️ Admin Panel
- Add, edit, and delete products
- Upload product images
- Toggle product availability (soft delete — preserves transaction history)
- View transaction history with breakdown per order

### 🗃️ Database
- MySQL via XAMPP
- Transactions stored with full item-level detail (quantity, unit price at time of sale, subtotal)
- 12% VAT computed and stored per transaction

---

## 🖥️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17+ |
| UI Framework | JavaFX |
| UI Design | Scene Builder |
| IDE | Apache NetBeans |
| Database | MySQL 8 (via XAMPP) |
| DB Connectivity | JDBC — MySQL Connector/J |
| Build Tool | Apache Maven |

---

## 🗄️ Database Schema

```sql
Users               — employee accounts with role and PIN
Products            — menu items with category, price, image, and availability
Transactions        — order header (cashier, date, subtotal, tax, total)
TransactionItems    — per-item breakdown (product, quantity, unit price, subtotal)
```

---

## 🚀 Getting Started

### Prerequisites

- [Java JDK 17+](https://www.oracle.com/java/technologies/downloads/)
- [Apache NetBeans 19+](https://netbeans.apache.org/)
- [XAMPP](https://www.apachefriends.org/) (MySQL + Apache)
- [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/) `.jar` added to project libraries

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/closure-cafe-pos.git
cd closure-cafe-pos
```

### 2. Set Up the Database

1. Start **XAMPP** and turn on **MySQL**
2. Open `http://localhost/phpmyadmin`
3. Click **Import** → **Choose File** → select `closurecafe.sql` from the project root
4. Click **Go**

### 3. Configure the Database Connection

Open `src/com/mycompany/closurecafepos/DataBaseConnection.java` and update the credentials if needed:

```java
private static final String URL  = "jdbc:mysql://localhost:3306/closurecafe";
private static final String USER = "root";
private static final String PASS = "";  // XAMPP default is empty
```

### 4. Run the Project

Open the project in **Apache NetBeans** and press **Run** (`F6`), or build with Maven:

```bash
mvn javafx:run
```

---

## 📁 Project Structure

```
src/
└── com/mycompany/closurecafepos/
    ├── App.java                  — entry point
    ├── DataBaseConnection.java   — MySQL connection manager
    ├── SessionManager.java       — logged-in user session
    ├── MenuController.java       — cashier POS screen
    ├── AdminController.java      — admin panel
    ├── Products.java             — product model
    ├── OrderItem.java            — order item model
    └── ...

resources/
├── fxml/
│   ├── EmployeeLogin.fxml
│   ├── Menu.fxml
│   └── Admin.fxml
├── styles/
│   └── menu.css
└── images/
    └── ...
```

---

## 👤 Author

**John Rey Aranton**
BSIT Student — Bohol Island State University, Balilihan Campus

---

## 📝 License

This project was developed for academic purposes only and is not intended for commercial use.
