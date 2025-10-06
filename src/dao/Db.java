package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {
  // Reads DB config from environment variables with sensible fallbacks.
  private static final String URL  = getenvOr("DB_URL",  "jdbc:postgresql://localhost:5432/panda_express");
  private static final String USER = getenvOr("DB_USER", "postgres");
  private static final String PASS = getenvOr("DB_PASS", "password");

  private static String getenvOr(String k, String def) {
    String v = System.getenv(k);
    return (v == null || v.isEmpty()) ? def : v;
  }

  static {
    // Try to load a driver explicitly (helpful on some setups).
    try {
      if (URL.startsWith("jdbc:postgresql:")) {
        Class.forName("org.postgresql.Driver");
      } else if (URL.startsWith("jdbc:sqlite:")) {
        Class.forName("org.sqlite.JDBC");
      }
    } catch (Throwable ignored) {
      // Modern JDBC can autoload drivers from the JAR; failure here isn't fatal.
    }
  }

  public static Connection connect() throws SQLException {
    // SQLite ignores USER/PASS; Postgres uses them.
    if (URL.startsWith("jdbc:sqlite:")) {
      return DriverManager.getConnection(URL);
    } else {
      return DriverManager.getConnection(URL, USER, PASS);
    }
  }
}
