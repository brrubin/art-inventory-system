import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ArtWork {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Art-Inventory-System"); // Title of GUI
        frame.setSize(1000, 1000); //Size of window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        frame.setLocationRelativeTo(null); // Centers the window on the device
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        frame.setVisible(true); // Makes sure the GUI is visible
    }
}