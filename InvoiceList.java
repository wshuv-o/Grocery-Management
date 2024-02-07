import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InvoiceList extends JPanel {
    private JList<File> fileList;
    private DefaultListModel<File> listModel;

    public InvoiceList(File folder) {
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileList.setCellRenderer(new FileListCellRenderer());
        fileList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    File selectedFile = fileList.getSelectedValue();
                    if (selectedFile != null && selectedFile.isFile()) {
                        openTextFile(selectedFile);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(fileList);
        add(scrollPane, BorderLayout.CENTER);

        loadFiles(folder);
    }

    private void loadFiles(File folder) {
        listModel.clear();
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    listModel.addElement(file);
                }
            }
        }
    }

    private void openTextFile(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error opening the file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class FileListCellRenderer extends DefaultListCellRenderer {
        private SimpleDateFormat dateFormat;

        public FileListCellRenderer() {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            File file = (File) value;
            label.setText(file.getName());
            label.setToolTipText(file.getAbsolutePath());

            if (file.isFile()) {
                Date creationDate = new Date(file.lastModified());
                label.setText("<html><b>" + file.getName() + "</b>"+"      &nbsp; &nbsp;&nbsp;&nbsp;&nbsp;         " + dateFormat.format(creationDate) + "</html>");
            }

            return label;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("File Manager");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);

            InvoiceList fileListPanel = new InvoiceList(new File("invoices"));
            frame.add(fileListPanel);

            frame.setVisible(true);
        });
    }
}