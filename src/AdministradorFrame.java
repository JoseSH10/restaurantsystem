
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdministradorFrame extends JFrame {

    public AdministradorFrame() {
        setTitle("Admin Window");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel lblAdmin = new JLabel("Bienvenido Administrador");
        lblAdmin.setBounds(100, 20, 200, 25);
        add(lblAdmin);

        JButton btnGestionarProductos = new JButton("Gestionar Productos");
        btnGestionarProductos.setBounds(100, 60, 200, 25);
        add(btnGestionarProductos);

        JButton btnGestionarUsuarios = new JButton("Gestionar Usuarios");
        btnGestionarUsuarios.setBounds(100, 100, 200, 25);
        add(btnGestionarUsuarios);

        JButton btnVerVentas = new JButton("Ver Ventas");
        btnVerVentas.setBounds(100, 140, 200, 25);
        add(btnVerVentas);

        // Añadir ActionListeners a los botones
        btnGestionarProductos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GestionarP().setVisible(true);
            }
        });

        btnGestionarUsuarios.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               // new GestionarUsuariosFrame().setVisible(true);
            }
        });

        btnVerVentas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              //  new VerVentasFrame().setVisible(true);
            }
        });
    }
}