import javax.swing.*;
import javax.swing.plaf.nimbus.State;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ArtWork {

    private static final String DB_URL = "jdbc:sqlite:db/art_inventory.db";

    private static void initDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS artwork (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "artist TEXT NOT NULL," +
                "year INTEGER," +
                "medium TEXT," +
                "location TEXT," +
                "price REAL," +
                "image BLOB" +
                ");";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void loadFromDatabase(DefaultTableModel tableModel, JLabel statusLabel){
        tableModel.setRowCount(0);
        String sql = "SELECT id, title, artist, year, medium, location, price FROM artwork ORDER BY id;";

        try(Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){

            while (rs.next()){
                Object[] row = new Object[]{
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("artist"),
                        (rs.getObject("year") == null ? null : rs.getInt("year")),
                        rs.getString("medium"),
                        rs.getString("location"),
                        (rs.getObject("price") == null ? null : rs.getDouble("price"))
                };
                tableModel.addRow(row);
            }

            updateStatus(statusLabel, tableModel);
        }catch (SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Database error while loading:\n" + e.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        initDatabase();

        JFrame frame = new JFrame("Art-Inventory-System"); // Title of GUI
        frame.setSize(850, 550); //Size of window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        frame.setLocationRelativeTo(null); // Centers the window on the device

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // Title Label at the top of the GUI
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel titleLabel = new JLabel("Art Inventory System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20)); //Changes the font and size of my title
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(titleLabel, gbc);

        gbc.gridwidth = 1; // reset
        gbc.anchor = GridBagConstraints.NORTHWEST;

        JTextField searchTextField = new JTextField(20); // Search TextField
        JButton searchButton = new JButton("Search");  // Search Button
        JButton clearButton = new JButton("Clear");  // Clear Button
        JButton addButton = new JButton("Add Artwork");  // Add artwork button
        JButton editButton = new JButton("Edit Selected");  // Edit selected button
        JButton deleteButton = new JButton("Delete Selected"); // Delete selected button
        JButton saveButton = new JButton("Save"); // Save button
        JButton loadButton = new JButton("Load"); // Load button
        JButton exitButton = new JButton("Exit"); //Exit button

        JPanel managePanel = new JPanel(new GridBagLayout());
        JPanel searchPanel = new JPanel(new GridBagLayout());

        GridBagConstraints m = new GridBagConstraints();
        GridBagConstraints s = new GridBagConstraints();

        m.insets = new Insets(5,5,5,5);
        m.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 0.5;
        frame.add(managePanel, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0.5;
        frame.add(searchPanel, gbc);
        m.gridx = 0;

        m.gridy = 0;
        managePanel.add(addButton, m);

        m.gridy = 1;
        managePanel.add(editButton, m);

        m.gridy = 2;
        managePanel.add(deleteButton, m);

        m.gridy = 3;
        managePanel.add(saveButton, m);

        m.gridy = 4;
        managePanel.add(loadButton, m);

        m.gridy = 5;
        managePanel.add(exitButton, m);

        s.insets = new Insets(5,5,5,5);
        s.fill = GridBagConstraints.HORIZONTAL;
        s.gridx = 0;

        s.gridy = 0;
        searchPanel.add(new JLabel("Search"), s);

        s.gridy = 1;
        searchPanel.add(searchTextField, s);

        s.gridy = 2;
        searchPanel.add(searchButton, s);

        s.gridy = 3;
        searchPanel.add(clearButton, s);

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
        gbc.gridy = 2;
        gbc.gridwidth =4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        frame.add(scrollPane, gbc);

        // Status label at the bottom
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        gbc.weighty = 0;

        JLabel statusLabel = new JLabel("Total artworks: 0");
        frame.add(statusLabel, gbc);

        loadFromDatabase(tableModel, statusLabel);

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

                Integer yearValue = null;

                while (true){
                    String yearInput = JOptionPane.showInputDialog(
                            frame,
                            "Enter year:",
                            "Add Artwork",
                            JOptionPane.PLAIN_MESSAGE
                    );

                    if (yearInput == null){
                        return;
                    }

                    yearInput = yearInput.trim();

                    if(yearInput.isEmpty()){
                        yearValue = null;
                        break;
                    }

                    try{
                        yearValue = Integer.parseInt(yearInput);
                        break;
                    } catch (NumberFormatException ex){
                        JOptionPane.showMessageDialog(
                                frame,
                                "Year must be a valid number",
                                "Input error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }

                String medium = JOptionPane.showInputDialog(
                        frame,
                        "Enter medium:",
                        "Add Artwork",
                        JOptionPane.PLAIN_MESSAGE
                );
                if (medium == null){
                    return;
                }
                medium = medium.trim();
                if(medium.isEmpty()){
                    medium = null;
                }

                String location = JOptionPane.showInputDialog(
                        frame,
                        "Enter location:",
                        "Add Artwork",
                        JOptionPane.PLAIN_MESSAGE
                );
                if(location == null){
                    return;
                }
                location = location.trim();
                if(location.isEmpty()){
                    location = null;
                }

                Double priceValue = null; // Price Validation was just fixed

                while (true){
                    String priceInput = JOptionPane.showInputDialog(
                            frame,
                            "Enter price:",
                            "Add Artwork",
                            JOptionPane.PLAIN_MESSAGE
                    );

                    if (priceInput == null){
                        return;
                    }

                    priceInput = priceInput.trim();

                    if (priceInput.isEmpty()){
                        priceValue = null;
                        break;
                    }

                    try{
                        priceValue = Double.parseDouble(priceInput);
                        break;
                    } catch (NumberFormatException ex){
                        JOptionPane.showMessageDialog(
                                frame,
                                "Price must be a valid number",
                                "Input error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }

                Object[] row = new Object[]{
                        nextId[0],
                        title.trim(),
                        artist.trim(),
                        yearValue,
                        medium,
                        location,
                        priceValue
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

                String currentYear = currentYearObj == null? "": currentYearObj.toString();
                String currentMedium = currentMediumObj == null? "": currentMediumObj.toString();
                String currentLocation = currentLocationObj == null? "": currentLocationObj.toString();
                String currentPrice = currentPriceObj == null? "": currentPriceObj.toString();

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

                Integer newYearValue = null; // Just made an exception handle for editing the year

                while(true){
                    String newYearInput = JOptionPane.showInputDialog(
                            frame,
                            "Edit year (optional):",
                            currentYear
                    );

                    if (newYearInput == null){
                        return;
                    }

                    newYearInput = newYearInput.trim();

                    if (newYearInput.isEmpty()){
                        newYearValue = null;
                        break;
                    }

                    try{
                        newYearValue = Integer.parseInt(newYearInput);
                        break;
                    }catch (NumberFormatException ex){
                        JOptionPane.showMessageDialog(
                                frame,
                                "Year must be a valid number",
                                "Input error",
                                JOptionPane.ERROR_MESSAGE
                        );
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

                // This new block of code improved the validation with edit so when something is invalid there is now an exception for
                Double newPriceValue = null;
                while(true){
                    String newPriceInput = JOptionPane.showInputDialog(
                            frame,
                            "Edit price (optional):",
                            currentPrice
                    );
                    if (newPriceInput == null){
                        return;
                    }
                    newPriceInput = newPriceInput.trim();
                    if (newPriceInput.isEmpty()){
                        newPriceValue = null;
                        break;
                    }
                    try{
                        newPriceValue = Double.parseDouble(newPriceInput);
                        break;
                    }catch (NumberFormatException ex){
                        JOptionPane.showMessageDialog(
                                frame,
                                "Price must be a valid number.",
                                "Input error",
                                JOptionPane.ERROR_MESSAGE
                        );
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

        // Make the Exit button work with an action listener

        exitButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                int choice = JOptionPane.showConfirmDialog(
                        frame,
                        "Are you sure you want to exit?",
                        "Exit",
                        JOptionPane.YES_NO_OPTION
                );

                if(choice == JOptionPane.YES_OPTION){
                    frame.dispose(); // this closes the window
                }
            }
        });

        frame.setVisible(true); // Makes sure the GUI is visible
    }

    private static void updateStatus(JLabel statusLabel, DefaultTableModel tableModel) {
        int count = tableModel.getRowCount();
        statusLabel.setText("Total artworks: " + count);
    }
}