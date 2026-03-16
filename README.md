# Medical Store System 🏥

A full-fledged desktop application built with **JavaFX** and **MySQL** that implements a complete Model-View-Controller (MVC) architecture, specifically designed for modern pharmacy and medical store management.

This project serves as a comprehensive case study touching on Object-Oriented Programming (OOP) principles, relational database management, dynamic UI rendering, and transactional stock control.

## 🚀 Key Features
- **Rebranded Experience**: Fully customized for "Medical Store System" with a professional UI.
- **Localized Billing**: All transactions and reports are processed in **Indian Rupee (₹)**.
- **Customer Management**: track patient/customer names and contact numbers during checkout.
- **Purchase History**: View a detailed log of medicines bought by any customer in a dedicated directory.
- **Global Search**: Search for medicines, suppliers, or system activities from anywhere in the app.
- **Real-Time POS Filter**: Instantly filter available medicines by Name, Generic, or Category at the Point of Sale.
- **Inventory Control**: Advanced batch tracking, reorder levels, and expiration alerts.
- **User Authentication**: Secure login system with dynamic session handling for Admin and Staff.
- **Audit Trail**: Automated logging of all critical system activities for transparency.

## 🛠️ Tech Stack
- **Language**: Java 17+
- **GUI Framework**: JavaFX (FXML + CSS)
- **Database**: MySQL 8.0+
- **Build Tool**: Maven
- **Secret Management**: Dotenv (io.github.cdimascio:dotenv-java)

## 🗄️ Database Setup
The system now includes an **Automatic Migration Utility** that sets up your tables on the first run.

1. Open your MySQL client (e.g., MySQL Workbench).
2. Create the database:
   ```sql
   CREATE DATABASE pharmacy_db;
   ```
3. Ensure your credentials are set in the `.env` file (see Configuration below). 
4. **Launch the app**: The system will automatically create the `Users`, `Suppliers`, `Medicines`, `Sales`, `Sale_Items`, and `Customers` tables for you.

## 🔑 Default Admin Credentials
The system seeds a default administrator account on first run:

- **Username**: `admin`
- **Password**: `admin123`
- **Role**: `ADMIN`

## ⚙️ Configuration
The system relies on an environment file to securely connect to your database.
1. Rename the `.env.example` file in the root directory to exactly `.env`.
2. Update the credentials inside to match your local MySQL configuration:
   ```env
   DB_HOST=localhost
   DB_PORT=3306
   DB_USER=root
   DB_PASSWORD=root
   ```

## ▶️ Running the Application
The project is configured as a Maven application.

### Option 1: Using the Launcher (Windows)
Double-click `run.bat` in the project root to automatically configure Maven and launch the store system.

### Option 2: Using Maven Command Line
Navigate to the root directory where `pom.xml` is located and execute:
```powershell
mvn clean compile javafx:run
```
