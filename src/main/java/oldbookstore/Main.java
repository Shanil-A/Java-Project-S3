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

        // Launch the Swing GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            }
        });
    }
}
