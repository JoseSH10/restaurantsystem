import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class VentasF extends JFrame {
    private JTextField txtTotal;
    private JButton btnAgregarVenta;

    public VentasF() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Agregar Venta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        txtTotal = new JTextField(20);
        btnAgregarVenta = new JButton("Agregar Venta");

        btnAgregarVenta.addActionListener(evt -> agregarVenta());

        JPanel panel = new JPanel();
        panel.add(new JLabel("Total:"));
        panel.add(txtTotal);
        panel.add(btnAgregarVenta);

        add(panel);
        pack();
        setLocationRelativeTo(null);
    }

    private void agregarVenta() {
        double total;

        try {
            total = Double.parseDouble(txtTotal.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El total debe ser un número.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/restaurante", "root", "");
             PreparedStatement pst = conn.prepareStatement("INSERT INTO ventas (total) VALUES (?)")) {
            pst.setDouble(1, total);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Venta agregada");
            txtTotal.setText("");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error agregando venta: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new VentasF().setVisible(true));
    }
}
