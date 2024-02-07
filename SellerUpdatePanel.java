import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SellerUpdatePanel extends JPanel {

    private JTable table;
    private List<String[]> sellers;

    public SellerUpdatePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240)); // Light gray background

        // Create the table model and table
        DefaultTableModel tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        table.setBackground(new Color(255, 255, 255)); // White background

        // Add columns to the table
        tableModel.addColumn("Name");
        tableModel.addColumn("User ID");
        tableModel.addColumn("Password");
        tableModel.addColumn("Birthdate");
        tableModel.addColumn("Address");
        tableModel.addColumn("Phone");
        tableModel.addColumn("Email");

        // Read seller data from file and populate the table
        sellers = readsellersFromFile();
        for (String[] seller : sellers) {
            tableModel.addRow(seller);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove border
        add(scrollPane, BorderLayout.CENTER);

        JButton updateButton = new JButton("Update Selected Seller");
        updateButton.setBackground(new Color(60, 179, 113)); // Medium Sea Green color
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String[] selectedSeller = sellers.get(selectedRow);
                    String modifiedName = JOptionPane.showInputDialog("Enter modified name:", selectedSeller[0]);
                    String modifiedUserId = JOptionPane.showInputDialog("Enter modified user ID:", selectedSeller[1]);
                    String modifiedPassword = JOptionPane.showInputDialog("Enter modified password:", selectedSeller[2]);
                    String modifiedBirthdate = JOptionPane.showInputDialog("Enter modified birthdate:", selectedSeller[3]);
                    String modifiedAddress = JOptionPane.showInputDialog("Enter modified address:", selectedSeller[4]);
                    String modifiedPhone = JOptionPane.showInputDialog("Enter modified phone:", selectedSeller[5]);
                    String modifiedEmail = JOptionPane.showInputDialog("Enter modified email:", selectedSeller[6]);

                    selectedSeller[0] = modifiedName;
                    selectedSeller[1] = modifiedUserId;
                    selectedSeller[2] = modifiedPassword;
                    selectedSeller[3] = modifiedBirthdate;
                    selectedSeller[4] = modifiedAddress;
                    selectedSeller[5] = modifiedPhone;
                    selectedSeller[6] = modifiedEmail;

                    updateTableData(tableModel);
                    writesellersToFile();
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(240, 240, 240)); // Light gray background
        buttonPanel.add(updateButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private List<String[]> readsellersFromFile() {
        List<String[]> sellers = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("sellers.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] sellerInfo = line.split(",");
                sellers.add(sellerInfo);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sellers;
    }

    private void updateTableData(DefaultTableModel tableModel) {
        tableModel.setRowCount(0); // Clear the table
        for (String[] seller : sellers) {
            tableModel.addRow(seller);
        }
    }

    private void writesellersToFile() {
        try {
            FileWriter writer = new FileWriter("sellers.txt");
            for (String[] seller : sellers) {
                writer.write(String.join(",", seller) + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Seller Update Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new SellerUpdatePanel());
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);
    }
}
