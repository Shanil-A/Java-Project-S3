package oldbookstore;

import javax.swing.SwingUtilities;

/**
 * Main entry point for the Old Book Buy & Sell Register application.
 * Creates and displays the main application window.
 */
public class Main {

    public static void main(String[] args) {
        // Initialize the database tables on startup
        DatabaseConnection.initializeDatabase();

        // Launch the Login & Register authentication interface on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            }
        });
    }
}
