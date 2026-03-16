# Medical Store System 🏥

A robust, enterprise-grade Pharmacy Management System built with **Java 17**, **JavaFX**, and **MySQL**. This application provides a comprehensive solution for managing inventory, point of sale (POS) transactions, suppliers, and customer relations using a strictly decoupled **MVC (Model-View-Controller)** architecture.

---

## 🌟 Core Features

- **Dynamic Master Dashboard**: A centralized BorderPane-based UI enabling seamless navigation between modules without multiple window popups.
- **Advanced Inventory Management**: 
    - Real-time stock tracking with batch-level granularity.
    - Automated reorder level monitoring and expiration management.
    - Category-based tax calculation (5%, 12%, etc., specialized for pharmaceuticals).
- **Interactive Point of Sale (POS)**:
    - Live search/filter for medicines by Name, Generic, or Category.
    - Shopping cart functionality with atomic SQL transactions to ensure data integrity during checkout.
    - Integrated customer lookup and automated invoice number generation.
- **Supplier & Customer Directories**: Comprehensive management of entities with transaction history tracking.
- **Role-Based Security**: Secured access for Admin and Staff roles with session management.
- **Transactional Safety**: All financial and inventory updates are wrapped in SQL Transactions (`COMMIT/ROLLBACK`), preventing partial data updates.

---

## 🏗️ Technical Architecture

### Tech Stack
- **Backend**: Java 17 (Core Logic), JDBC (MySQL Connector/J)
- **Frontend**: JavaFX (FXML + CSS for premium styling)
- **Database**: MySQL 8.0 (Relational Storage)
- **Build Tool**: Maven (Dependency Management)
- **Configuration**: Dotenv (Environment variable isolation)

### Build Process & Workflow
The project is built using **Maven**. The build flow follows these stages:
1. **Compilation**: Maven compiles Java source code and processes resources (FXML/CSS).
2. **Resource Injection**: FXML files are linked to their respective Controller classes via the `fx:controller` attribute.
3. **Database Migration**: On first launch, the `DatabaseMigration.java` utility initializes the `pharmacy_db` schema, ensuring indices, foreign keys, and default admin users are created.

---

## 🚀 Getting Started

### Prerequisites
- **Java JDK 17** or higher.
- **MySQL Server 8.0** (Running on port **4000** for this specific setup).
- **Maven** (A portable version is included in the environment if global Maven is missing).

### Database Initialization
1. Ensure your MySQL server is running.
2. Create the target database:
   ```sql
   CREATE DATABASE pharmacy_db;
   ```
3. The application will handle the table creation (`Users`, `Medicines`, `Suppliers`, `Sales`, `Customers`) automatically upon the first successful connection.

### Configuration (`.env`)
Create a `.env` file in the root directory (copy from `.env.example`) and configure your local settings:
```env
DB_HOST=127.0.0.1
DB_PORT=4000
DB_NAME=pharmacy_db
DB_USER=your_root_user
DB_PASSWORD=your_password
```

---

## ▶️ Execution

### Using the Portable Launcher (Windows)
Double-click the `run.bat` file in the root directory. This script is pre-configured to:
- Use the local portable Maven.
- Compile the latest source code.
- Launch the JavaFX Application Thread.

### Manual Launch (Command Line)
If Maven is installed globally, run:
```powershell
mvn clean compile javafx:run
```

---

## 🔑 Login Credentials
- **Username**: `admin`
- **Password**: `admin123`
- **Role**: `Administrator`

---

## 📂 Project Structure
```text
PharmacySystem/
├── src/main/java/com/pharmacy/
│   ├── controllers/  # UI Logic (JavaFX Controllers)
│   ├── dao/          # Data Access Objects (SQL Queries)
│   ├── models/       # Data Objects (POJOs)
│   ├── utils/        # Database Connection & Migrations
│   └── Main.java     # Entry Point
├── src/main/resources/com/pharmacy/
│   ├── views/        # FXML Layouts
│   └── styles/       # CSS Design Tokens
├── run.bat           # Windows Runner Script
└── pom.xml           # Project Dependencies
```

---
*Developed for Pharmacy/Medical Store environment study.*
