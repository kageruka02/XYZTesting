# XYZ Bank — Selenium WebDriver Test Automation Framework

Production-ready Selenium + TestNG + Allure framework targeting the
[XYZ Bank demo app](https://www.globalsqa.com/angularJs-protractor/BankingProject/#/login).

---

## Tech Stack

| Tool | Purpose |
|------|---------|
| Java 21 | Language |
| Selenium WebDriver 4.20 | Browser automation |
| WebDriverManager | Automatic driver binary management |
| TestNG 7.10 | Test framework, parallel execution |
| Allure 2.27 | Rich HTML reporting |
| Jackson | JSON test-data parsing |
| Apache POI | Excel test-data support |
| Maven | Build & dependency management |
| GitHub Actions | CI/CD |
| Docker | Containerised execution |

---

## Project Structure

```
XYZTesting/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── base/
│   │   │   │   └── BasePage.java          # Common WebDriver interactions
│   │   │   ├── models/
│   │   │   │   ├── CustomerData.java
│   │   │   │   ├── AccountData.java
│   │   │   │   └── TransactionData.java
│   │   │   ├── pages/
│   │   │   │   ├── HomePage.java
│   │   │   │   ├── manager/
│   │   │   │   │   ├── ManagerPage.java
│   │   │   │   │   ├── AddCustomerPage.java
│   │   │   │   │   ├── OpenAccountPage.java
│   │   │   │   │   └── CustomersPage.java
│   │   │   │   └── customer/
│   │   │   │       ├── CustomerLoginPage.java
│   │   │   │       ├── CustomerDashboard.java
│   │   │   │       ├── DepositPage.java
│   │   │   │       ├── WithdrawPage.java
│   │   │   │       └── TransactionsPage.java
│   │   │   └── utils/
│   │   │       ├── ConfigReader.java
│   │   │       ├── DriverFactory.java
│   │   │       ├── WaitUtils.java
│   │   │       ├── ScreenshotUtils.java
│   │   │       └── JsonDataReader.java
│   │   └── resources/
│   │       └── config.properties
│   └── test/
│       ├── java/
│       │   ├── base/
│       │   │   └── BaseTest.java
│       │   └── tests/
│       │       ├── manager/
│       │       │   ├── AddCustomerTest.java
│       │       │   ├── CreateAccountTest.java
│       │       │   └── DeleteCustomerTest.java
│       │       └── customer/
│       │           ├── CustomerLoginTest.java
│       │           ├── DepositTest.java
│       │           ├── WithdrawTest.java
│       │           └── TransactionTest.java
│       └── resources/
│           └── logback-test.xml
├── testdata/
│   ├── customers.json
│   └── transactions.json
├── .github/workflows/ci.yml
├── testng.xml
├── pom.xml
├── Dockerfile
└── docker-compose.yml
```

---

## Prerequisites

| Requirement | Version |
|-------------|---------|
| Java JDK | 21+ |
| Maven | 3.9+ |
| Chrome | Latest |
| Docker (optional) | 24+ |

---

## Quick Start

### 1. Clone the repository

```bash
git clone https://github.com/<your-org>/XYZTesting.git
cd XYZTesting
```

### 2. Install dependencies

```bash
mvn dependency:resolve
```

### 3. Run all tests (headed Chrome)

```bash
mvn clean test
```

### 4. Run in headless mode

```bash
mvn clean test -Dheadless=true
```

### 5. Run with a specific browser

```bash
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=edge
```

### 6. Run a single test class

```bash
mvn clean test -Dtest=AddCustomerTest
```

### 7. Run a single test method

```bash
mvn clean test -Dtest=AddCustomerTest#testAddCustomerFormIsVisible
```

---

## Allure Reports

```bash
# Run tests first
mvn clean test

# Generate Allure HTML report
mvn allure:report

# Serve at http://localhost:port
mvn allure:serve
```

---

## Configuration

Edit `src/main/resources/config.properties` to change defaults.
Any property can be overridden at runtime with `-D<property>=<value>`.

| Property | Default | Description |
|----------|---------|-------------|
| `base.url` | XYZ Bank URL | Application under test |
| `browser` | `chrome` | `chrome` / `firefox` / `edge` |
| `headless` | `false` | Headless mode |
| `explicit.wait` | `15` | Seconds for explicit waits |
| `page.load.timeout` | `30` | Seconds for page load |
| `screenshot.on.failure` | `true` | Capture on test failure |

---

## Parallel Execution

Configured in `testng.xml` (`parallel="classes"`, `thread-count="3"`).
`DriverFactory` uses `ThreadLocal<WebDriver>` — every thread owns an isolated browser.

---

## Docker

```bash
# Build and run
docker build -t xyz-testing .
docker run --rm -v "$(pwd)/target:/app/target" xyz-testing

# Or with Compose
docker-compose up --build
```

Reports and screenshots are mounted to `./target/` on the host.

---

## CI/CD — GitHub Actions

`.github/workflows/ci.yml` triggers on every push/PR:
1. Java 21 + Chrome setup
2. `mvn clean test -Dheadless=true`
3. Allure results uploaded as artifact
4. Allure HTML report generated and uploaded
5. Report deployed to GitHub Pages (main branch)

---

## Test Coverage

### Bank Manager
- Add customer — valid data (data-driven, 3 customers)
- Add customer — invalid data (numbers, special chars, empty fields)
- Open account — Dollar / Pound / Rupee for existing customers
- Delete customer — verify removal from list
- Search returns no results for deleted customer

### Customer
- Login with valid credentials (5 customers, data-driven)
- Login button disabled before selection; logout works
- Deposit valid amount — success message + balance increase
- Deposit zero / negative — no success message
- Withdraw valid partial and full balance — success
- Withdraw more than balance — failure message
- Balance decreases by exact withdrawn amount
- Transaction history accessible and shows Credit/Debit rows
- Transaction history table is read-only (no input fields)
- Back button returns to dashboard

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| `ChromeDriver` not found | WebDriverManager auto-downloads; ensure internet access |
| `TimeoutException` | Increase `explicit.wait` in `config.properties` |
| Allure report empty | Run `mvn clean test` before `mvn allure:report` |
| Docker JDK download fails | Update the Temurin URL in `Dockerfile` to the latest release |