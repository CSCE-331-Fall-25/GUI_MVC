package controller;

import dao.MenuItemDao;
import model.MenuItem;
import view.MenuView;

import javax.swing.*;
import java.util.List;

public class MenuController {
  private final MenuView view;
  private final MenuItemDao dao = new MenuItemDao();

  // We cache the currently displayed items to map list selection -> item id
  private List<MenuItem> cache = List.of();

  public MenuController(MenuView view) {
    this.view = view;

    // Wire button actions
    view.refreshBtn.addActionListener(e -> refresh());
    view.addBtn.addActionListener(e -> addItem());
    view.updateBtn.addActionListener(e -> updatePrice());
    view.deleteBtn.addActionListener(e -> deleteSelected());

    // Initial data load
    refresh();
  }

  private void refresh() {
    // Run DB work off the Event Dispatch Thread
    new SwingWorker<List<MenuItem>, Void>() {
      @Override
      protected List<MenuItem> doInBackground() throws Exception {
        return dao.findAll();
      }
      @Override
      protected void done() {
        try {
          cache = get();
          view.listModel.clear();
          for (MenuItem m : cache) {
            view.listModel.addElement("#" + m.getId() + "  " + m.toString());
          }
        } catch (Exception ex) {
          showError("Load failed: " + ex.getMessage());
        }
      }
    }.execute();
  }

  private void addItem() {
    String name = view.nameField.getText().trim();
    String priceText = view.priceField.getText().trim();
    if (name.isEmpty() || priceText.isEmpty()) {
      showError("Name and price are required.");
      return;
    }
    double price;
    try {
      price = Double.parseDouble(priceText);
      if (price < 0) throw new NumberFormatException("Negative");
    } catch (NumberFormatException nfe) {
      showError("Price must be a non-negative number.");
      return;
    }

    new SwingWorker<Integer, Void>() {
      @Override protected Integer doInBackground() throws Exception {
        return dao.insert(name, price);
      }
      @Override protected void done() {
        try {
          int rows = get();
          if (rows == 1) {
            clearInputs();
            refresh();
          } else {
            showError("Insert did not affect any rows.");
          }
        } catch (Exception ex) {
          showError("Insert failed: " + ex.getMessage());
        }
      }
    }.execute();
  }

  private void updatePrice() {
    int idx = view.list.getSelectedIndex();
    if (idx < 0) {
      showError("Select an item to update.");
      return;
    }
    String priceText = view.priceField.getText().trim();
    if (priceText.isEmpty()) {
      showError("Enter a new price in the Price field.");
      return;
    }
    double price;
    try {
      price = Double.parseDouble(priceText);
      if (price < 0) throw new NumberFormatException("Negative");
    } catch (NumberFormatException nfe) {
      showError("Price must be a non-negative number.");
      return;
    }
    int id = cache.get(idx).getId();

    new SwingWorker<Integer, Void>() {
      @Override protected Integer doInBackground() throws Exception {
        return dao.updatePrice(id, price);
      }
      @Override protected void done() {
        try {
          int rows = get();
          if (rows == 1) refresh();
          else showError("Update did not affect any rows.");
        } catch (Exception ex) {
          showError("Update failed: " + ex.getMessage());
        }
      }
    }.execute();
  }

  private void deleteSelected() {
    int idx = view.list.getSelectedIndex();
    if (idx < 0) {
      showError("Select an item to delete.");
      return;
    }
    int confirm = JOptionPane.showConfirmDialog(view,
        "Delete the selected item?", "Confirm Delete", JOptionPane.OK_CANCEL_OPTION);
    if (confirm != JOptionPane.OK_OPTION) return;

    int id = cache.get(idx).getId();

    new SwingWorker<Integer, Void>() {
      @Override protected Integer doInBackground() throws Exception {
        return dao.delete(id);
      }
      @Override protected void done() {
        try {
          int rows = get();
          if (rows == 1) refresh();
          else showError("Delete did not affect any rows.");
        } catch (Exception ex) {
          showError("Delete failed: " + ex.getMessage());
        }
      }
    }.execute();
  }

  private void clearInputs() {
    view.nameField.setText("");
    view.priceField.setText("");
  }

  private void showError(String msg) {
    JOptionPane.showMessageDialog(view, msg, "Error", JOptionPane.ERROR_MESSAGE);
  }
}
