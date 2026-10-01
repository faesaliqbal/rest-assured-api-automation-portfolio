# REST API Automation

A focused Java 17 API automation portfolio by **Faisal Iqbal**, testing the
[JSONPlaceholder](https://jsonplaceholder.typicode.com/) user resource with
REST Assured and TestNG.

The project demonstrates maintainable API clients, shared specifications, Java
payload serialization, data-driven assertions, nested JSON Schema validation,
configurable network timeouts, and failure-focused reporting. Its scope is
deliberately small: independent tests for one public API resource.

## API under test

Default base URL: https://jsonplaceholder.typicode.com

JSONPlaceholder is a public fake REST API. **POST, PUT, and DELETE simulate writes
without persisting changes.** These tests validate returned HTTP responses; they
do not verify database state or chain create/read/update/delete operations.
See the [official guide](https://jsonplaceholder.typicode.com/guide/).

## Technology stack

Versions are configured in [pom.xml](pom.xml).

| Component | Version |
| --- | --- |
| Java compilation release | 17 |
| REST Assured | 5.5.6 |
| REST Assured JSON Schema Validator | 5.5.6 |
| TestNG | 7.11.0 |
| Jackson Databind | 2.20.0 |
| ExtentReports | 5.1.2 |
| Maven Surefire Plugin | 3.5.4 |
| Maven Compiler Plugin | 3.13.0 |
| Maven Resources Plugin | 3.3.1 |
| Maven Clean Plugin | 3.2.0 |

Maven itself is not pinned or bundled. Local validation used Maven 3.9.16 and
Eclipse Temurin Java 17.0.20.1.

## Architecture

All automation code is under the Maven test source set.

```text
.
├── .github/workflows/api-tests.yml
├── .gitattributes
├── .gitignore
├── LICENSE
├── README.md
├── pom.xml
├── scripts/prepare_ci_reports.py
├── testng.xml
└── src/test/
    ├── java/com/faisal/api/
    │   ├── clients/UserClient.java
    │   ├── models/User.java
    │   ├── specs/
    │   │   ├── RequestSpec.java
    │   │   └── ResponseSpec.java
    │   ├── tests/
    │   │   ├── GetUserTest.java
    │   │   ├── CreateUserTest.java
    │   │   ├── UpdateUserTest.java
    │   │   └── DeleteUserTest.java
    │   └── utils/
    │       ├── ConfigReader.java
    │       ├── TestDataProvider.java
    │       ├── ExtentManager.java
    │       ├── ExtentTestManager.java
    │       └── TestListener.java
    └── resources/
        ├── config.properties
        └── schemas/user-schema.json
```

- **Client:** [UserClient](src/test/java/com/faisal/api/clients/UserClient.java)
  owns endpoint calls and returns responses for test assertions.
- **Specifications:** request configuration supplies the base URI, JSON headers,
  connection/socket timeouts, and buffered request/response logging. Automatic
  connection retries are disabled. The response specification checks HTTP status
  and JSON content type.
- **Model:** `User` supplies name, username, and email for Jackson serialization
  of POST/PUT payloads. It does not model the complete GET response.
- **Configuration:** `ConfigReader` resolves overrides and validates the base URL
  and positive timeout values.
- **Tests/data:** TestNG classes own assertions; `TestDataProvider` contains
  expected public fixtures for users 1–3.
- **Schema:** [user-schema.json](src/test/resources/schemas/user-schema.json)
  checks required fields, positive IDs, nested address/geographic structure,
  and company fields. It is used for single-user GETs and every listed user.
- **Reporting:** the TestNG listener creates Extent entries, records outcomes,
  and emits buffered request/response details only when a test fails.

## Implemented coverage

Seven test methods produce **nine executions**: the data-driven GET method runs
three times. The suite is defined in [testng.xml](testng.xml).

| Scenario | Executions | Assertions |
| --- | ---: | --- |
| GET user 1 | 1 | 200, JSON content type, user schema, expected ID/name/username/email |
| GET users 1–3 (DataProvider) | 3 | 200, JSON content type, user schema, expected fixture fields |
| GET all users | 1 | 200, JSON content type, ten records, unique positive IDs, known IDs present, schema for every record |
| GET nonexistent user 9999 | 1 | 404, JSON content type, empty JSON object |
| POST user | 1 | 201, JSON content type, positive integer ID, echoed name/username/email |
| PUT user 1 | 1 | 200, JSON content type, integer ID 1, echoed updated fields |
| DELETE user 1 | 1 | 200, JSON content type, empty JSON object |

## Prerequisites and execution

- JDK **17** with Java available on `PATH`.
- Apache Maven **3.9.x** available on `PATH`.
- Internet access to Maven dependencies and JSONPlaceholder.
- Python **3.10+** only for the optional CI artifact preparation script locally;
  it is not needed for Maven API tests.

Download or clone this repository, then open a terminal in the directory
containing `pom.xml`. No API credentials or separate service installation are
needed.

```sh
java -version
mvn -version
mvn clean test
```

Maven resolves dependencies, compiles automation code, and runs the TestNG suite.
Use `mvn test` for a subsequent run without cleaning build outputs.

## Configuration

Precedence: **JVM system property → environment variable → classpath default**.
Defaults are in [config.properties](src/test/resources/config.properties).

| JVM property | Environment variable | Default |
| --- | --- | --- |
| `base.url` | `BASE_URL` | `https://jsonplaceholder.typicode.com` |
| `connection.timeout.ms` | `CONNECTION_TIMEOUT_MS` | `10000` |
| `socket.timeout.ms` | `SOCKET_TIMEOUT_MS` | `20000` |

Timeouts are positive integers in milliseconds. The socket timeout limits waits
for response data; it is not a total end-to-end request deadline. Blank values,
nonpositive timeouts, and invalid base URLs fail configuration validation.
Base URLs must use HTTP(S) and cannot contain credentials, a query, or a fragment.

Command-line overrides (quoted arguments also work in PowerShell):

```sh
mvn clean test "-Dbase.url=https://jsonplaceholder.typicode.com" "-Dconnection.timeout.ms=15000" "-Dsocket.timeout.ms=30000"
```

PowerShell environment overrides:

```powershell
$env:BASE_URL = "https://jsonplaceholder.typicode.com"
$env:CONNECTION_TIMEOUT_MS = "15000"
$env:SOCKET_TIMEOUT_MS = "30000"
mvn clean test

# Remove session overrides when finished.
Remove-Item Env:BASE_URL, Env:CONNECTION_TIMEOUT_MS, Env:SOCKET_TIMEOUT_MS
```

Bash environment overrides:

```sh
BASE_URL=https://jsonplaceholder.typicode.com CONNECTION_TIMEOUT_MS=15000 SOCKET_TIMEOUT_MS=30000 mvn clean test
```

Changing the base URL does not adapt the test contract: a replacement service
must expose the same routes, responses, and user fixtures.

## Reports and diagnostics

Generated locally after a test run:

| Output | Location |
| --- | --- |
| Extent HTML report | `test-output/ExtentReport.html` |
| Surefire summary | `target/surefire-reports/TestSuite.txt` |
| Surefire XML | `target/surefire-reports/TEST-TestSuite.xml` |
| TestNG HTML report | `target/surefire-reports/index.html` |

Open the HTML files in a browser. Extent records method, endpoint, status,
response time, and test outcome. Failures add buffered request/response details
to the console and Extent report; successful tests do not dump bodies.

Generated reports are ignored by Git. `mvn clean` removes `target/`, but the
Extent file lives in `test-output/` and is overwritten when the listener flushes
a new run. If execution stops before report generation, an older Extent file may
remain; check its timestamp.

## Local validation

Latest verified local API suite: **9 passed, 0 failed, 0 errors, 0 skipped**,
on **1 October 2026**. This is local execution evidence, **not a GitHub Actions
result**. The current [workflow](.github/workflows/api-tests.yml) has not executed
on GitHub.

## GitHub Actions

The workflow is configured for pushes to `main`, pull requests targeting `main`,
and manual dispatch. It uses Temurin Java 17, Maven dependency caching, and:

```sh
mvn --batch-mode --no-transfer-progress clean test
```

After execution, including test failure, it prepares and uploads `api-test-results`
with a seven-day retention period. The artifact contains `summary.json` and
`junit-results.xml` with counts, test names, durations, and outcomes. The
[export script](scripts/prepare_ci_reports.py) excludes system properties,
hostnames, raw request/response bodies, exception messages, and stack traces.
Full diagnostics stay in the CI job log; raw Surefire, TestNG, and Extent reports
are not uploaded. If setup or compilation fails before Surefire produces XML,
no result artifact is available.

To prepare the same compact results locally after Maven execution:

```sh
python scripts/prepare_ci_reports.py
```

Output is written to ignored `target/ci-reports/`. The script uses only the Python
standard library.

## Known limitations

- Availability, latency, and fixture stability depend on an external public API.
  Failures remain failures; there are no retry or rerun mechanisms.
- Write tests check simulated responses and cannot establish persisted CRUD.
- Assertions reflect JSONPlaceholder fixtures, including ten users.
- There is no authentication/authorization, PATCH, or parallel-execution coverage.
- Response times are diagnostic measurements, not asserted performance targets.
- The GET schema checks selected structure and types; it is not a complete formal
  API contract. POST/PUT validate their smaller returned payloads separately.
- TestNG's transitive SLF4J API emits a missing-provider warning. No logging
  provider was added; REST Assured diagnostics and Extent reporting work.
- The suite is not a security test or dependency vulnerability assessment.

## License

[MIT](LICENSE) — Copyright (c) 2026 Faisal Iqbal.
