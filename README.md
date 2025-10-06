# GUI_MVC
MVC version of the simple GUI.java example provided in class

# Project Layout

```bash
src/
  model/
    MenuItem.java          // domain object (a row from menu table)
  dao/
    Db.java                // opens JDBC Connection
    MenuItemDao.java       // SQL lives here (SELECT/INSERT/UPDATE)
  view/
    MenuView.java          // Swing UI: JFrame, JPanel, buttons, fields
  controller/
    MenuController.java    // button handlers call DAO methods
  App.java                 // main(): wires View + Controller

