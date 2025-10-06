package view;

import javax.swing.*;
import java.awt.*;

public class MenuView extends JFrame {
  // Inputs
  public final JTextField nameField  = new JTextField(16);
  public final JTextField priceField = new JTextField(8);

  // Action buttons
  public final JButton addBtn     = new JButton("Add Item");
  public final JButton refreshBtn = new JButton("Refresh");
  public final JButton updateBtn  = new JButton("Update Price");
  public final JButton deleteBtn  = new JButton("Delete");

  // Display list
  public final DefaultListModel<String> listModel = new DefaultListModel<>();
  public final JList<String> list = new JList<>(listModel);

  public MenuView() {
    super("Panda Express — Manager: Menu Items");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(600, 420);
    setLocationRelativeTo(null);

    // Top form row
    JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
    form.add(new JLabel("Name:"));
    form.add(nameField);
    form.add(new JLabel("Price:"));
    form.add(priceField);
    form.add(addBtn);
    form.add(refreshBtn);

    // Middle list
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    JScrollPane scroll = new JScrollPane(list);

    // Bottom actions
    JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    actions.add(updateBtn);
    actions.add(deleteBtn);

    // Root layout
    JPanel root = new JPanel(new BorderLayout(8,8));
    root.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
    root.add(form, BorderLayout.NORTH);
    root.add(scroll, BorderLayout.CENTER);
    root.add(actions, BorderLayout.SOUTH);
    setContentPane(root);
  }
}
