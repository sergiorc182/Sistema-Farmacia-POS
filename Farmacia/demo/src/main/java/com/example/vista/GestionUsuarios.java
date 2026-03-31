package com.example.vista;

import com.example.controlador.UsuarioDAO;
import com.example.modelo.UsuarioVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GestionUsuarios extends JFrame {

    private final UsuarioVO usuarioLogueado;
    private JButton btnEliminar;

    JTable tabla;
    DefaultTableModel modelo;

    JTextField txtBuscar;

    UsuarioDAO dao = new UsuarioDAO();

    public GestionUsuarios(UsuarioVO usuarioLogueado) {

        this.usuarioLogueado = usuarioLogueado;

        setTitle("Gestion de Usuarios");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(245, 250, 245));

        add(panelSuperior(), BorderLayout.NORTH);
        add(panelTabla(), BorderLayout.CENTER);

        listarUsuarios();
    }

    private JPanel panelSuperior() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel izquierda = new JPanel();
        izquierda.setBackground(Color.WHITE);

        txtBuscar = new JTextField(20);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(new Color(0, 150, 90));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBuscar.addActionListener(e -> buscar());

        izquierda.add(new JLabel("Buscar: "));
        izquierda.add(txtBuscar);
        izquierda.add(btnBuscar);

        JPanel derecha = new JPanel();
        derecha.setBackground(Color.WHITE);

        JButton btnNuevo = new JButton("Anadir Usuario");
        btnNuevo.setBackground(new Color(0, 150, 90));
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFocusPainted(false);
        btnNuevo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnNuevo.addActionListener(e -> nuevoUsuario());

        btnEliminar = new JButton("Eliminar Usuario");
        btnEliminar.setBackground(new Color(200, 70, 70));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnEliminar.setEnabled(esAdmin());

        derecha.add(btnNuevo);
        derecha.add(btnEliminar);

        panel.add(izquierda, BorderLayout.WEST);
        panel.add(derecha, BorderLayout.EAST);

        return panel;
    }

    private JScrollPane panelTabla() {

        String columnas[] = {
                "ID", "Nombre", "Rol"
        };

        modelo = new DefaultTableModel(null, columnas);

        tabla = new JTable(modelo);
        tabla.setRowHeight(28);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabla.getTableHeader().setBackground(new Color(0, 150, 90));
        tabla.getTableHeader().setForeground(Color.WHITE);

        tabla.setSelectionBackground(new Color(200, 240, 220));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        return scroll;
    }

    private void listarUsuarios() {

        modelo.setRowCount(0);

        List<UsuarioVO> lista = dao.listarUsuarios();

        for (UsuarioVO u : lista) {

            modelo.addRow(new Object[]{
                    u.getIdUsuario(),
                    u.getNombre(),
                    u.getIdRol()
            });
        }
    }

    private void buscar() {

        String texto = txtBuscar.getText();

        modelo.setRowCount(0);

        List<UsuarioVO> lista = dao.buscarUsuarios(texto);

        for (UsuarioVO u : lista) {

            modelo.addRow(new Object[]{
                    u.getIdUsuario(),
                    u.getNombre(),
                    u.getIdRol()
            });
        }
    }

    private void nuevoUsuario() {

        String nombre = JOptionPane.showInputDialog("Nombre usuario");
        String password = JOptionPane.showInputDialog("Password");
        String rol = JOptionPane.showInputDialog("Rol (1=Admin / 2=Cajero)");

        if (nombre == null || password == null || rol == null ||
                nombre.trim().isEmpty() || password.trim().isEmpty() || rol.trim().isEmpty()) {
            return;
        }

        UsuarioVO u = new UsuarioVO();

        u.setNombre(nombre);
        u.setPassword(password);
        u.setIdRol(Integer.parseInt(rol));

        dao.insertarUsuario(u);

        listarUsuarios();
    }

    private void eliminarUsuario() {

        if (!esAdmin()) {
            JOptionPane.showMessageDialog(this, "Solo un usuario con rol Admin puede eliminar usuarios");
            return;
        }

        int filaSeleccionada = tabla.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario para eliminar");
            return;
        }

        int idUsuario = Integer.parseInt(modelo.getValueAt(filaSeleccionada, 0).toString());
        String nombreUsuario = modelo.getValueAt(filaSeleccionada, 1).toString();

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "Deseas eliminar al usuario \"" + nombreUsuario + "\"?",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {

            boolean eliminado = dao.eliminarUsuario(idUsuario);

            if (eliminado) {
                JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente");
                listarUsuarios();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar el usuario");
            }
        }
    }

    private boolean esAdmin() {
        return usuarioLogueado != null && usuarioLogueado.getIdRol() == 1;
    }
}
