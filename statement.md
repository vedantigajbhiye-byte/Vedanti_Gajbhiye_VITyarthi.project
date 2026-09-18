# Project Statement: Personal Portfolio Tracker

## 1. Problem Statement

Individual retail investors today allocate capital across diverse asset classes, primarily Indian equities (NSE/BSE), international stocks, cryptocurrencies, and mutual funds. However, tracking performance across these disparate asset classes poses significant challenges:

1. **Fragmentation of Portfolios**: Investors must navigate multiple broker apps (e.g., Zerodha, Groww, Binance, CoinDCX, AMC portals) to monitor their holdings.
2. **Privacy and Data Security Concerns**: Many commercial portfolio management websites require full access to personal financial statements, broker credentials, or SMS feeds, exposing sensitive data to cloud breaches.
3. **Spreadsheet Inefficiencies**: Manual tracking using tools like Excel or Google Sheets requires complex custom formula maintenance, is error-prone, lacks relational integrity, and cannot maintain historical price logs effectively.
4. **Lack of Uniform Metrics**: Different asset classes quote prices in different units (e.g., share price, NAV for mutual funds, fractional satoshis for crypto), making aggregated real-time calculation of Net Worth, Total Capital Invested, Net Profit/Loss, and overall Return on Investment (ROI%) cumbersome.

**Personal Portfolio Tracker** solves this by providing a unified, privacy-first, local command-line application that consolidates multi-asset investments into a single persistent SQLite database with automated metrics, sorting, filtering, and report generation.

---

## 2. Scope of the Project

The scope of this project encompasses designing and implementing a standalone, modular Java application adhering to Object-Oriented Design (OOP) principles, relational database persistence, and robust input validation.

### In-Scope:
- **Multi-Asset Modeling**: Support for distinct investment classes (Equities/Stocks, Cryptocurrencies, and Mutual Funds) through polymorphic class inheritance (`Asset` base class).
- **Persistent Local Storage**: Relational schema implemented via SQLite (`portfolio.db`) using the JDBC API with full CRUD (Create, Read, Update, Delete) capability.
- **Dynamic Price & P&L Calculation**: Real-time evaluation of current asset value, absolute profit/loss, and percentage returns based on historical purchase prices versus updated market prices.
- **Price History Logging**: Tracking and storing historical price updates per asset in a dedicated relational table (`price_history`).
- **Data Filtering and In-Memory Analytics**:
  - Search assets by ticker symbol or company/fund name (case-insensitive).
  - Multi-criteria sorting using Java Collections and Comparators (sort by absolute P&L descending, sort by total capital invested).
  - Categorical grouping by asset class (Stocks vs. Crypto vs. Mutual Funds).
- **Report Generation (File I/O)**:
  - Export full portfolio status to CSV (Character-oriented streams) for external spreadsheet analysis.
  - Export formatted, human-readable ASCII summary reports to TXT (Byte-oriented streams).
- **Robust Exception Handling & Validation**: Custom checked exceptions and sanitizers preventing application crashes on invalid numerical, date, or empty inputs.

### Out-of-Scope (Future Work):
- Direct automated scraping or live REST API streaming of ticker prices (current version relies on verified manual price updates or offline inputs).
- Multi-user authentication and cloud database synchronization.
- Automated tax harvesting and dividend tracking calculations.

---

## 3. Target Users

The application is engineered for:
1. **Retail Investors & Traders**: Individuals holding a diversified basket of stocks, crypto tokens, and mutual fund units who desire a single, lightweight dashboard to assess net portfolio performance without login overheads.
2. **Privacy-Conscious Users**: Users who prefer offline, local-first applications where financial data never leaves their local workstation.
3. **Computer Science Students & Java Developers**: Students and educators evaluating real-world implementations of core Java curriculum concepts:
   - Object-Oriented Programming (Inheritance, Polymorphism, Encapsulation, Abstraction)
   - Java Collections Framework (`ArrayList`, `HashMap`, `Comparator`, Streams)
   - JDBC API & Relational Database Management (SQLite, `PreparedStatement`)
   - Java I/O Streams (`BufferedWriter`, `PrintWriter`, `FileOutputStream`)
   - Custom Exception Handling and Defensive Input Validation

---

## 4. High-Level Features

| Feature ID | Feature Name | Description |
|:---|:---|:---|
| **F01** | **Asset Registration (CRUD - Create)** | Add new Stocks (with exchange & sector), Cryptocurrencies (with blockchain & wallet), or Mutual Funds (with fund house, category & expense ratio). |
| **F02** | **Portfolio Dashboard (CRUD - Read)** | View all active investments in a formatted tabular terminal layout showing ID, symbol, name, quantity, purchase price, current price, invested amount, current valuation, and P&L. |
| **F03** | **Live P&L & ROI Calculation** | Real-time computation of net gains/losses and percentage returns with dynamic visual status indicators (▲ gain, ▼ loss). |
| **F04** | **Portfolio Aggregation Summary** | Consolidated view showing total capital deployed, total current valuation, net profit/loss, overall portfolio return %, and asset count breakdown. |
| **F05** | **Market Price Updates (CRUD - Update)** | Update the current market price or NAV for any asset with automatic logging to `price_history`. |
| **F06** | **Asset Liquidation / Removal (CRUD - Delete)** | Safely remove assets and their associated historical price records with explicit user confirmation prompts. |
| **F07** | **Search Engine** | Search across portfolio holdings by ticker symbol or asset name with partial-match support. |
| **F08** | **Performance Ranking & Sorting** | Rank assets from highest to lowest performer based on absolute P&L or sort by total capital allocation. |
| **F09** | **Asset Class Grouping** | Partition portfolio holdings into dedicated categories (Equities, Digital Assets, Mutual Funds) via hash-map aggregation. |
| **F10** | **Multi-Format Report Export** | Export portfolio snapshots to comma-separated values (`.csv`) and formatted text files (`.txt`) with automatic timestamping in the `reports/` directory. |
| **F11** | **Defensive Error Handling** | Comprehensive input validation preventing invalid formats, negative values, and non-existent record queries. |
