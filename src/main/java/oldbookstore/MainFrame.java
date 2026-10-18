package oldbookstore;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Main application window for the Old Book Buy & Sell Register.
 * Contains sidebar navigation and swappable content panels for
 * Dashboard, Books, Buy Books, and Sell Books screens.
 */
public class MainFrame extends JFrame {

    // ==================== COLORS AND FONTS ====================

    private static final Color BG_DARK = new Color(24, 24, 32);
    private static final Color BG_SIDEBAR = new Color(30, 30, 42);
    private static final Color BG_CARD = new Color(38, 38, 52);
    private static final Color BG_INPUT = new Color(46, 46, 62);
    private static final Color TEXT_PRIMARY = new Color(230, 230, 240);
    private static final Color TEXT_SECONDARY = new Color(150, 150, 170);
    private static final Color ACCENT = new Color(100, 140, 255);
    private static final Color ACCENT_HOVER = new Color(130, 165, 255);
    private static final Color DANGER = new Color(220, 75, 75);
    private static final Color SUCCESS = new Color(75, 200, 120);
    private static final Color TABLE_ROW_ALT = new Color(34, 34, 48);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_NAV = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_INPUT = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_CARD_VALUE = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONT_CARD_LABEL = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_TABLE = new Font("Segoe UI", Font.PLAIN, 13);

    // ==================== DAO OBJECTS ====================

    private BookDAO bookDAO = new BookDAO();
    private PurchaseDAO purchaseDAO = new PurchaseDAO();
    private SaleDAO saleDAO = new SaleDAO();
    private UserDAO userDAO = new UserDAO();

    // ==================== UI COMPONENTS ====================

    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Dashboard labels (updated when dashboard is shown)
    private JLabel lblTotalBooks;
    private JLabel lblTotalStock;
    private JLabel lblTotalPurchases;
    private JLabel lblTotalSales;
    private JLabel lblTotalProfit;

    // Books tab components
    private JTable booksTable;
    private DefaultTableModel booksTableModel;
    private JTextField searchField;

    // Buy Books tab components
    private JComboBox<Book> buyBookCombo;
    private JTextField buyQuantityField;
    private JTextField buyBoughtFromField;
    private JTextField buyPriceField;
    private JLabel buyTotalLabel;
    private JTable purchaseHistoryTable;
    private DefaultTableModel purchaseTableModel;

    // Sell Books tab components
    private JComboBox<Book> sellBookCombo;
    private JTextField sellQuantityField;
    private JTextField sellSoldToField;
    private JTextField sellPriceField;
    private JLabel sellTotalLabel;
    private JLabel sellProfitLabel;
    private JTable salesHistoryTable;
    private DefaultTableModel salesTableModel;

    // Logged-in user
    private User currentUser;

    // ==================== CONSTRUCTORS ====================

    public MainFrame() {
        this(null);
    }

    public MainFrame(User user) {
        this.currentUser = user;
        if (this.currentUser == null) {
            try {
                this.currentUser = userDAO.getUserByUsername("Admin");
            } catch (SQLException ignored) {
            }
        }
        setTitle("Old Book Buy & Sell Register" + (currentUser != null ? " - Logged in as: " + currentUser.getUsername() : ""));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);

        // Main layout: sidebar on the left, content on the right
        setLayout(new BorderLayout());

        // Create sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Create content area with CardLayout for switching panels
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_DARK);

        contentPanel.add(createDashboardPanel(), "Dashboard");
        contentPanel.add(createBooksPanel(), "Books");
        contentPanel.add(createBuyBooksPanel(), "Buy Books");
        contentPanel.add(createSellBooksPanel(), "Sell Books");

        add(contentPanel, BorderLayout.CENTER);

        // Show dashboard first
        refreshDashboard();
        cardLayout.show(contentPanel, "Dashboard");
    }

    // ==================== SIDEBAR ====================

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));

        // App title in sidebar
        JLabel appTitle = new JLabel("  Bookstore");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appTitle.setForeground(ACCENT);
        appTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        appTitle.setBorder(new EmptyBorder(10, 20, 10, 10));
        sidebar.add(appTitle);

        // Logged in user info
        if (currentUser != null) {
            JLabel userBadge = new JLabel("  👤 " + currentUser.getUsername());
            userBadge.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            userBadge.setForeground(TEXT_SECONDARY);
            userBadge.setAlignmentX(Component.LEFT_ALIGNMENT);
            userBadge.setBorder(new EmptyBorder(0, 20, 20, 10));
            sidebar.add(userBadge);
        } else {
            sidebar.add(Box.createVerticalStrut(20));
        }

        // Navigation buttons
        String[] navItems = { "Dashboard", "Books", "Buy Books", "Sell Books", "Change Password", "Logout", "Exit" };

        for (String item : navItems) {
            JButton navBtn = createNavButton(item);
            sidebar.add(navBtn);
            sidebar.add(Box.createVerticalStrut(5));
        }

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton("   " + text);
        button.setFont(FONT_NAV);
        button.setForeground(TEXT_PRIMARY);
        button.setBackground(BG_SIDEBAR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(200, 42));
        button.setPreferredSize(new Dimension(200, 42));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(BG_CARD);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(BG_SIDEBAR);
            }
        });

        // Navigation action
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (text.equals("Exit")) {
                    System.exit(0);
                } else if (text.equals("Change Password")) {
                    showChangePasswordDialog();
                } else if (text.equals("Logout")) {
                    int confirm = JOptionPane.showConfirmDialog(
                            MainFrame.this,
                            "Are you sure you want to log out?",
                            "Confirm Logout",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        dispose();
                        SwingUtilities.invokeLater(() -> {
                            new LoginFrame().setVisible(true);
                        });
                    }
                } else {
                    switchToPanel(text);
                }
            }
        });

        return button;
    }

    /** Switches to the selected panel and refreshes its data. */
    private void switchToPanel(String panelName) {
        if (panelName.equals("Dashboard")) {
            refreshDashboard();
        } else if (panelName.equals("Books")) {
            refreshBooksTable();
        } else if (panelName.equals("Buy Books")) {
            refreshBuyBooksCombo();
            refreshPurchaseHistory();
        } else if (panelName.equals("Sell Books")) {
            refreshSellBooksCombo();
            refreshSalesHistory();
        }
        cardLayout.show(contentPanel, panelName);
    }

    // ==================== DASHBOARD PANEL ====================

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Title
        JLabel title = new JLabel("Dashboard");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 25, 0));
        panel.add(title, BorderLayout.NORTH);

        // Cards grid
        JPanel cardsPanel = new JPanel(new GridLayout(1, 5, 15, 15));
        cardsPanel.setBackground(BG_DARK);

        lblTotalBooks = createDashboardCard(cardsPanel, "Total Books", "0");
        lblTotalStock = createDashboardCard(cardsPanel, "Books in Stock", "0");
        lblTotalPurchases = createDashboardCard(cardsPanel, "Total Purchases", "₹0.00");
        lblTotalSales = createDashboardCard(cardsPanel, "Total Sales", "₹0.00");
        lblTotalProfit = createDashboardCard(cardsPanel, "Total Profit", "₹0.00");

        // Wrap cards at the top of center area
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(BG_DARK);
        centerWrapper.add(cardsPanel, BorderLayout.NORTH);
        panel.add(centerWrapper, BorderLayout.CENTER);

        return panel;
    }

    /** Creates a single dashboard card and returns the value label. */
    private JLabel createDashboardCard(JPanel parent, String label, String initialValue) {
        JPanel card = new JPanel();
        card.setBackground(BG_CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel valueLabel = new JLabel(initialValue);
        valueLabel.setFont(FONT_CARD_VALUE);
        valueLabel.setForeground(ACCENT);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(valueLabel);

        card.add(Box.createVerticalStrut(8));

        JLabel textLabel = new JLabel(label);
        textLabel.setFont(FONT_CARD_LABEL);
        textLabel.setForeground(TEXT_SECONDARY);
        textLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(textLabel);

        parent.add(card);
        return valueLabel;
    }

    private void refreshDashboard() {
        try {
            lblTotalBooks.setText(String.valueOf(bookDAO.getTotalBookCount()));
            lblTotalStock.setText(String.valueOf(bookDAO.getTotalStock()));
            lblTotalPurchases.setText(String.format("₹%.2f", purchaseDAO.getTotalPurchaseAmount()));
            lblTotalSales.setText(String.format("₹%.2f", saleDAO.getTotalSalesAmount()));
            lblTotalProfit.setText(String.format("₹%.2f", saleDAO.getTotalProfit()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==================== BOOKS PANEL ====================

    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Title
        JLabel title = new JLabel("Books");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        // Top bar: search + add button
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        topBar.setBackground(BG_DARK);

        searchField = createStyledTextField(20);
        topBar.add(searchField);

        JButton searchBtn = createStyledButton("Search", ACCENT);
        searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchBooks();
            }
        });
        topBar.add(searchBtn);

        JButton showAllBtn = createStyledButton("Show All", BG_INPUT);
        showAllBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchField.setText("");
                refreshBooksTable();
            }
        });
        topBar.add(showAllBtn);

        JButton addBtn = createStyledButton("Add Book", SUCCESS);
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAddBookDialog();
            }
        });
        topBar.add(addBtn);

        JButton updateBtn = createStyledButton("Update", ACCENT);
        updateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showUpdateBookDialog();
            }
        });
        topBar.add(updateBtn);

        JButton deleteBtn = createStyledButton("Delete", DANGER);
        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelectedBook();
            }
        });
        topBar.add(deleteBtn);

        // Combine title and top bar
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_DARK);
        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(topBar, BorderLayout.SOUTH);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Books table
        String[] columns = { "ID", "Title", "Author", "Category", "ISBN", "Buying Price", "Selling Price", "Stock" };
        booksTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false; // Table is read-only
            }
        };
        booksTable = createStyledTable(booksTableModel);
        JScrollPane scrollPane = new JScrollPane(booksTable);
        styleScrollPane(scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void refreshBooksTable() {
        try {
            List<Book> books = bookDAO.getAllBooks();
            booksTableModel.setRowCount(0);

            for (Book book : books) {
                Object[] row = {
                        book.getBookId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getCategory(),
                        book.getIsbn(),
                        String.format("%.2f", book.getBuyingPrice()),
                        String.format("%.2f", book.getSellingPrice()),
                        book.getStock()
                };
                booksTableModel.addRow(row);
            }
        } catch (SQLException e) {
            showError("Error loading books: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void searchBooks() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            refreshBooksTable();
            return;
        }

        try {
            List<Book> books = bookDAO.searchBooks(searchText);
            booksTableModel.setRowCount(0);

            for (Book book : books) {
                Object[] row = {
                        book.getBookId(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getCategory(),
                        book.getIsbn(),
                        String.format("%.2f", book.getBuyingPrice()),
                        String.format("%.2f", book.getSellingPrice()),
                        book.getStock()
                };
                booksTableModel.addRow(row);
            }
        } catch (SQLException e) {
            showError("Error searching books: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== ADD BOOK DIALOG ====================

    private void showAddBookDialog() {
        JDialog dialog = createStyledDialog("Add New Book", 400, 380);

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));
        form.setBackground(BG_CARD);
        form.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField titleField = createStyledTextField(15);
        JTextField authorField = createStyledTextField(15);
        JTextField categoryField = createStyledTextField(15);
        JTextField isbnField = createStyledTextField(15);
        JTextField buyPriceField = createStyledTextField(15);
        JTextField sellPriceField = createStyledTextField(15);

        form.add(createFormLabel("Title:"));
        form.add(titleField);
        form.add(createFormLabel("Author:"));
        form.add(authorField);
        form.add(createFormLabel("Category:"));
        form.add(categoryField);
        form.add(createFormLabel("ISBN (optional):"));
        form.add(isbnField);
        form.add(createFormLabel("Buying Price:"));
        form.add(buyPriceField);
        form.add(createFormLabel("Selling Price:"));
        form.add(sellPriceField);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(BG_CARD);

        JButton addBtn = createStyledButton("Add Book", SUCCESS);
        JButton cancelBtn = createStyledButton("Cancel", BG_INPUT);

        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Validate input
                String bookTitle = titleField.getText().trim();
                String author = authorField.getText().trim();
                String category = categoryField.getText().trim();
                String isbn = isbnField.getText().trim();

                if (bookTitle.isEmpty() || author.isEmpty() || category.isEmpty()) {
                    showError("Title, Author, and Category are required.");
                    return;
                }

                double buyPrice, sellPrice;
                try {
                    buyPrice = Double.parseDouble(buyPriceField.getText().trim());
                    sellPrice = Double.parseDouble(sellPriceField.getText().trim());
                } catch (NumberFormatException ex) {
                    showError("Please enter valid numeric prices.");
                    return;
                }

                if (buyPrice < 0 || sellPrice < 0) {
                    showError("Prices cannot be negative.");
                    return;
                }

                // Create book object and save to database
                Book book = new Book(bookTitle, author, category, isbn, buyPrice, sellPrice);
                try {
                    bookDAO.addBook(book);
                    showSuccess("Book added successfully!");
                    refreshBooksTable();
                    dialog.dispose();
                } catch (SQLException ex) {
                    showError("Error adding book: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        btnPanel.add(addBtn);
        btnPanel.add(cancelBtn);

        form.add(new JLabel("")); // Empty cell for alignment
        form.add(btnPanel);

        dialog.add(form);
        dialog.setVisible(true);
    }

    // ==================== UPDATE BOOK DIALOG ====================

    private void showUpdateBookDialog() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a book to update.");
            return;
        }

        int bookId = (int) booksTableModel.getValueAt(selectedRow, 0);
        String currentTitle = (String) booksTableModel.getValueAt(selectedRow, 1);
        String currentAuthor = (String) booksTableModel.getValueAt(selectedRow, 2);
        String currentCategory = (String) booksTableModel.getValueAt(selectedRow, 3);
        String currentIsbn = (String) booksTableModel.getValueAt(selectedRow, 4);
        String currentBuyPrice = (String) booksTableModel.getValueAt(selectedRow, 5);
        String currentSellPrice = (String) booksTableModel.getValueAt(selectedRow, 6);

        JDialog dialog = createStyledDialog("Update Book", 400, 380);

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));
        form.setBackground(BG_CARD);
        form.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField titleField = createStyledTextField(15);
        titleField.setText(currentTitle);
        JTextField authorField = createStyledTextField(15);
        authorField.setText(currentAuthor);
        JTextField categoryField = createStyledTextField(15);
        categoryField.setText(currentCategory);
        JTextField isbnField = createStyledTextField(15);
        isbnField.setText(currentIsbn != null ? currentIsbn : "");
        JTextField buyPriceField = createStyledTextField(15);
        buyPriceField.setText(currentBuyPrice);
        JTextField sellPriceField = createStyledTextField(15);
        sellPriceField.setText(currentSellPrice);

        form.add(createFormLabel("Title:"));
        form.add(titleField);
        form.add(createFormLabel("Author:"));
        form.add(authorField);
        form.add(createFormLabel("Category:"));
        form.add(categoryField);
        form.add(createFormLabel("ISBN (optional):"));
        form.add(isbnField);
        form.add(createFormLabel("Buying Price:"));
        form.add(buyPriceField);
        form.add(createFormLabel("Selling Price:"));
        form.add(sellPriceField);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(BG_CARD);

        JButton updateBtn = createStyledButton("Update", ACCENT);
        JButton cancelBtn = createStyledButton("Cancel", BG_INPUT);

        updateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String bookTitle = titleField.getText().trim();
                String author = authorField.getText().trim();
                String category = categoryField.getText().trim();
                String isbn = isbnField.getText().trim();

                if (bookTitle.isEmpty() || author.isEmpty() || category.isEmpty()) {
                    showError("Title, Author, and Category are required.");
                    return;
                }

                double buyPrice, sellPrice;
                try {
                    buyPrice = Double.parseDouble(buyPriceField.getText().trim());
                    sellPrice = Double.parseDouble(sellPriceField.getText().trim());
                } catch (NumberFormatException ex) {
                    showError("Please enter valid numeric prices.");
                    return;
                }

                if (buyPrice < 0 || sellPrice < 0) {
                    showError("Prices cannot be negative.");
                    return;
                }

                Book book = new Book();
                book.setBookId(bookId);
                book.setTitle(bookTitle);
                book.setAuthor(author);
                book.setCategory(category);
                book.setIsbn(isbn);
                book.setBuyingPrice(buyPrice);
                book.setSellingPrice(sellPrice);

                try {
                    bookDAO.updateBook(book);
                    showSuccess("Book updated successfully!");
                    refreshBooksTable();
                    dialog.dispose();
                } catch (SQLException ex) {
                    showError("Error updating book: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        btnPanel.add(updateBtn);
        btnPanel.add(cancelBtn);

        form.add(new JLabel("")); // Empty cell for alignment
        form.add(btnPanel);

        dialog.add(form);
        dialog.setVisible(true);
    }

    // ==================== DELETE BOOK ====================

    private void deleteSelectedBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a book to delete.");
            return;
        }

        int bookId = (int) booksTableModel.getValueAt(selectedRow, 0);
        String bookTitle = (String) booksTableModel.getValueAt(selectedRow, 1);

        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete \"" + bookTitle + "\"?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            // Check for transaction history before deleting
            if (bookDAO.hasTransactionHistory(bookId)) {
                showError("This book cannot be deleted because it has purchase or sales history.");
                return;
            }

            bookDAO.deleteBook(bookId);
            showSuccess("Book deleted successfully!");
            refreshBooksTable();

        } catch (SQLException e) {
            showError("Error deleting book: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== BUY BOOKS PANEL ====================

    private JPanel createBuyBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Title
        JLabel title = new JLabel("Buy Books");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(title, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBackground(BG_CARD);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        buyBookCombo = new JComboBox<>();
        styleComboBox(buyBookCombo);

        // When a book is selected, auto-fill the buying price
        buyBookCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Book selectedBook = (Book) buyBookCombo.getSelectedItem();
                if (selectedBook != null) {
                    buyPriceField.setText(String.format("%.2f", selectedBook.getBuyingPrice()));
                    updateBuyTotal();
                }
            }
        });

        buyQuantityField = createStyledTextField(15);
        buyBoughtFromField = createStyledTextField(15);
        buyPriceField = createStyledTextField(15);
        buyTotalLabel = new JLabel("₹0.00");
        buyTotalLabel.setFont(FONT_LABEL);
        buyTotalLabel.setForeground(ACCENT);

        // Auto-calculate total when quantity or price changes
        addCalculationListener(buyQuantityField);
        addCalculationListener(buyPriceField);

        formPanel.add(createFormLabel("Select Book:"));
        formPanel.add(buyBookCombo);
        formPanel.add(createFormLabel("Quantity:"));
        formPanel.add(buyQuantityField);
        formPanel.add(createFormLabel("Bought From:"));
        formPanel.add(buyBoughtFromField);
        formPanel.add(createFormLabel("Buying Price:"));
        formPanel.add(buyPriceField);
        formPanel.add(createFormLabel("Total Amount:"));
        formPanel.add(buyTotalLabel);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(BG_CARD);
        JButton purchaseBtn = createStyledButton("Purchase Book", SUCCESS);
        purchaseBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processPurchase();
            }
        });
        btnPanel.add(purchaseBtn);
        formPanel.add(new JLabel(""));
        formPanel.add(btnPanel);

        // Purchase history table
        String[] columns = { "ID", "Book", "Qty", "Bought From", "Price", "Total", "Date" };
        purchaseTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        purchaseHistoryTable = createStyledTable(purchaseTableModel);
        JScrollPane scrollPane = new JScrollPane(purchaseHistoryTable);
        styleScrollPane(scrollPane);

        // Split the center: form on top, table below
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(BG_DARK);
        centerPanel.add(formPanel, BorderLayout.NORTH);

        JLabel historyTitle = new JLabel("Purchase History");
        historyTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        historyTitle.setForeground(TEXT_PRIMARY);
        historyTitle.setBorder(new EmptyBorder(10, 0, 5, 0));

        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(BG_DARK);
        historyPanel.add(historyTitle, BorderLayout.NORTH);
        historyPanel.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(historyPanel, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    /** Adds a key listener to update buy total when text changes. */
    private void addCalculationListener(JTextField field) {
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                updateBuyTotal();
                updateSellTotal();
            }
        });
    }

    private void updateBuyTotal() {
        try {
            double price = Double.parseDouble(buyPriceField.getText().trim());
            int qty = Integer.parseInt(buyQuantityField.getText().trim());
            double total = price * qty;
            buyTotalLabel.setText(String.format("₹%.2f", total));
        } catch (NumberFormatException e) {
            buyTotalLabel.setText("₹0.00");
        }
    }

    // ==================== PURCHASE OPERATION ====================

    private void processPurchase() {
        Book selectedBook = (Book) buyBookCombo.getSelectedItem();
        if (selectedBook == null) {
            showError("Please select a book.");
            return;
        }

        String boughtFrom = buyBoughtFromField.getText().trim();
        if (boughtFrom.isEmpty()) {
            showError("'Bought From' cannot be empty.");
            return;
        }

        int quantity;
        double buyingPrice;
        try {
            quantity = Integer.parseInt(buyQuantityField.getText().trim());
            buyingPrice = Double.parseDouble(buyPriceField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Please enter valid numeric values for quantity and price.");
            return;
        }

        if (quantity <= 0) {
            showError("Quantity must be a positive number.");
            return;
        }
        if (buyingPrice < 0) {
            showError("Buying price cannot be negative.");
            return;
        }

        double totalAmount = buyingPrice * quantity;
        String purchaseDate = LocalDate.now().toString(); // YYYY-MM-DD format

        Purchase purchase = new Purchase(selectedBook.getBookId(), quantity, boughtFrom,
                buyingPrice, totalAmount, purchaseDate);
        try {
            purchaseDAO.addPurchase(purchase);
            showSuccess("Purchase recorded! Stock updated.");

            // Clear form fields
            buyQuantityField.setText("");
            buyBoughtFromField.setText("");
            buyTotalLabel.setText("₹0.00");

            // Refresh displays
            refreshBuyBooksCombo();
            refreshPurchaseHistory();
            refreshDashboard();

        } catch (SQLException e) {
            showError("Error recording purchase: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshBuyBooksCombo() {
        try {
            List<Book> books = bookDAO.getAllBooks();
            buyBookCombo.removeAllItems();
            for (Book book : books) {
                buyBookCombo.addItem(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshPurchaseHistory() {
        try {
            List<Object[]> history = purchaseDAO.getPurchaseHistory();
            purchaseTableModel.setRowCount(0);
            for (Object[] row : history) {
                // Format the price and total columns
                row[4] = String.format("%.2f", (double) row[4]);
                row[5] = String.format("%.2f", (double) row[5]);
                purchaseTableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==================== SELL BOOKS PANEL ====================

    private JPanel createSellBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Title
        JLabel title = new JLabel("Sell Books");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(title, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBackground(BG_CARD);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        sellBookCombo = new JComboBox<>();
        styleComboBox(sellBookCombo);

        // When a book is selected, auto-fill the selling price
        sellBookCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Book selectedBook = (Book) sellBookCombo.getSelectedItem();
                if (selectedBook != null) {
                    sellPriceField.setText(String.format("%.2f", selectedBook.getSellingPrice()));
                    updateSellTotal();
                }
            }
        });

        sellQuantityField = createStyledTextField(15);
        sellSoldToField = createStyledTextField(15);
        sellPriceField = createStyledTextField(15);

        sellTotalLabel = new JLabel("₹0.00");
        sellTotalLabel.setFont(FONT_LABEL);
        sellTotalLabel.setForeground(ACCENT);

        sellProfitLabel = new JLabel("₹0.00");
        sellProfitLabel.setFont(FONT_LABEL);
        sellProfitLabel.setForeground(SUCCESS);

        // Auto-calculate total and profit when quantity or price changes
        sellQuantityField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                updateSellTotal();
            }
        });
        sellPriceField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                updateSellTotal();
            }
        });

        formPanel.add(createFormLabel("Select Book:"));
        formPanel.add(sellBookCombo);
        formPanel.add(createFormLabel("Quantity:"));
        formPanel.add(sellQuantityField);
        formPanel.add(createFormLabel("Sold To:"));
        formPanel.add(sellSoldToField);
        formPanel.add(createFormLabel("Selling Price:"));
        formPanel.add(sellPriceField);
        formPanel.add(createFormLabel("Total Amount:"));
        formPanel.add(sellTotalLabel);
        formPanel.add(createFormLabel("Profit:"));
        formPanel.add(sellProfitLabel);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(BG_CARD);
        JButton sellBtn = createStyledButton("Sell Book", SUCCESS);
        sellBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processSale();
            }
        });
        btnPanel.add(sellBtn);
        formPanel.add(new JLabel(""));
        formPanel.add(btnPanel);

        // Sales history table
        String[] columns = { "ID", "Book", "Qty", "Sold To", "Price", "Total", "Profit", "Date" };
        salesTableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        salesHistoryTable = createStyledTable(salesTableModel);
        JScrollPane scrollPane = new JScrollPane(salesHistoryTable);
        styleScrollPane(scrollPane);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(BG_DARK);
        centerPanel.add(formPanel, BorderLayout.NORTH);

        JLabel historyTitle = new JLabel("Sales History");
        historyTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        historyTitle.setForeground(TEXT_PRIMARY);
        historyTitle.setBorder(new EmptyBorder(10, 0, 5, 0));

        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(BG_DARK);
        historyPanel.add(historyTitle, BorderLayout.NORTH);
        historyPanel.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(historyPanel, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private void updateSellTotal() {
        try {
            Book selectedBook = (Book) sellBookCombo.getSelectedItem();
            if (selectedBook == null)
                return;

            double price = Double.parseDouble(sellPriceField.getText().trim());
            int qty = Integer.parseInt(sellQuantityField.getText().trim());
            double total = price * qty;
            double profit = (price - selectedBook.getBuyingPrice()) * qty;

            sellTotalLabel.setText(String.format("₹%.2f", total));
            sellProfitLabel.setText(String.format("₹%.2f", profit));
        } catch (NumberFormatException e) {
            sellTotalLabel.setText("₹0.00");
            sellProfitLabel.setText("₹0.00");
        }
    }

    // ==================== SALE OPERATION ====================

    private void processSale() {
        Book selectedBook = (Book) sellBookCombo.getSelectedItem();
        if (selectedBook == null) {
            showError("Please select a book.");
            return;
        }

        String soldTo = sellSoldToField.getText().trim();
        if (soldTo.isEmpty()) {
            showError("'Sold To' cannot be empty.");
            return;
        }

        int quantity;
        double sellingPrice;
        try {
            quantity = Integer.parseInt(sellQuantityField.getText().trim());
            sellingPrice = Double.parseDouble(sellPriceField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Please enter valid numeric values for quantity and price.");
            return;
        }

        if (quantity <= 0) {
            showError("Quantity must be a positive number.");
            return;
        }
        if (sellingPrice < 0) {
            showError("Selling price cannot be negative.");
            return;
        }

        // Check stock before selling
        try {
            int currentStock = saleDAO.getBookStock(selectedBook.getBookId());
            if (quantity > currentStock) {
                showError("Insufficient stock. Available quantity: " + currentStock);
                return;
            }
        } catch (SQLException e) {
            showError("Error checking stock: " + e.getMessage());
            return;
        }

        double totalAmount = sellingPrice * quantity;
        double profit = (sellingPrice - selectedBook.getBuyingPrice()) * quantity;
        String saleDate = LocalDate.now().toString(); // YYYY-MM-DD format

        Sale sale = new Sale(selectedBook.getBookId(), quantity, soldTo,
                sellingPrice, totalAmount, profit, saleDate);
        try {
            saleDAO.addSale(sale);
            showSuccess("Sale recorded! Stock updated.");

            // Clear form fields
            sellQuantityField.setText("");
            sellSoldToField.setText("");
            sellTotalLabel.setText("₹0.00");
            sellProfitLabel.setText("₹0.00");

            // Refresh displays
            refreshSellBooksCombo();
            refreshSalesHistory();
            refreshDashboard();

        } catch (SQLException e) {
            showError("Error recording sale: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshSellBooksCombo() {
        try {
            List<Book> books = bookDAO.getAllBooks();
            sellBookCombo.removeAllItems();
            for (Book book : books) {
                sellBookCombo.addItem(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshSalesHistory() {
        try {
            List<Object[]> history = saleDAO.getSaleHistory();
            salesTableModel.setRowCount(0);
            for (Object[] row : history) {
                // Format the price, total, and profit columns
                row[4] = String.format("%.2f", (double) row[4]);
                row[5] = String.format("%.2f", (double) row[5]);
                row[6] = String.format("%.2f", (double) row[6]);
                salesTableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==================== CHANGE PASSWORD DIALOG ====================

    private void showChangePasswordDialog() {
        if (currentUser == null) {
            try {
                currentUser = userDAO.getUserByUsername("Admin");
            } catch (SQLException ignored) {
            }
        }

        String username = (currentUser != null) ? currentUser.getUsername() : "Admin";
        JDialog dialog = createStyledDialog("Change Password - " + username, 430, 420);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(BG_CARD);
        form.setBorder(new EmptyBorder(22, 25, 22, 25));

        // Header info
        JLabel lblHeader = new JLabel("Change Password for: " + username);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHeader.setForeground(ACCENT);
        lblHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblHeader);
        form.add(Box.createVerticalStrut(15));

        // Current Password
        JLabel lblCurrent = createFormLabel("Current Password:");
        lblCurrent.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPasswordField txtCurrent = createStyledPasswordField();
        form.add(lblCurrent);
        form.add(Box.createVerticalStrut(4));
        form.add(txtCurrent);
        form.add(Box.createVerticalStrut(10));

        // New Password
        JLabel lblNew = createFormLabel("New Password:");
        lblNew.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPasswordField txtNew = createStyledPasswordField();
        form.add(lblNew);
        form.add(Box.createVerticalStrut(4));
        form.add(txtNew);
        form.add(Box.createVerticalStrut(10));

        // Confirm New Password
        JLabel lblConfirm = createFormLabel("Confirm New Password:");
        lblConfirm.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPasswordField txtConfirm = createStyledPasswordField();
        form.add(lblConfirm);
        form.add(Box.createVerticalStrut(4));
        form.add(txtConfirm);
        form.add(Box.createVerticalStrut(8));

        // Show/hide password checkbox
        JCheckBox chkShow = new JCheckBox("Show Passwords");
        chkShow.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkShow.setForeground(TEXT_SECONDARY);
        chkShow.setBackground(BG_CARD);
        chkShow.setFocusPainted(false);
        chkShow.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkShow.addActionListener(e -> {
            char echo = chkShow.isSelected() ? (char) 0 : '•';
            txtCurrent.setEchoChar(echo);
            txtNew.setEchoChar(echo);
            txtConfirm.setEchoChar(echo);
        });
        form.add(chkShow);
        form.add(Box.createVerticalStrut(8));

        // Status message
        JLabel lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(DANGER);
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblStatus);
        form.add(Box.createVerticalStrut(15));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(BG_CARD);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton saveBtn = createStyledButton("Update Password", ACCENT);
        JButton cancelBtn = createStyledButton("Cancel", BG_INPUT);

        saveBtn.addActionListener(e -> {
            String currentPass = new String(txtCurrent.getPassword());
            String newPass = new String(txtNew.getPassword());
            String confirmPass = new String(txtConfirm.getPassword());

            if (currentPass.isEmpty()) {
                lblStatus.setText("Please enter your current password.");
                txtCurrent.requestFocusInWindow();
                return;
            }
            if (newPass.isEmpty()) {
                lblStatus.setText("Please enter a new password.");
                txtNew.requestFocusInWindow();
                return;
            }
            if (newPass.length() < 4) {
                lblStatus.setText("New password must be at least 4 characters.");
                txtNew.requestFocusInWindow();
                return;
            }
            if (!newPass.equals(confirmPass)) {
                lblStatus.setText("New passwords do not match.");
                txtConfirm.setText("");
                txtConfirm.requestFocusInWindow();
                return;
            }

            try {
                User verified = userDAO.authenticate(username, currentPass);
                if (verified == null) {
                    lblStatus.setText("Current password is incorrect.");
                    txtCurrent.setText("");
                    txtCurrent.requestFocusInWindow();
                    return;
                }

                boolean updated = userDAO.updatePassword(username, newPass);
                if (updated) {
                    currentUser = userDAO.getUserByUsername(username);
                    dialog.dispose();
                    showSuccess("Password updated successfully!\nPlease use your new password for future logins.");
                } else {
                    lblStatus.setText("Failed to update password. Please try again.");
                }
            } catch (SQLException ex) {
                lblStatus.setText("Error updating password: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        form.add(btnPanel);

        dialog.add(form, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    // ==================== UI HELPER METHODS ====================

    /** Creates a styled password field with dark theme. */
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(FONT_INPUT);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(BG_INPUT);
        field.setCaretColor(TEXT_PRIMARY);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        field.setPreferredSize(new Dimension(360, 36));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 80), 1),
                new EmptyBorder(6, 10, 6, 10)));
        return field;
    }

    /** Creates a styled text field with dark theme. */
    private JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(FONT_INPUT);
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(BG_INPUT);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 80), 1),
                new EmptyBorder(6, 10, 6, 10)));
        return field;
    }

    /** Creates a styled button with the given background color. */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(FONT_BUTTON);
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(130, 34));

        // Hover effect: slightly brighter on hover
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

    /** Creates a styled form label. */
    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    /** Creates a styled JTable with dark theme and alternating row colors. */
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(FONT_TABLE);
        table.setForeground(TEXT_PRIMARY);
        table.setBackground(BG_DARK);
        table.setSelectionBackground(ACCENT);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(50, 50, 68));
        table.setRowHeight(32);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        // Style the table header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(BG_CARD);
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 68)));
        header.setReorderingAllowed(false);

        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? BG_DARK : TABLE_ROW_ALT);
                    c.setForeground(TEXT_PRIMARY);
                }
                return c;
            }
        });

        return table;
    }

    /** Styles a scroll pane with dark theme. */
    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBackground(BG_DARK);
        scrollPane.getViewport().setBackground(BG_DARK);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 68)));
    }

    /** Styles a combo box with dark theme. */
    private void styleComboBox(JComboBox<?> combo) {
        combo.setFont(FONT_INPUT);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBackground(BG_INPUT);
    }

    /** Creates a styled dialog window. */
    private JDialog createStyledDialog(String title, int width, int height) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(width, height);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BG_CARD);
        return dialog;
    }

    /** Shows a success message dialog. */
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Shows an error message dialog. */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
