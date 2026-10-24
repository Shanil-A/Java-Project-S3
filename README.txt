===========================================================
  OLD BOOK BUY & SELL REGISTER
  B.Tech Semester 3 - OOP Project
===========================================================

DESCRIPTION
-----------
A desktop application for managing a second-hand bookstore.
Features include maintaining a book catalog, recording
purchases and sales, tracking stock, and calculating profit.
Includes user authentication with password hashing.

TECHNOLOGY STACK
----------------
- Java 21 (Temurin/OpenJDK)
- Java Swing (GUI)
- JDBC (Database connectivity)
- SQLite (Local file database)
- SHA-256 (Password hashing)

REQUIRED SOFTWARE
-----------------
- Java JDK 21 or higher
- No other software needed (SQLite is bundled via JDBC driver)

HOW TO VERIFY YOUR JAVA VERSION
---------------------------------
  java -version
  javac -version

Both commands should report version 21 or higher.

PROJECT STRUCTURE
-----------------
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
  MainFrame.java          - Swing GUI (all screens)
  TestAll.java            - Automated tests

lib/
  sqlite-jdbc-3.42.0.0.jar - SQLite JDBC driver

database/
  oldbookstore.db          - Created automatically on first run

HOW TO COMPILE AND RUN
----------------------
Open a terminal in the project folder and run:

  1. Compile:
     javac -cp "lib/sqlite-jdbc-3.42.0.0.jar" -d out src/main/java/oldbookstore/*.java

  2. Run:
     java -cp "out;lib/sqlite-jdbc-3.42.0.0.jar" oldbookstore.Main

  (On Linux/Mac, use : instead of ; in the classpath)

  3. Run Tests:
     java -cp "out;lib/sqlite-jdbc-3.42.0.0.jar" oldbookstore.TestAll

HOW SQLITE IS USED
------------------
- SQLite stores all data in a single file: database/oldbookstore.db
- The file is created automatically on first run
- Tables are created using CREATE TABLE IF NOT EXISTS
- No separate database server is needed
- Connection is established using JDBC DriverManager
- Foreign-key enforcement is enabled on every connection
  using PRAGMA foreign_keys = ON

DATABASE TABLES
---------------
1. books      - Stores book information and current stock
2. purchases  - Records of books bought by the store
3. sales      - Records of books sold by the store
4. users      - Application user accounts

FOREIGN-KEY RELATIONSHIPS
--------------------------
  books.book_id  <--  purchases.book_id  (ON DELETE RESTRICT)
  books.book_id  <--  sales.book_id      (ON DELETE RESTRICT)

- Every purchase record references a valid book.
- Every sale record references a valid book.
- A book cannot be deleted if it has purchase or sales history.
- Inserting a purchase or sale with a nonexistent book_id is rejected.
- PRAGMA foreign_keys = ON is executed on every database connection.

CLASS DESCRIPTIONS
------------------
Main.java:
  Starts the app. Initializes the database, then opens the login window.

DatabaseConnection.java:
  Provides getConnection() to connect Java to SQLite.
  Enables foreign-key enforcement on every connection.
  Creates all four tables on startup.

Book.java:
  Model class representing a book. Has private fields with
  getters and setters. Demonstrates encapsulation.

Purchase.java:
  Model class for a purchase transaction.

Sale.java:
  Model class for a sale transaction.

User.java:
  Model class for user accounts.

BookDAO.java:
  Contains all SQL operations for books:
  - addBook()        -> INSERT (Create)
  - getAllBooks()     -> SELECT (Read)
  - searchBooks()    -> SELECT with LIKE (Search)
  - updateBook()     -> UPDATE (Update)
  - deleteBook()     -> DELETE (Delete, respects FK constraints)

PurchaseDAO.java:
  - addPurchase()    -> Inserts purchase + increases stock (transaction)
  - getPurchaseHistory() -> Returns all purchases

SaleDAO.java:
  - addSale()        -> Inserts sale + decreases stock (transaction)
  - getSaleHistory() -> Returns all sales
  - getBookStock()   -> Checks current stock before selling

UserDAO.java:
  - registerUser()   -> Creates a new user with hashed password
  - authenticate()   -> Verifies username and password
  - updatePassword() -> Changes a user's password

PasswordUtil.java:
  - generateSalt()   -> Creates a random salt
  - hashPassword()   -> SHA-256 hash with salt
  - verifyPassword() -> Constant-time password comparison

LoginFrame.java:
  Authentication window with username and password login.

MainFrame.java:
  The main GUI window with:
  - Sidebar navigation (Dashboard, Books, Buy, Sell, Change Password, Logout, Exit)
  - Dashboard with summary cards
  - Books screen with JTable and CRUD buttons
  - Buy Books screen with purchase form and history
  - Sell Books screen with sale form and history
  - Change Password dialog

CRUD IMPLEMENTATION
-------------------
CREATE: BookDAO.addBook() uses INSERT INTO books
READ:   BookDAO.getAllBooks() uses SELECT * FROM books
        BookDAO.searchBooks() uses WHERE title LIKE ?
UPDATE: BookDAO.updateBook() uses UPDATE books SET ...
DELETE: BookDAO.deleteBook() uses DELETE FROM books
        (FK constraints prevent deletion if transactions exist)

OOP CONCEPTS DEMONSTRATED
--------------------------
1. Classes and Objects   - Book, Purchase, Sale, User, DAO classes
2. Encapsulation         - Private fields + public getters/setters
3. Constructors          - Multiple constructors in model classes
4. Access Modifiers      - private fields, public methods
5. Method Organization   - Separate DAO classes for database logic
6. Exception Handling    - try-catch for SQL and number parsing
7. Event Handling        - ActionListener for Swing button clicks

STOCK LOGIC
-----------
- Stock starts at 0 when a book is added
- Buying increases stock: stock = stock + quantity
- Selling decreases stock: stock = stock - quantity
- Cannot sell more than available stock
- Purchase and sale operations use database transactions

PROFIT CALCULATION
------------------
profit = (sellingPrice - book's buyingPrice) x quantity

DEFAULT LOGIN
-------------
Username: Admin
Password: admin

===========================================================
