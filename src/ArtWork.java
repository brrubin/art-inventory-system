import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ArtWork {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Art-Inventory-System"); // Title of GUI
        frame.setSize(1000, 800); //Size of window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        frame.setLocationRelativeTo(null); // Centers the window on the device

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // Title Label

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        JLabel titleLabel = new JLabel("Art Inventory System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(titleLabel, gbc);

        gbc.gridwidth = 1; // reset

        // Search Label

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel searchLabel = new JLabel("Search:");
        frame.add(searchLabel, gbc);

        // Search TextField

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField searchTextField = new JTextField(20);
        frame.add(searchTextField, gbc);

        // Search Button

        gbc.gridx = 2;
        gbc.gridy = 1;
        JButton searchButton = new JButton("Search");
        frame.add(searchButton, gbc);

        // Clear Button

        gbc.gridx = 3;
        gbc.gridy = 1;
        JButton clearButton = new JButton("Clear");
        frame.add(clearButton, gbc);

        // Add artwork button

        gbc.gridx = 0;
        gbc.gridy = 2;
        JButton addButton = new JButton("Add Artwork");
        frame.add(addButton, gbc);

        // Edit selected button

        gbc.gridx = 1;
        gbc.gridy = 2;
        JButton editButton = new JButton("Edit Selected");
        frame.add(editButton, gbc);

        // Delete selected button

        gbc.gridx = 2;
        gbc.gridy = 2;
        JButton deleteButton = new JButton("Delete Selected");
        frame.add(deleteButton, gbc);

        // Table with columns: ID, Title, Artist, Year, Medium, Location, Price

        String[] columnNames = {"ID", "Title", "Artist", "Year", "Medium", "Location", "Price"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);

        // JScrollPane for table

        JScrollPane scrollPane = new JScrollPane(table);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth =4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        frame.add(scrollPane, gbc);

        // Status label at the bottom

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        gbc.weighty = 0;
        JLabel statusLabel = new JLabel("Total artworks: 0");
        frame.add(statusLabel, gbc);

        // ID generator

        final int[] nextId ={1};

        // Add artwork using JOptionPane

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = JOptionPane.showInputDialog(
                        frame,
                        "Enter title:",
                        "Add Artwork",
                        JOptionPane.PLAIN_MESSAGE
                );

                if (title == null) {
                    return;
                }
                if (title.trim().isEmpty()){
                    JOptionPane.showMessageDialog(frame, "Title cannot be empty");
                    return;
                }
                String artist = JOptionPane.showInputDialog(
                        frame,
                        "Enter artist:",
                        "Add Artwork",
                        JOptionPane.PLAIN_MESSAGE
                );
                if (artist == null){
                    return;
                }
                if (artist.trim().isEmpty()){
                    JOptionPane.showMessageDialog(frame, "Artist cannot be empty");
                    return;
                }

                Object[] row = new Object[]{
                        nextId[0],
                        title.trim(),
                        artist.trim(),
                        null,   // Year
                        null,   // Medium
                        null,   // Location
                        null    // Price
                };
                nextId[0]++;

                tableModel.addRow(row);
                updateStatus(statusLabel, tableModel);
            }
        });

        // Edit selected row using JOptionPane (all fields)

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Please select a row to edit.",
                            "No selection",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }

                // Get current values from the table

                String currentTitle = (tableModel.getValueAt(selectedRow, 1) + "");
                String currentArtist = (tableModel.getValueAt(selectedRow, 2) + "");
                Object currentYearObj = tableModel.getValueAt(selectedRow, 3);
                Object currentMediumObj = tableModel.getValueAt(selectedRow, 4);
                Object currentLocationObj = tableModel.getValueAt(selectedRow, 5);
                Object currentPriceObj = tableModel.getValueAt(selectedRow, 6);

                String currentYear = currentYearObj == null ? "" : currentYearObj.toString();
                String currentMedium = currentMediumObj == null ? "" : currentMediumObj.toString();
                String currentLocation = currentLocationObj == null ? "" : currentLocationObj.toString();
                String currentPrice = currentPriceObj == null ? "" : currentPriceObj.toString();

                // Title

                String newTitle = JOptionPane.showInputDialog(
                        frame,
                        "Edit title:",
                        currentTitle
                );
                if (newTitle == null) {
                    return; // cancel
                }
                if (newTitle.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Title cannot be empty.");
                    return;
                }

                // Artist

                String newArtist = JOptionPane.showInputDialog(
                        frame,
                        "Edit artist:",
                        currentArtist
                );
                if (newArtist == null) {
                    return;
                }
                if (newArtist.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Artist cannot be empty.");
                    return;
                }

                // Year

                String newYearInput = JOptionPane.showInputDialog(
                        frame,
                        "Edit year (optional):",
                        currentYear
                );
                Integer newYearValue = null;
                if (newYearInput != null && !newYearInput.trim().isEmpty()) {
                    try {
                        newYearValue = Integer.parseInt(newYearInput.trim());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Year must be a number.");
                        return;
                    }
                }

                //Medium

                String newMedium = JOptionPane.showInputDialog(
                        frame,
                        "Edit medium (optional):",
                        currentMedium
                );
                if (newMedium == null) {
                    newMedium = currentMedium; // keep old if cancel
                }

                //Location

                String newLocation = JOptionPane.showInputDialog(
                        frame,
                        "Edit location (optional):",
                        currentLocation
                );
                if (newLocation == null) {
                    newLocation = currentLocation;
                }

                // Price

                String newPriceInput = JOptionPane.showInputDialog(
                        frame,
                        "Edit price (optional):",
                        currentPrice
                );
                Double newPriceValue = null;
                if (newPriceInput != null && !newPriceInput.trim().isEmpty()) {
                    try {
                        newPriceValue = Double.parseDouble(newPriceInput.trim());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Price must be a valid number.");
                        return;
                    }
                }

                // write everything back to the table

                tableModel.setValueAt(newTitle.trim(), selectedRow, 1);
                tableModel.setValueAt(newArtist.trim(), selectedRow, 2);
                tableModel.setValueAt(newYearValue, selectedRow, 3);
                tableModel.setValueAt(newMedium.trim().isEmpty() ? null : newMedium.trim(), selectedRow, 4);
                tableModel.setValueAt(newLocation.trim().isEmpty() ? null : newLocation.trim(), selectedRow, 5);
                tableModel.setValueAt(newPriceValue, selectedRow, 6);
            }
        });

        // Delete selected row

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Please select a row to delete.",
                            "No selection",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(
                        frame,
                        "Are you sure you want to delete this artwork?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    tableModel.removeRow(selectedRow);
                    updateStatus(statusLabel, tableModel);
                }
            }
        });

        // Filter table by title/artist/medium

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String query = searchTextField.getText().trim().toLowerCase();
                if (query.isEmpty()) {
                    return;
                }

                // highlight the first matching row

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String title = (tableModel.getValueAt(i, 1) + "").toLowerCase();
                    String artist = (tableModel.getValueAt(i, 2) + "").toLowerCase();
                    String medium = (tableModel.getValueAt(i, 4) + "").toLowerCase();

                    if (title.contains(query) || artist.contains(query) || medium.contains(query)) {
                        table.setRowSelectionInterval(i, i);
                        table.scrollRectToVisible(table.getCellRect(i, 0, true));
                        return;
                    }
                }

                JOptionPane.showMessageDialog(
                        frame,
                        "No artworks found for: " + query,
                        "Search",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        // Reset table on clear

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchTextField.setText("");
                table.clearSelection();
            }
        });

        frame.setVisible(true); // Makes sure the GUI is visible
    }

    private static void updateStatus(JLabel statusLabel, DefaultTableModel tableModel) {
        int count = tableModel.getRowCount();
        statusLabel.setText("Total artworks: " + count);
    }
}