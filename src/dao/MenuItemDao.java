package dao;

import model.MenuItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuItemDao {

  // Adjust table/column names to your schema if needed
  private static final String TABLE = "menu_items";
  private static final String COL_ID = "id";
  private static final String COL_NAME = "item_name";
  private static final String COL_PRICE = "price";

  public List<MenuItem> findAll() throws SQLException {
    String sql = "SELECT " + COL_ID + ", " + COL_NAME + ", " + COL_PRICE +
                 " FROM " + TABLE + " ORDER BY " + COL_NAME;
    List<MenuItem> out = new ArrayList<>();
    try (Connection c = Db.connect();
         Statement s = c.createStatement();
         ResultSet r = s.executeQuery(sql)) {
      while (r.next()) {
        out.add(new MenuItem(
            r.getInt(COL_ID),
            r.getString(COL_NAME),
            r.getDouble(COL_PRICE)
        ));
      }
    }
    return out;
  }

  public int insert(String name, double price) throws SQLException {
    String sql = "INSERT INTO " + TABLE + " (" + COL_NAME + ", " + COL_PRICE + ") VALUES (?, ?)";
    try (Connection c = Db.connect();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, name);
      ps.setDouble(2, price);
      return ps.executeUpdate(); // rows affected (expect 1)
    }
  }

  public int updatePrice(int id, double newPrice) throws SQLException {
    String sql = "UPDATE " + TABLE + " SET " + COL_PRICE + "=? WHERE " + COL_ID + "=?";
    try (Connection c = Db.connect();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setDouble(1, newPrice);
      ps.setInt(2, id);
      return ps.executeUpdate();
    }
  }

  public int delete(int id) throws SQLException {
    String sql = "DELETE FROM " + TABLE + " WHERE " + COL_ID + "=?";
    try (Connection c = Db.connect();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, id);
      return ps.executeUpdate();
    }
  }
}
