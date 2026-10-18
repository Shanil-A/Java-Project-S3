package oldbookstore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

/**
 * Authentication window providing a direct Login interface.
 * Defaults to username: admin, password: admin.
 */
public class LoginFrame extends JFrame {

    // ==================== COLORS AND FONTS ====================

    private static final Color BG_DARK = new Color(24, 24, 32);
    private static final Color BG_CARD = new Color(38, 38, 52);
    private static final Color BG_INPUT = new Color(46, 46, 62);
    private static final Color TEXT_PRIMARY = new Color(230, 230, 240);
    private static final Color TEXT_SECONDARY = new Color(150, 150, 170);
    private static final Color ACCENT = new Color(100, 140, 255);
    private static final Color ACCENT_HOVER = new Color(130, 165, 255);
    private static final Color DANGER = new Color(220, 75, 75);
    private static final Color BADGE_BG = new Color(30, 40, 65);

    private static final Font FONT_HEADER_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_HEADER_SUB = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_INPUT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_HINT = new Font("Segoe UI", Font.BOLD, 12);

    // ==================== STATE AND DAOS ====================

    private final UserDAO userDAO = new UserDAO();

    // Login Form Components
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblStatus;
    private JButton btnLogin;

    // ==================== CONSTRUCTOR ====================

    public LoginFrame() {
        setTitle("Old Book Store - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 520);
        setResizable(false);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);

        initComponents();

        // Focus password field by default since username is pre-filled with admin
        SwingUtilities.invokeLater(() -> txtPassword.requestFocusInWindow());
    }

    // ==================== UI INITIALIZATION ====================

    private void initComponents() {
        setLayout(new BorderLayout());

        // Main outer container with padding
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(BG_DARK);
        container.setBorder(new EmptyBorder(30, 35, 30, 35));

        // 1. Header Section
        JPanel headerPanel = createHeaderPanel();
        container.add(headerPanel);
        container.add(Box.createVerticalStrut(20));

        // 2. Login Card
        JPanel loginCard = createLoginCard();
        container.add(loginCard);

        add(container, BorderLayout.CENTER);
    }

    /** Creates the top title and subtitle header. */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BG_DARK);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appTitle = new JLabel("Old Book Store");
        appTitle.setFont(FONT_HEADER_TITLE);
        appTitle.setForeground(ACCENT);
        appTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTitle = new JLabel("Buy & Sell Register System");
        subTitle.setFont(FONT_HEADER_SUB);
        subTitle.setForeground(TEXT_SECONDARY);
        subTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(appTitle);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subTitle);

        return panel;
    }

    /** Creates the Login card form. */
    private JPanel createLoginCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(BG_CARD);
        card.setBorder(new EmptyBorder(22, 22, 22, 22));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Default login info badge
        JPanel hintBadge = new JPanel();
        hintBadge.setLayout(new BoxLayout(hintBadge, BoxLayout.Y_AXIS));
        hintBadge.setBackground(BADGE_BG);
        hintBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 90, 140), 1),
                new EmptyBorder(8, 12, 8, 12)));
        hintBadge.setAlignmentX(Component.LEFT_ALIGNMENT);
        hintBadge.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel hintTitle = new JLabel("Default Login Credentials:");
        hintTitle.setFont(FONT_SMALL);
        hintTitle.setForeground(TEXT_SECONDARY);

        JLabel hintCreds = new JLabel("Username: Admin  |  Password: admin");
        hintCreds.setFont(FONT_HINT);
        hintCreds.setForeground(ACCENT);

        hintBadge.add(hintTitle);
        hintBadge.add(Box.createVerticalStrut(2));
        hintBadge.add(hintCreds);

        // Username
        JLabel lblUsername = createFormLabel("Username");
        txtUsername = createStyledTextField();
        txtUsername.setText("Admin"); // Default to Admin for convenience

        // Password
        JLabel lblPassword = createFormLabel("Password");
        txtPassword = createStyledPasswordField();

        // Show/Hide password toggle
        JCheckBox chkShowPassword = createShowPasswordCheckBox(txtPassword);

        // Status / Error message
        lblStatus = new JLabel(" ");
        lblStatus.setFont(FONT_SMALL);
        lblStatus.setForeground(DANGER);
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login Action Button
        btnLogin = createStyledButton("Log In", ACCENT);
        btnLogin.addActionListener(e -> handleLogin());

        // Keyboard Enter listener
        KeyAdapter enterKey = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterKey);
        txtPassword.addKeyListener(enterKey);

        // Assemble Login Card
        card.add(hintBadge);
        card.add(Box.createVerticalStrut(18));

        card.add(lblUsername);
        card.add(Box.createVerticalStrut(6));
        card.add(txtUsername);
        card.add(Box.createVerticalStrut(14));

        card.add(lblPassword);
        card.add(Box.createVerticalStrut(6));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(6));

        card.add(chkShowPassword);
        card.add(Box.createVerticalStrut(12));

        card.add(lblStatus);
        card.add(Box.createVerticalStrut(14));

        card.add(btnLogin);

        getRootPane().setDefaultButton(btnLogin);

        return card;
    }

    // ==================== ACTION HANDLER ====================

    /** Handles the Login action. */
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty()) {
            showLoginError("Please enter your username.");
            txtUsername.requestFocusInWindow();
            return;
        }

        if (password.isEmpty()) {
            showLoginError("Please enter your password.");
            txtPassword.requestFocusInWindow();
            return;
        }

        try {
            User user = userDAO.authenticate(username, password);
            if (user != null) {
                // Authentication succeeded
                launchMainApplication(user);
            } else {
                showLoginError("Invalid username or password.");
                txtPassword.setText("");
                txtPassword.requestFocusInWindow();
            }
        } catch (SQLException ex) {
            showLoginError("Database error during login: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /** Launches the main application and disposes of the login frame. */
    private void launchMainApplication(User user) {
        dispose();
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(user);
            mainFrame.setVisible(true);
        });
    }

    private void showLoginError(String message) {
        lblStatus.setText(message);
        lblStatus.setForeground(DANGER);
    }

    // ==================== UI HELPER METHODS ====================

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(FONT_INPUT);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(BG_INPUT);
        field.setCaretColor(TEXT_PRIMARY);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setPreferredSize(new Dimension(340, 38));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 80), 1),
                new EmptyBorder(6, 12, 6, 12)));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(FONT_INPUT);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(BG_INPUT);
        field.setCaretColor(TEXT_PRIMARY);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setPreferredSize(new Dimension(340, 38));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 80), 1),
                new EmptyBorder(6, 12, 6, 12)));
        return field;
    }

    private JCheckBox createShowPasswordCheckBox(JPasswordField passwordField) {
        JCheckBox chk = new JCheckBox("Show Password");
        chk.setFont(FONT_SMALL);
        chk.setForeground(TEXT_SECONDARY);
        chk.setBackground(BG_CARD);
        chk.setFocusPainted(false);
        chk.setAlignmentX(Component.LEFT_ALIGNMENT);
        chk.addActionListener(e -> {
            passwordField.setEchoChar(chk.isSelected() ? (char) 0 : '•');
        });
        return chk;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        button.setPreferredSize(new Dimension(340, 42));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        Color hoverColor = bgColor.brighter();
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }
}
