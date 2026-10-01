===========================================================
  OLD BOOK BUY & SELL REGISTER
  B.Tech Semester 3 - OOP Project
===========================================================

DESCRIPTION
-----------
A desktop application for managing a second-hand bookstore.
Features include maintaining a book catalog, recording
purchases and sales, tracking stock, and calculating profit.

TECHNOLOGY STACK
----------------
- Java 21 (Temurin/OpenJDK)
- Java Swing (GUI)
- JDBC (Database connectivity)
- SQLite (Local file database)

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
  DatabaseConnection.java - SQLite connection and table setup
  Book.java               - Book model (encapsulation demo)
  Purchase.java           - Purchase model
  Sale.java               - Sale model
  BookDAO.java            - Book database operations (CRUD)
  PurchaseDAO.java        - Purchase database operations
  SaleDAO.java            - Sale database operations
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
     javac -cp lib/sqlite-jdbc-3.42.0.0.jar -d out src/main/java/oldbookstore/*.java

  2. Run:
     java -cp out;lib/sqlite-jdbc-3.42.0.0.jar oldbookstore.Main

  (On Linux/Mac, use : instead of ; in the classpath)

  3. Run Tests:
     java -cp out;lib/sqlite-jdbc-3.42.0.0.jar oldbookstore.TestAll

BUILDING WITH MAVEN (optional)
------------------------------
If you have Apache Maven installed:

  1. Compile:       mvn compile
  2. Run tests:     mvn test
  3. Package JAR:   mvn package
  4. Run:           java -cp target/old-book-store-1.0.jar;lib/sqlite-jdbc-3.42.0.0.jar oldbookstore.Main

The pom.xml is configured for Java 21.

HOW SQLITE IS USED
------------------
- SQLite stores all data in a single file: database/oldbookstore.db
- The file is created automatically on first run
- Tables are created using CREATE TABLE IF NOT EXISTS
- No separate database server is needed
- Connection is established using JDBC DriverManager

DATABASE TABLES
---------------
1. books      - Stores book information and current stock
2. purchases  - Records of books bought by the store
3. sales      - Records of books sold by the store

CLASS DESCRIPTIONS
------------------
Main.java:
  Starts the app. Initializes the database, then opens the GUI.

DatabaseConnection.java:
  Provides getConnection() to connect Java to SQLite.
  Creates all three tables on startup.

Book.java:
  Model class representing a book. Has private fields with
  getters and setters. Demonstrates encapsulation.

Purchase.java:
  Model class for a purchase transaction.

Sale.java:
  Model class for a sale transaction.

BookDAO.java:
  Contains all SQL operations for books:
  - addBook()        -> INSERT (Create)
  - getAllBooks()     -> SELECT (Read)
  - searchBooks()    -> SELECT with LIKE (Search)
  - updateBook()     -> UPDATE (Update)
  - deleteBook()     -> DELETE (Delete)

PurchaseDAO.java:
  - addPurchase()    -> Inserts purchase + increases stock
  - getPurchaseHistory() -> Returns all purchases

SaleDAO.java:
  - addSale()        -> Inserts sale + decreases stock
  - getSaleHistory() -> Returns all sales
  - getBookStock()   -> Checks current stock before selling

MainFrame.java:
  The main GUI window with:
  - Sidebar navigation (Dashboard, Books, Buy, Sell, Exit)
  - Dashboard with summary cards
  - Books screen with JTable and CRUD buttons
  - Buy Books screen with purchase form and history
  - Sell Books screen with sale form and history

CRUD IMPLEMENTATION
-------------------
CREATE: BookDAO.addBook() uses INSERT INTO books
READ:   BookDAO.getAllBooks() uses SELECT * FROM books
        BookDAO.searchBooks() uses WHERE title LIKE ?
UPDATE: BookDAO.updateBook() uses UPDATE books SET ...
DELETE: BookDAO.deleteBook() uses DELETE FROM books
        (Checks transaction history before allowing delete)

OOP CONCEPTS DEMONSTRATED
--------------------------
1. Classes and Objects   - Book, Purchase, Sale, DAO classes
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

PROFIT CALCULATION
------------------
profit = (sellingPrice - book's buyingPrice) × quantity

===========================================================
