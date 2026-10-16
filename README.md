# Employee Management System (EMS)

An Object-Oriented Employee Management System built for an OOSE (Object Oriented
Software Engineering) academic project.

**Stack:** Java 17 + Spring Boot 3 (backend & REST API) · MongoDB (database) ·
HTML/CSS/JavaScript (frontend, served by Spring Boot itself — no separate server needed).

---

## 1. Object-Oriented Design

| Class | Responsibility |
|---|---|
| `Employee` | Personal info, job role, department link, basic salary, embedded `LeaveBalance` |
| `Department` | Department name/description, optional manager reference |
| `Payroll` | Month/year payslip: basic + allowances - deductions = net salary, with `calculateNetSalary()` |
| `Leave` | Leave application: type, dates, computed number of days, status |
| `LeaveBalance` | Casual / Sick / Earned leave counters with a `deduct()` method used on approval |
| `User` | Login account (username/password/role), linked to an `Employee` for employee logins |

Each entity has its own `Repository` (Spring Data MongoDB), `Service` (business logic —
salary calculation, leave balance deduction, username generation) and `Controller`
(REST endpoints). This is a classic 3-layer OOP architecture: **Model → Service → Controller**.

Key OOP concepts used:
- **Encapsulation** – all fields are private with getters/setters.
- **Abstraction** – controllers never touch the database directly, only services.
- **Single Responsibility** – one service per entity.
- **Composition** – `Employee` *has-a* `LeaveBalance`.

---

## 2. Features

- Employee registration (auto-creates a login account, password `123`)
- Employee edit/delete, department-wise listing
- Department CRUD
- Payroll generation with automatic net salary calculation (one payslip per employee per month)
- Leave application, approval/rejection with automatic leave-balance deduction
- Two dashboards: **Admin** (full control) and **Employee** (self-service)
- Login system — every user's password is `123` (see credentials below)

---

## 3. Prerequisites

Install these on your machine before running:

1. **Java 17 or later** — `java -version`
2. **Maven 3.8+** — `mvn -version` (or use the included `mvnw` wrapper if you add one)
3. **MongoDB** (Community Edition), running locally on the default port `27017`

### Installing MongoDB

- **Windows / Mac:** download from https://www.mongodb.com/try/download/community and
  follow the installer (on Mac you can also `brew install mongodb-community`).
- **Linux (Ubuntu/Debian):**
  ```bash
  sudo apt-get install -y mongodb
  sudo systemctl start mongodb
  ```
  (or follow MongoDB's official apt-repo instructions for the latest version)

Verify it's running:
```bash
mongosh   # or "mongo" on older installs — should connect without error
```

No manual database/collection setup is needed — Spring Data MongoDB creates the
`employee_management_system` database and its collections automatically on first save.

---

## 4. Running the project

1. Unzip this project and open a terminal inside the `ems` folder.
2. Make sure MongoDB is running (see above).
3. Run:
   ```bash
   mvn spring-boot:run
   ```
   or build a jar and run it:
   ```bash
   mvn clean package -DskipTests
   java -jar target/ems.jar
   ```
4. Open your browser at **http://localhost:8080**

The app seeds itself on first run with:

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `123` |
| Employee (sample) | `arunkumar` | `123` |

Every **new employee** you register from the Admin panel automatically gets a login
account with password `123` and a username generated from their name (lowercased,
spaces removed — e.g. "Prema Sri" → `premasri`; a number is appended if that username
is already taken).

To change the MongoDB connection (e.g. MongoDB Atlas instead of local), edit:
`src/main/resources/application.properties` → `spring.data.mongodb.uri`

---

## 5. Project structure

```
ems/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/ems/
    │   ├── EmsApplication.java
    │   ├── model/        (Employee, Department, Payroll, Leave, LeaveBalance, User, enums)
    │   ├── repository/   (Spring Data MongoDB repositories)
    │   ├── service/      (business logic, DataSeeder)
    │   ├── controller/   (REST controllers, GlobalExceptionHandler)
    │   └── dto/           (LoginRequest / LoginResponse)
    └── resources/
        ├── application.properties
        └── static/        (login.html, admin-dashboard.html, employee-dashboard.html, css/, js/)
```

---

## 6. Notes for your project report / viva

- Passwords are stored in plain text (`"123"` for everyone) intentionally, to keep the
  login flow simple for a classroom project. In a real system you would hash passwords
  (e.g. BCrypt) and use Spring Security with sessions/JWT instead of the simplified
  localStorage-based login used here.
- The frontend is plain HTML/CSS/JS calling the REST API with `fetch()` — no framework,
  so it's easy to read and explain in a viva.
- Since the frontend is served from Spring Boot's own `static/` folder, there is **no CORS
  configuration needed** — everything runs on `http://localhost:8080`.
