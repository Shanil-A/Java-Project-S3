# Old Book Buy & Sell Register

A Java Swing desktop application for managing second-hand book purchases, sales, inventory, and profit using SQLite, JDBC, and SQL with a simple, modern interface.

Developed as a B.Tech Computer Science Engineering Semester 3 Object-Oriented Programming project under the KTU 2024 scheme.

## Requirements

- **Java JDK 21** or higher ([Eclipse Temurin](https://adoptium.net/) recommended)
- No other software needed — SQLite is bundled via the JDBC driver in `lib/`

## Quick Start

Open a terminal in the project folder and run:

**Compile:**

```
javac -cp "lib/sqlite-jdbc-3.42.0.0.jar" -d out src/main/java/oldbookstore/*.java
```

**Run:**

```
java -cp "out;lib/sqlite-jdbc-3.42.0.0.jar" oldbookstore.Main
```

**Run Tests:**

```
java -cp "out;lib/sqlite-jdbc-3.42.0.0.jar" oldbookstore.TestAll
```

> **Note:** Windows uses `;` (semicolon) between classpath entries. On Linux/Mac, use `:` (colon) instead.

## Technologies Used

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Java Swing | GUI framework |
| JDBC | Database connectivity |
| SQLite | Local file-based database |
| SHA-256 | Password hashing |

## Project Structure

```
src/main/java/oldbookstore/
  Main.java               - Application entry point
  DatabaseConnection.java - SQLite connection, FK enforcement, and table setup
  Book.java               - Book model (encapsulation demo)
  Purchase.java           - Purchase model
  Sale.java               - Sale model
  User.java               - User model
  BookDAO.java            - Book database operations (CRUD)
  PurchaseDAO.java        - Purchase database operations
  SaleDAO.java            - Sale database operations
  UserDAO.java            - User authentication and account management
  PasswordUtil.java       - Password hashing and verification utility
  LoginFrame.java         - Login/authentication window
  MainFrame.java          - Main Swing GUI (all screens)
  TestAll.java            - Automated tests

lib/
  sqlite-jdbc-3.42.0.0.jar - SQLite JDBC driver (bundled)

database/
  oldbookstore.db          - Created automatically on first run
```

## Database Tables and Relationships

The application uses four SQLite tables:

| Table | Purpose |
|---|---|
| `books` | Stores book information and current stock |
| `purchases` | Records of books bought by the store |
| `sales` | Records of books sold by the store |
| `users` | Application user accounts |

### Foreign-Key Relationships

```
books.book_id  <──  purchases.book_id  (FOREIGN KEY, ON DELETE RESTRICT)
books.book_id  <──  sales.book_id      (FOREIGN KEY, ON DELETE RESTRICT)
```

- Every purchase record references a valid book via `book_id`.
- Every sale record references a valid book via `book_id`.
- A book cannot be deleted if it has purchase or sales history (`ON DELETE RESTRICT`).
- Foreign-key enforcement is enabled on every database connection using `PRAGMA foreign_keys = ON`.

## Class Descriptions

| Class | Description |
|---|---|
| `Main` | Starts the app. Initializes the database, then opens the login window. |
| `DatabaseConnection` | Provides `getConnection()` to connect Java to SQLite. Enables foreign-key enforcement. Creates all tables on startup. |
| `Book` | Model class representing a book. Private fields with getters/setters demonstrate encapsulation. |
| `Purchase` | Model class for a purchase transaction. |
| `Sale` | Model class for a sale transaction. |
| `User` | Model class for user accounts. |
| `BookDAO` | All SQL operations for books: add, getAll, search, update, delete. |
| `PurchaseDAO` | Records purchases and increases stock in a transaction. |
| `SaleDAO` | Records sales, checks stock, decreases stock in a transaction. |
| `UserDAO` | User registration, authentication, and password management. |
| `PasswordUtil` | Generates salt, hashes passwords with SHA-256, verifies credentials. |
| `LoginFrame` | Authentication window with username/password login. |
| `MainFrame` | Main GUI window with sidebar, dashboard, books, buy, and sell screens. |
| `TestAll` | Automated tests for CRUD, stock, profit, foreign keys, and user management. |

## CRUD Implementation

| Operation | Method | SQL |
|---|---|---|
| **Create** | `BookDAO.addBook()` | `INSERT INTO books` |
| **Read** | `BookDAO.getAllBooks()` | `SELECT * FROM books` |
| **Search** | `BookDAO.searchBooks()` | `WHERE title LIKE ?` |
| **Update** | `BookDAO.updateBook()` | `UPDATE books SET ...` |
| **Delete** | `BookDAO.deleteBook()` | `DELETE FROM books` (checks FK) |

## OOP Concepts Demonstrated

1. **Classes and Objects** — Book, Purchase, Sale, User, DAO classes
2. **Encapsulation** — Private fields + public getters/setters
3. **Constructors** — Multiple constructors in model classes
4. **Access Modifiers** — private fields, public methods
5. **Method Organization** — Separate DAO classes for database logic
6. **Exception Handling** — try-catch for SQL and number parsing
7. **Event Handling** — ActionListener for Swing button clicks

## Stock Logic

- Stock starts at 0 when a book is added
- Buying increases stock: `stock = stock + quantity`
- Selling decreases stock: `stock = stock - quantity`
- Cannot sell more than available stock
- Purchase and sale operations use database transactions to keep stock consistent

## Profit Calculation

```
profit = (sellingPrice - book's buyingPrice) × quantity
```

## Default Login

- **Username:** Admin
- **Password:** admin
