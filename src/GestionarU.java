import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class GestionarU extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbxRol;
    private int usuarioSeleccionado = -1;

    public GestionarU() {
        initComponents();
        cargarUsuarios();
    }

    private void initComponents() {
        JScrollPane scrollPane = new JScrollPane();
        table = new JTable();
        tableModel = new DefaultTableModel(
            new Object [][] {},
            new String [] {"ID", "Username", "Rol"}
        ) {
            Class[] types = new Class[] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        table.setModel(tableModel);
        scrollPane.setViewportView(table);

        JLabel lblUsername = new JLabel("Username:");
        txtUsername = new JTextField();
        JLabel lblPassword = new JLabel("Contraseña:");
        txtPassword = new JPasswordField();
        JLabel lblRol = new JLabel("Rol:");
        cbxRol = new JComboBox<>(new String[] { "admin", "usuario" });

        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                agregarUsuario();
            }
        });

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                actualizarUsuario();
            }
        });

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                eliminarUsuario();
            }
        });

        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                usuarioSeleccionado = table.getSelectedRow();
                txtUsername.setText(tableModel.getValueAt(usuarioSeleccionado, 1).toString());
                cbxRol.setSelectedItem(tableModel.getValueAt(usuarioSeleccionado, 2).toString());
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(lblUsername)
                            .addComponent(lblPassword)
                            .addComponent(lblRol))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtUsername)
                            .addComponent(txtPassword)
                            .addComponent(cbxRol, 0, 150, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnAgregar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnActualizar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnEliminar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUsername)
                    .addComponent(txtUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAgregar))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPassword)
                    .addComponent(txtPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnActualizar))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEliminar))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRol)
                    .addComponent(cbxRol, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }

    private void cargarUsuarios() {
        tableModel.setRowCount(0);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/restaurante", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM usuarios")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("username"),
                    rs.getString("rol")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error cargando usuarios: " + ex.getMessage());
        }
    }

    private void agregarUsuario() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        String rol = cbxRol.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario y la contraseña no pueden estar vacíos.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/restaurante", "root", "");
             PreparedStatement pst = conn.prepareStatement("INSERT INTO usuarios (username, password, rol) VALUES (?, ?, ?)")) {
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, rol);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Usuario agregado");
            cargarUsuarios();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error agregando usuario: " + ex.getMessage());
        }
    }

    private void actualizarUsuario() {
        if (usuarioSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para actualizar");
            return;
        }

        int id = (int) tableModel.getValueAt(usuarioSeleccionado, 0);
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        String rol = cbxRol.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de usuario y la contraseña no pueden estar vacíos.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/restaurante", "root", "");
             PreparedStatement pst = conn.prepareStatement("UPDATE usuarios SET username = ?, password = ?, rol = ? WHERE id = ?")) {
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, rol);
            pst.setInt(4, id);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Usuario actualizado");
            cargarUsuarios();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error actualizando usuario: " + ex.getMessage());
        }
    }

    private void eliminarUsuario() {
        if (usuarioSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para eliminar");
            return;
        }

        int id = (int) tableModel.getValueAt(usuarioSeleccionado, 0);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/restaurante", "root", "");
             PreparedStatement pst = conn.prepareStatement("DELETE FROM usuarios WHERE id = ?")) {
            pst.setInt(1, id);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Usuario eliminado");
            cargarUsuarios();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error eliminando usuario: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GestionarU().setVisible(true);
            }
        });
    }
}
