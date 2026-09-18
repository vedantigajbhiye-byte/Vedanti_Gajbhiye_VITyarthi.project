# 💼 Personal Portfolio Tracker

> A modular, command-line Java application to track Stocks, Cryptocurrencies, and Mutual Funds — featuring persistent SQLite storage, file I/O export reports, multi-criteria sorting, and an extensive JUnit 5 test suite adhering to full Object-Oriented Programming (OOP) design.

---

## 📋 Course & Project Information

| Field | Details |
|:---|:---|
| **Course** | CSE2006 – Programming in Java |
| **Course Type** | LP (Lab + Project) |
| **Credits** | 3 |
| **Framework & Evaluation** | VITyarthi - Build Your Own Project Guidelines |
| **Project Statement** | See [statement.md](statement.md) for Problem Statement, Scope, Target Users & Feature Matrix |
| **Formal Project Report** | Located at [docs/Portfolio_Tracker_Report_priyam_prakash.pdf](docs/Portfolio_Tracker_Report_priyam_prakash.pdf) |

---

## 🏛️ Project Architecture & Layout

```
PortfolioTracker/
├── pom.xml                                  ← Maven configuration & dependency manager
├── statement.md                             ← VITyarthi project statement & user scope
├── README.md                                ← Project setup, documentation & test guide
├── run.bat                                  ← Windows automated build, run & test script
├── run.sh                                   ← Linux/macOS automated build, run & test script
├── docs/
│   └── Portfolio_Tracker_Report_priyam_prakash.pdf  ← Comprehensive academic project report
├── lib/                                     ← JAR dependencies (auto-downloaded)
│   ├── sqlite-jdbc.jar
│   ├── slf4j-api.jar
│   ├── slf4j-simple.jar
│   └── junit-platform-console-standalone.jar
├── reports/                                 ← Exported CSV and TXT reports
├── src/
│   ├── main/java/com/portfolio/
│   │   ├── Main.java                        ← Entry point & ANSI CLI menus
│   │   ├── model/
│   │   │   ├── Asset.java                   ← Abstract base class (OOP Abstraction)
│   │   │   ├── Stock.java                   ← Concrete subclass (Equities)
│   │   │   ├── Crypto.java                  ← Concrete subclass (Digital assets)
│   │   │   ├── MutualFund.java              ← Concrete subclass (Mutual funds & NAV)
│   │   │   └── PortfolioSummary.java        ← Aggregated portfolio metrics
│   │   ├── dao/
│   │   │   └── AssetDAO.java                ← JDBC CRUD operations on SQLite
│   │   ├── service/
│   │   │   └── PortfolioService.java        ← Business logic, caching & collection analytics
│   │   ├── util/
│   │   │   ├── DatabaseManager.java         ← Singleton SQLite connection manager
│   │   │   ├── InputValidator.java          ← Defensive input validation utility
│   │   │   └── ReportWriter.java            ← Character & Byte stream file exporters
│   │   └── exception/
│   │       ├── AssetNotFoundException.java  ← Custom checked exception
│   │       └── InvalidInputException.java   ← Custom input validation exception
│   └── test/java/com/portfolio/
│       ├── model/
│       │   ├── AssetModelTest.java          ← Unit tests for models & P&L math
│       │   └── PortfolioSummaryTest.java    ← Unit tests for multi-asset aggregation
│       ├── service/
│       │   └── PortfolioServiceTest.java    ← Integration tests for service & DAO
│       └── util/
│           └── InputValidatorTest.java      ← Parameterized tests for sanitizers
└── out/                                     ← Compiled bytecode binaries
```

---

## ✨ Features

- **Add Assets (CRUD - Create)**: Multi-asset support (Stocks, Cryptocurrencies, and Mutual Funds) with type-specific attributes.
- **View Portfolio (CRUD - Read)**: Formatted table with live P&L, percentage returns, and directional status indicators.
- **Portfolio Summary**: Aggregate total invested, current valuation, net profit/loss, and portfolio-wide ROI.
- **Update Prices (CRUD - Update)**: Update current market prices or NAVs with automatic logging to `price_history`.
- **Delete Assets (CRUD - Delete)**: Remove holdings and purge historical records with safety confirmation prompts.
- **Search Engine**: Case-insensitive partial matching across ticker symbols and asset names.
- **Performance Ranking**: Sort assets descending by absolute P&L or total capital invested using Java `Comparator`.
- **Asset Categorization**: Group assets by asset class via HashMap bucketing.
- **Report Generation**: Export full portfolio snapshots to CSV (character streams) and formatted text files (byte streams).
- **Persistent Storage**: Zero-configuration embedded SQLite database (`portfolio.db`) created automatically.

---

## 🔒 Non-Functional Requirements (NFRs)

In adherence to Section 2.2 of the VITyarthi guidelines, the system implements:

1. **Performance & Low Latency**:
   - In-memory cache-aside architecture using `ArrayList` and `HashMap` provides $O(1)$ price lookups and instantaneous portfolio summary aggregations without redundant database queries.
2. **Security & Data Privacy (Local-First)**:
   - Runs 100% locally with zero external network tracking or cloud data harvesting.
   - All SQL interactions are strictly parameterized via `PreparedStatement` to eliminate SQL Injection vectors.
3. **Usability & Accessibility**:
   - Clean, color-coded ANSI terminal interface with formatted tabular output, explicit confirmation dialogues on destructive actions, and clear error diagnostics.
4. **Reliability & Data Integrity**:
   - SQLite relational schema enforces Foreign Key constraints linking `price_history` to `assets`.
   - All database statements and file streams use Java `try-with-resources` blocks to guarantee automatic closure and prevent connection or file descriptor leaks.
5. **Maintainability & Modularity**:
   - Strict separation of concerns following the Service-DAO-Model architectural pattern with decoupled utility classes and custom exceptions.

---

## 🛠️ Setup & Execution

### Prerequisites
- **Java JDK 17 or higher** ([Download from Adoptium](https://adoptium.net))
- *(Optional)* **Apache Maven 3.8+** if building via Maven.

---

### Method A: Automated Scripts (Zero Setup Required)

The provided build scripts handle dependency downloads, compilation, and execution automatically without needing Maven or Gradle installed.

#### Windows
```cmd
# 1. Run the interactive application
run.bat

# 2. Run the automated JUnit 5 test suite (37 tests)
run.bat test

# 3. Clean compiled binaries
run.bat clean
```

#### Linux / macOS
```bash
chmod +x run.sh

# 1. Run the interactive application
./run.sh

# 2. Run the automated JUnit 5 test suite
./run.sh test

# 3. Clean compiled binaries
./run.sh clean
```

---

### Method B: Standard Apache Maven (Optional)

If you have Maven installed on your workstation:
```bash
# Compile and run all 37 unit tests
mvn test

# Launch the CLI application
mvn exec:java
```

---

## 🧪 Automated Testing Instructions

The project contains a comprehensive automated test suite in `src/test/java/` covering domain models, service business logic, and input validation.

### Test Execution Command:
```cmd
run.bat test
```
*or via Maven:*
```bash
mvn test
```

### Test Suite Summary (37 Tests Total):
- **`InputValidatorTest`**: Parameterized and edge-case tests validating numeric parsing, bounds checking, date format compliance (ISO-8601), and non-empty string constraints.
- **`AssetModelTest`**: Validates inheritance, polymorphism, buy price vs. market price calculations, and percentage yields for `Stock`, `Crypto`, and `MutualFund`.
- **`PortfolioSummaryTest`**: Tests multi-asset aggregation, count bucketing, and portfolio-wide net worth calculations.
- **`PortfolioServiceTest`**: Integration tests asserting database CRUD, cache synchronization, Comparator-based sorting, search filtering, and price updates.

---

## 🖥️ Application Previews & Sample Run

### Main Menu Interface
```
╔══════════════════════════════════════════════════════════╗
║        💼  PERSONAL PORTFOLIO TRACKER  v1.0              ║
║        Track Stocks • Crypto • Mutual Funds              ║
╚══════════════════════════════════════════════════════════╝

═══════════════ MAIN MENU ═══════════════
  1. ➕  Add New Asset
  2. 📋  View All Assets
  3. 📊  Portfolio Summary
  4. 💲  Update Current Price
  5. 🗑️   Delete Asset
  6. 🔍  Search Assets
  7. 📈  View Sorted by P&L
  8. 🗂️   View by Asset Type
  9. 💾  Export Reports
  0. 🚪  Exit
═════════════════════════════════════════
Enter your choice: 
```

### Tabular Portfolio View
```
+-----+------------+---------------------------+-------------+----------+------------+------------+---------------+---------------+---------------+
| ID  | Symbol     | Name                      | Type        | Quantity |  Buy Price | Cur. Price | Invested (₹)  | Cur Val (₹)   | P&L (₹)       |
+-----+------------+---------------------------+-------------+----------+------------+------------+---------------+---------------+---------------+
|   1 | TCS        | Tata Consultancy Services | STOCK       |  10.0000 |    3500.00 |    3950.00 |     35,000.00 |     39,500.00 | +4,500.00  ▲ |
|   2 | BTC        | Bitcoin                   | CRYPTO      |   0.0500 | 3200000.00 | 3850000.00 |    160,000.00 |    192,500.00 | +32,500.00 ▲ |
|   3 | HDFC-TOP100| HDFC Top 100 Fund         | MUTUAL_FUND | 100.0000 |     850.00 |     820.00 |     85,000.00 |     82,000.00 | -3,000.00  ▼ |
+-----+------------+---------------------------+-------------+----------+------------+------------+---------------+---------------+---------------+
```

### Consolidated Portfolio Summary
```
📊 PORTFOLIO SUMMARY
──────────────────────────────────────────────────
  Total Invested              : ₹ 280,000.00
  Current Value               : ₹ 314,000.00
  Total P&L                   : +₹ 34,000.00 (+12.14%)
──────────────────────────────────────────────────
  Total Assets                : 3
    Stocks                    : 1
    Crypto                    : 1
    Mutual Funds              : 1
──────────────────────────────────────────────────
```

---

## 🎓 Java Concepts & Curriculum Alignment (Units 1–5)

- **Unit 1 & 2: Object-Oriented Programming**:
  - `Asset` (Abstract base class), `Stock`, `Crypto`, `MutualFund` (Inheritance & Polymorphism).
  - Encapsulation via private fields with accessors/mutators.
  - Method overriding (`calculateCurrentValue()`, `getSummary()`).
- **Unit 3: Robust Exception Handling**:
  - Custom checked exceptions: `AssetNotFoundException` and `InvalidInputException`.
  - Defensive parsing and resource safety with `try-catch-finally` and `try-with-resources`.
- **Unit 4: Java Collections & I/O Streams**:
  - `ArrayList`, `HashMap`, and `Comparator` for sorting and grouping.
  - Character-oriented streams (`BufferedWriter`, `FileWriter`) for CSV export.
  - Byte-oriented streams (`FileOutputStream`, `PrintWriter`) for structured text export.
- **Unit 5: JDBC API & Database Persistence**:
  - `DriverManager`, `Connection`, `PreparedStatement`, `Statement`, `ResultSet`.
  - Embedded SQLite database management and transaction consistency.

---

## 📚 References

1. Herbert Schildt, *Java: The Complete Reference*, 11th Edition, Oracle Press, 2018.
2. Paul Deitel & Harvey Deitel, *Java How to Program (Early Objects)*, 10th Edition, Pearson, 2015.
3. SQLite JDBC Driver Documentation – https://github.com/xerial/sqlite-jdbc
4. JUnit 5 User Guide – https://junit.org/junit5/docs/current/user-guide/
