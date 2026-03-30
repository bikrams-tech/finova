# FINOVA

**FINOVA** is a modern desktop business management platform built with JavaFX for handling business operations including inventory, accounting, sales, employee management, and financial reporting in a unified system.

It is designed as a scalable desktop solution for small and medium-sized business workflows with a strong focus on usability, modular architecture, and reporting automation.

---

## Key Highlights

* Modern desktop UI built with JavaFX
* Modular layered architecture
* Integrated accounting workflow
* Inventory and stock lifecycle tracking
* Financial statement generation
* Barcode-based product operations
* PDF / Excel reporting engine
* SQLite persistence with Hibernate ORM

---

## Core Business Modules

### Inventory Management

* Product registration
* Product import
* Stock monitoring
* Stock transition history
* Supplier integration
* Barcode generation

### Sales Management

* Sales dashboard
* Payment processing
* Receipt generation
* Sales history
* Customer support flow

### Accounting System

* Chart of Accounts
* Journal Entry
* Ledger Account
* Ledger by Time Range
* Expense Tracking

### Financial Statements

* Balance Sheet
* Profit and Loss Statement
* Cash Flow Report
* Sales Report
* Expense Report

### Company Management

* Company registration
* Company selection
* Multi-company support foundation

### Employee Management

* Employee registration
* Role preparation for future access control

---

## Technical Architecture

### Backend

* Java 24 / Java 25
* Hibernate ORM
* SQLite Database
* Service Layer Architecture
* Repository Pattern

### Frontend

* JavaFX
* Custom Dashboard UI
* TableView
* Dynamic Form Components
* Search / Filter Support

### Reporting Engine

* Apache POI
* OpenPDF / PDFBox
* Export to Excel
* Export to PDF

### Build System

* Maven

---

## Project Structure

```text
src/
├── controller/
├── model/
├── service/
├── repository/
├── utils/
├── view/
├── reports/
└── assets/
```

---

## Installation

### Clone Repository

```bash
git clone your-repository-link
```

### Open Project

Import into IntelliJ IDEA

### Run Application

Execute:

```bash
Main.java
```

---

## Export System

Generated files are automatically stored under:

```text
data/
├── pdf/
├── excel/
```

### File Naming Convention

```text
yyyyMMdd_HHmmss_reportname.pdf
```

Example:

```text
20260330_143500_balance_sheet.pdf
```

---

## Screenshots

### Authentication

![Login](screenshots/login.png)

### Dashboard

![Dashboard](screenshots/dashboard.png)

### Main Dashboard

![Main Dashboard](screenshots/main_dash_board.png)

### Product Management

![Product Management](screenshots/product_management.png)

### Product Registration

![Product Register](screenshots/product_register.png)

### Product Import

![Product Import](screenshots/product_import.png)

### Stock Management

![Stock](screenshots/stock.png)

### Stock Transition

![Stock Transition](screenshots/stock_transition.png)

### Sales Dashboard

![Sales Dashboard](screenshots/sale_dashboard.png)

### Payment Processing

![Payment Process](screenshots/payment_process.png)

### Chart of Accounts

![Chart of Account](screenshots/chartofaccount.png)

### Journal Entry

![Journal Entry](screenshots/journal_entry.png)

### Ledger Account

![Ledger Account](screenshots/ledger_account.png)

### Ledger by Time

![Ledger Account Time](screenshots/ledger_account_by_time.png)

### Balance Sheet

![Balance Sheet](screenshots/balancesheet.png)

### Profit and Loss

![Profit and Loss](screenshots/profitandloss.png)

### Company Registration

![Company Register](screenshots/company_register.png)

### Company List

![Company List](screenshots/company_list.png)

---

## Planned Enhancements

* Multi-user authentication
* Role-based authorization
* Cloud synchronization
* Tax automation
* Invoice templates
* Multi-language support
* Advanced analytics dashboard

---

## Author

**Dhurba Bikram Khadka**

---

## License

This project is licensed under the MIT License.
