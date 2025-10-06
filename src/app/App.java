package app;

import javax.swing.SwingUtilities;
import view.MenuView;
import controller.MenuController;

public class App {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      MenuView view = new MenuView();
      new MenuController(view);   // wires handlers + initial refresh
      view.setVisible(true);
    });
  }
}
