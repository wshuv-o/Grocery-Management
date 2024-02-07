import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SellProduct extends JPanel {

    private DefaultTableModel defaultTableModel1;
    private DefaultTableModel defaultTableModel2;
    private Map<String, Integer> productQuantities = new HashMap<>();
    private Product[] products=new Product[100];
    int ii=0;
    JTextField quantityField = new JTextField(5);
        JLabel nameLabel = new JLabel("Customer Name:");
        JTextField nameField = new JTextField();

        JLabel addressLabel = new JLabel("Customer Address:");
        JTextField addressField = new JTextField();

        JLabel phoneLabel = new JLabel("Customer Phone:");
        JTextField phoneField = new JTextField();

    public SellProduct() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel customerPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        JPanel productPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        //productPanel.setBackground(Color.GREEN);
        JPanel cartPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        //cartPanel.setBackground(Color.RED);
        JPanel checkoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        checkoutPanel.setBackground(Color.BLUE);


        JLabel searchLabel = new JLabel("Search Product:");
        JTextField searchField = new JTextField(15);

        JPanel searchBoxPanel = new JPanel(new BorderLayout());
        JTextField productSearchField = new JTextField(15);
        JButton productSearchButton = new JButton("Search");
        searchBoxPanel.add(productSearchField, BorderLayout.CENTER);
        searchBoxPanel.add(productSearchButton, BorderLayout.EAST);

        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField(5);
        quantityField.setText("0");
        JButton minusButton = new JButton("-");
        JButton plusButton = new JButton("+");

        defaultTableModel1 = new DefaultTableModel(new String[]{"Name", "Price", "Quantity", "Category", "Id"}, 0);
        JTable defaultTableView1 = new JTable(defaultTableModel1);
        JScrollPane tableScrollPane1 = new JScrollPane(defaultTableView1);

        defaultTableModel2 = new DefaultTableModel(new String[]{"Name", "Price", "Quantity", "Category", "Id"}, 0);
        JTable defaultTableView2 = new JTable(defaultTableModel2);
        JScrollPane tableScrollPane2 = new JScrollPane(defaultTableView2);

        JButton addToCartButton = new JButton("Add to Cart");

        JButton checkoutButton = new JButton("Checkout");

        customerPanel.add(nameLabel);
        customerPanel.add(nameField);
        customerPanel.add(addressLabel);
        customerPanel.add(addressField);
        customerPanel.add(phoneLabel);
        customerPanel.add(phoneField);

        
        productPanel.add(searchLabel);
        productPanel.add(searchBoxPanel);

        cartPanel.add(tableScrollPane1);

        JPanel quantityControlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        quantityControlPanel.add(minusButton);
        quantityControlPanel.add(quantityField);
        quantityControlPanel.add(plusButton);

        cartPanel.add(tableScrollPane2);
        productPanel.add(quantityControlPanel);
        productPanel.add(addToCartButton);

        checkoutPanel.add(checkoutButton);

        mainPanel.add(customerPanel, BorderLayout.NORTH);
        mainPanel.add(productPanel, BorderLayout.CENTER);
        mainPanel.add(cartPanel, BorderLayout.WEST);
        mainPanel.add(checkoutPanel, BorderLayout.SOUTH);

        add(mainPanel);

        productSearchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = productSearchField.getText();
                searchAndDisplayProduct(searchTerm);
            }
        });

        minusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int quantity = Integer.parseInt(quantityField.getText());
                if (quantity > 0) {
                    quantity--;
                    quantityField.setText(Integer.toString(quantity));
                }
            }
        });

        plusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int quantity = Integer.parseInt(quantityField.getText());
                quantity++;
                quantityField.setText(Integer.toString(quantity));
            }
        });

        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = defaultTableView1.getSelectedRow();
                if (selectedRow >= 0) {
                    String productName = (String) defaultTableModel1.getValueAt(selectedRow, 0);
                    int quantity = Integer.parseInt(quantityField.getText());
                    //reduceProductQuantityInFile(productName, quantity );

                    Object[] rowData = new Object[defaultTableModel1.getColumnCount()];
                    for (int col = 0; col < defaultTableModel1.getColumnCount(); col++) {
                        rowData[col] = defaultTableModel1.getValueAt(selectedRow, col);
                    }
                    rowData[2]=quantityField.getText();

                    defaultTableModel2.addRow(rowData);

                    productQuantities.put(productName, productQuantities.getOrDefault(productName, 0) + quantity);

                    JOptionPane.showMessageDialog(null, "Product added to cart.", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // Checkout button listener
        checkoutButton.addActionListener(new ActionListener() {
            @Override
            
            public void actionPerformed(ActionEvent e) {
                String s="";
                for (String productName : productQuantities.keySet()) {
                    int quantityToReduce = productQuantities.get(productName);
                    reduceProductQuantityInFile(productName, quantityToReduce);
                    s=formatMatchingProductData(defaultTableModel2);
                }
                //JOptionPane.showMessageDialog(null, s, "Checkout", JOptionPane.INFORMATION_MESSAGE);
                switchToBodyPanel(new OrderDetails(s));
                createInvoiceFileWithContent(s);

            }
        });
        readProductsFromFile(defaultTableModel1);
    }
    public static String cleanUpString(String input) {
        String cleanedString = input.replaceAll("(Name:.*?|Address:.*?|Phone:.*?|Total cost:.*?\\d+\\.\\d+).*", "");

        cleanedString = cleanedString.trim();

        return cleanedString;
    }
    private void reduceProductQuantityInFile(String productName, int quantityToReduce) {
        try (BufferedReader reader = new BufferedReader(new FileReader("products.txt"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("temp.txt"))) {
    
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[0].equals(productName)) {
                    int currentQuantity = Integer.parseInt(parts[2]);
                    int updatedQuantity = (currentQuantity - quantityToReduce);
                    if (updatedQuantity < 0) {
                        updatedQuantity = 0; // Ensure quantity doesn't go negative
                                                            JOptionPane.showMessageDialog(this, updatedQuantity, "Success", JOptionPane.INFORMATION_MESSAGE);

                    }
                    line = parts[0] + "," + parts[1] + "," + updatedQuantity+","+parts[3] + ","+parts[4];
                                    JOptionPane.showMessageDialog(this, updatedQuantity, "Success", JOptionPane.INFORMATION_MESSAGE);

                }
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    replaceOriginalFileWithTempFile();
    
    }
    private void replaceOriginalFileWithTempFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("temp.txt"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("products.txt"))) {
    
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        // Delete the temporary file after replacing the original file
        File tempFile = new File("temp.txt");
        //tempFile.delete();
    }
    
     private void searchAndDisplayProduct(String searchTerm) {
        for (int row = 0; row < defaultTableModel1.getRowCount(); row++) {
            String productName = (String) defaultTableModel1.getValueAt(row, 0);
            if (productName.equalsIgnoreCase(searchTerm)) {
                Object[] rowData = new Object[defaultTableModel1.getColumnCount()];
                for (int col = 0; col < defaultTableModel1.getColumnCount(); col++) {
                    rowData[col] = defaultTableModel1.getValueAt(row, col);
                }

                rowData[2]=1;
                JOptionPane.showMessageDialog(this, rowData[2], quantityField.getText(), JOptionPane.INFORMATION_MESSAGE);

                defaultTableModel2.addRow(rowData);
                //JOptionPane.showMessageDialog(this, "Product found and added to cart!", "Product Found", JOptionPane.INFORMATION_MESSAGE);
                JOptionPane.showMessageDialog(this, "Product found and added to the cart", "Success", JOptionPane.INFORMATION_MESSAGE);

                return; 
            }
        }
        JOptionPane.showMessageDialog(this, "Product not found.", "Product Not Found", JOptionPane.WARNING_MESSAGE);
    }

    private void readProductsFromFile(DefaultTableModel tableModel) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("products.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] productInfo = line.split(",");
                if (productInfo.length >= 5) {
                    tableModel.addRow(productInfo);
                    // products[ii]=new Product(Integer.parseInt(productInfo[4]), productInfo[0], Double.parseDouble(productInfo[1]),  Integer.parseInt(productInfo[2]), productInfo[3]);
                    // ii++;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
  
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Advertisement Panel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.add(new SellProduct());
            frame.setVisible(true);
    });
}

public String formatMatchingProductData(DefaultTableModel defaultTableModel) {
    StringBuilder formattedData = new StringBuilder();

        String name = nameField.getText();
    String address = addressField.getText();
    formattedData.append("Name:").append(name).append("<br>");
    formattedData.append("Address: ").append(address).append("<br>");
    String phone = addressField.getText();
    formattedData.append("Phone: ").append(phone).append("<br>");
    // Append header
    formattedData.append("name &nbsp; price &nbsp; quantity  &nbsp;  cost<br>");
    formattedData.append("_____________________________________________<br>");
    double price=0;
    double cost=0;

    int qty=0;
    for (int row = 0; row < defaultTableModel.getRowCount(); row++) {
        String productName = (String) defaultTableModel.getValueAt(row, 0);
            for (int col = 0; col < 4; col++) {
                if(col!=3){

                formattedData.append(defaultTableModel.getValueAt(row, col)).append("     &nbsp;");
                if(col==1){
                    Object value = defaultTableModel.getValueAt(row, col);
                    String valueAsString = value.toString();
                    price=Double.parseDouble(valueAsString);
                }
                if(col==2){
                    Object value = defaultTableModel.getValueAt(row, col);
                    String valueAsString = value.toString();
                    qty=Integer.parseInt(valueAsString);

                }
            }
            }
            formattedData.append(price*qty+"<br>");
            formattedData.append("<br>");
            cost=cost+price*qty;
    }
    formattedData.append("<br>"+"Total cost: "+"<br>"+cost+"<br>");

    return formattedData.toString();
}

private void switchToBodyPanel(JPanel panel) {
    removeAll();
    add(panel, BorderLayout.CENTER);
    revalidate();
    repaint();
}
public static void createInvoiceFileWithContent(String content) {
    File folder = new File("invoices"); // Specify the folder path
    String[] invoiceFiles = folder.list((dir, name) -> name.matches("\\d+\\.txt"));

    if (invoiceFiles != null && invoiceFiles.length > 0) {
        int largestInvoiceNumber = Arrays.stream(invoiceFiles)
                .mapToInt(name -> Integer.parseInt(name.substring(0, name.lastIndexOf("."))))
                .max()
                .orElse(0);

        int newInvoiceNumber = largestInvoiceNumber + 1;

        String xxxxx = folder.getName() + "/" + newInvoiceNumber;

        // Create and write to the new txt file
        try {
            File newFile = new File(xxxxx + ".txt");
            FileWriter writer = new FileWriter(newFile);
            writer.write(content);
            writer.close();
            System.out.println("New file created: " + newFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    } else {
        System.out.println("No invoice files found in the folder.");
    }
}
}