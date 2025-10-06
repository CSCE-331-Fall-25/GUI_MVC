# GUI_MVC
MVC version of the simple GUI.java example provided in class

---

## Project Layout

```
repo/
  lib/
    postgresql-42.2.8.jar           # JDBC driver (put here)
    # sqlite-jdbc-<ver>.jar         # OPTIONAL: if you want local dev with SQLite
  src/
    app/
      App.java                      # Entry point
    model/
      MenuItem.java                 # Domain object (row from menu_items)
    dao/
      Db.java                       # Centralized JDBC connection factory
      MenuItemDao.java              # All SQL/JDBC for menu_items lives here
    view/
      MenuView.java                 # Swing UI (JFrame, fields, buttons, list)
    controller/
      MenuController.java           # Wires events → DAO calls → refresh UI
```

---

## Requirements

* Java 8+ (Java 17 recommended). Verify with:

  ```bash
  java -version
  javac -version
  ```
* JDBC driver jars in `lib/`:

  * **PostgreSQL**: `postgresql-42.2.8.jar` (or newer)
  * **SQLite (optional for dev)**: `sqlite-jdbc-<ver>.jar`

---

## Configuration

Set DB connection via environment variables (recommended):

* `DB_URL` – JDBC URL

  * PostgreSQL example: `jdbc:postgresql://csce-315-db.engr.tamu.edu/<DBNAME>`
  * SQLite example: `jdbc:sqlite:/absolute/path/dev.db`
* `DB_USER` – DB username (ignored by SQLite)
* `DB_PASS` – DB password (ignored by SQLite)

`Db.java` reads these vars and falls back to:

```
DB_URL  = jdbc:postgresql://localhost:5432/panda_express
DB_USER = postgres
DB_PASS = password
```

---

## Build & Run (Command Line)

> Replace the classpath separator with `;` on Windows and `:` on macOS/Linux.

### macOS/Linux

```bash
# from repo root
mkdir -p out
javac -cp ".:lib/postgresql-42.2.8.jar" -d out $(find src -name "*.java")
java  -cp "out:lib/postgresql-42.2.8.jar" app.App
```

**Using SQLite for dev:** (add the sqlite jar to the classpath and set DB_URL)

```bash
export DB_URL="jdbc:sqlite:/absolute/path/dev.db"
javac -cp ".:lib/postgresql-42.2.8.jar:lib/sqlite-jdbc-<ver>.jar" -d out $(find src -name "*.java")
java  -cp "out:lib/postgresql-42.2.8.jar:lib/sqlite-jdbc-<ver>.jar" app.App
```

### Windows (PowerShell/CMD)

```bat
REM from repo root
mkdir out
for /r %f in (src\*.java) do @echo %f >> sources.txt
javac -cp ".;lib\postgresql-42.2.8.jar" -d out @sources.txt
java  -cp "out;lib\postgresql-42.2.8.jar" app.App
```

**Using SQLite for dev:**

```bat
set DB_URL=jdbc:sqlite:C:\absolute\path\dev.db
javac -cp ".;lib\postgresql-42.2.8.jar;lib\sqlite-jdbc-<ver>.jar" -d out @sources.txt
java  -cp "out;lib\postgresql-42.2.8.jar;lib\sqlite-jdbc-<ver>.jar" app.App
```

---

## What You’ll See

* A Swing window titled **“Panda Express — Manager: Menu Items”**.
* **Refresh**: loads all rows from `menu_items`.
* **Add Item**: inserts a new row with `item_name`, `price`.
* **Update Price**: updates selected item’s price.
* **Delete**: removes selected item.

> Adjust table/column names in `MenuItemDao.java` if your schema differs (defaults assume `menu_items(id, item_name, price)`).

---

## How This Is MVC (and why it matters)

**MVC = separate concerns; each file has one job.**

* **Model** (data + DB access)

  * `model/MenuItem.java`: simple domain object (id, name, price).
  * `dao/MenuItemDao.java`: **all JDBC/SQL lives here**. Uses `executeQuery` for `SELECT` and `executeUpdate` for `INSERT/UPDATE/DELETE`. Returns domain objects or row counts.
  * `dao/Db.java`: one place to open a JDBC connection (Postgres or SQLite) based on `DB_URL`.
* **View** (what the user sees)

  * `view/MenuView.java`: **pure Swing** (JFrame/JPanel/buttons/fields/list). **No SQL here.**
* **Controller** (glue/logic)

  * `controller/MenuController.java`: listens to button clicks, **calls DAO methods**, and refreshes the view. Runs DB work off the Swing Event Dispatch Thread (via `SwingWorker`) so the UI doesn’t freeze.

**Data flow for “Add Item”:**

1. User types name/price → clicks **Add Item** (View)
2. Controller reads fields, validates, calls `MenuItemDao.insert(...)` (Controller → Model)
3. DAO executes `INSERT` via JDBC (Model)
4. Controller calls `refresh()` to `SELECT` items and updates list (Controller → View)

This separation:

* keeps SQL out of the GUI,
* makes testing easier,
* allows swapping databases (Postgres↔SQLite) without touching GUI/Controller code.

---

## Quick Start Database (Optional)

**PostgreSQL**

```sql
CREATE TABLE IF NOT EXISTS menu_items (
  id SERIAL PRIMARY KEY,
  item_name TEXT NOT NULL,
  price NUMERIC(10,2) NOT NULL CHECK (price >= 0)
);

INSERT INTO menu_items(item_name, price) VALUES
('Orange Chicken', 10.50),
('Beijing Beef', 11.25),
('Fried Rice', 3.95);
```

**SQLite**

```sql
CREATE TABLE IF NOT EXISTS menu_items (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  item_name TEXT NOT NULL,
  price REAL NOT NULL CHECK (price >= 0)
);
```

---
