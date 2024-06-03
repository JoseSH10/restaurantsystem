
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class GestionarP extends JFrame {

    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JButton btnAgregar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JTable tablaProductos;
    private DefaultTableModel modelo;
    private int productoSeleccionado = -1;

    public GestionarP() {
        setTitle("Gestionar Productos");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(20, 20, 100, 25);
        add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(130, 20, 200, 25);
        add(txtNombre);

        JLabel lblPrecio = new JLabel("Precio:");
        lblPrecio.setBounds(20, 60, 100, 25);
        add(lblPrecio);

        txtPrecio = new JTextField();
        txtPrecio.setBounds(130, 60, 200, 25);
        add(txtPrecio);

        btnAgregar = new JButton("Agregar");
        btnAgregar.setBounds(20, 100, 100, 25);
        add(btnAgregar);

        btnActualizar = new JButton("Actualizar");
        btnActualizar.setBounds(130, 100, 100, 25);
        add(btnActualizar);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(240, 100, 100, 25);
        add(btnEliminar);

        // Crear la tabla de productos
        modelo = new DefaultTableModel(new String[]{"ID", "Nombre", "Precio"}, 0);
        tablaProductos = new JTable(modelo);
        tablaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProductos.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && tablaProductos.getSelectedRow() != -1) {
                productoSeleccionado = tablaProductos.getSelectedRow();
                txtNombre.setText(modelo.getValueAt(productoSeleccionado, 1).toString());
                txtPrecio.setText(modelo.getValueAt(productoSeleccionado, 2).toString());
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        scrollPane.setBounds(20, 140, 550, 200);
        add(scrollPane);

        // Añadir ActionListeners a los botones
        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarProducto();
            }
        });

        btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarProducto();
            }
        });

        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarProducto();
            }
        });

        // Cargar productos en la tabla
        cargarProductos();
    }

    private void agregarProducto() {
        String nombre = txtNombre.getText();
        String precio = txtPrecio.getText();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/restaurante", "root", "");
             PreparedStatement pst = conn.prepareStatement("INSERT INTO productos (nombre, precio) VALUES (?, ?)")) {
            pst.setString(1, nombre);
            pst.setString(2, precio);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Producto agregado");
            cargarProductos();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void actualizarProducto() {
        if (productoSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para actualizar");
            return;
        }

        int id = (int) modelo.getValueAt(productoSeleccionado, 0);
        String nombre = txtNombre.getText();
        String precio = txtPrecio.getText();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/restaurante", "root", "");
             PreparedStatement pst = conn.prepareStatement("UPDATE productos SET nombre = ?, precio = ? WHERE id = ?")) {
            pst.setString(1, nombre);
            pst.setString(2, precio);
            pst.setInt(3, id);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Producto actualizado");
            cargarProductos();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void eliminarProducto() {
        if (productoSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar");
            return;
        }

        int id = (int) modelo.getValueAt(productoSeleccionado, 0);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/restaurante", "root", "");
             PreparedStatement pst = conn.prepareStatement("DELETE FROM productos WHERE id = ?")) {
            pst.setInt(1, id);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Producto eliminado");
            cargarProductos();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void cargarProductos() {
        modelo.setRowCount(0);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/restaurante", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM productos")) {

            while (rs.next()) {
                modelo.addRow(new Object[]{rs.getInt("id"), rs.getString("nombre"), rs.getDouble("precio")});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
