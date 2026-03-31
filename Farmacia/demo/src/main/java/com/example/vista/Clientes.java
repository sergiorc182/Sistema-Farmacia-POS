package com.example.vista;

import com.example.controlador.ClienteDAO;
import com.example.modelo.ClienteVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Clientes extends JFrame {

    JTable tabla;
    JTextField txtBuscar;

    DefaultTableModel modelo;

    ClienteDAO dao = new ClienteDAO();

    public Clientes() {

        setTitle("Gestion de Clientes");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(245, 250, 245));

        add(panelSuperior(), BorderLayout.NORTH);
        add(panelTabla(), BorderLayout.CENTER);

        listarClientes();
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

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setBackground(new Color(46, 125, 107));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnActualizar.addActionListener(e -> listarClientes());

        JButton btnAgregar = new JButton("Nuevo Cliente");
        btnAgregar.setBackground(new Color(0, 150, 90));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFocusPainted(false);
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAgregar.addActionListener(e -> agregarCliente());

        derecha.add(btnActualizar);
        derecha.add(btnAgregar);

        panel.add(izquierda, BorderLayout.WEST);
        panel.add(derecha, BorderLayout.EAST);

        return panel;
    }

    private JScrollPane panelTabla() {

        String columnas[] = {
                "ID", "Nombre", "Apellido", "DNI", "Telefono", "Direccion"
        };

        modelo = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

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

    private void listarClientes() {

        modelo.setRowCount(0);

        List<ClienteVO> lista = dao.listarClientes();

        for (ClienteVO c : lista) {

            Object fila[] = {
                    c.getId(),
                    c.getNombre(),
                    c.getApellido(),
                    c.getDni(),
                    c.getTelefono(),
                    c.getDireccion()
            };

            modelo.addRow(fila);
        }
    }

    private void buscar() {

        modelo.setRowCount(0);

        List<ClienteVO> lista = dao.buscarClientes(txtBuscar.getText().trim());

        for (ClienteVO c : lista) {

            Object fila[] = {
                    c.getId(),
                    c.getNombre(),
                    c.getApellido(),
                    c.getDni(),
                    c.getTelefono(),
                    c.getDireccion()
            };

            modelo.addRow(fila);
        }
    }

    private void agregarCliente() {

        JTextField nombre = new JTextField();
        JTextField apellido = new JTextField();
        JTextField dni = new JTextField();
        JTextField telefono = new JTextField();
        JTextField direccion = new JTextField();

        Object[] campos = {
                "Nombre", nombre,
                "Apellido", apellido,
                "DNI", dni,
                "Telefono", telefono,
                "Direccion", direccion
        };

        int opcion = JOptionPane.showConfirmDialog(
                this,
                campos,
                "Nuevo Cliente",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (opcion == JOptionPane.OK_OPTION) {

            if (nombre.getText().trim().isEmpty() ||
                    apellido.getText().trim().isEmpty() ||
                    dni.getText().trim().isEmpty() ||
                    telefono.getText().trim().isEmpty() ||
                    direccion.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Completa todos los campos del cliente");
                return;
            }

            if (dao.existeClientePorDni(dni.getText().trim())) {
                JOptionPane.showMessageDialog(this, "Ya existe un cliente con ese DNI");
                return;
            }

            ClienteVO c = new ClienteVO();

            c.setNombre(nombre.getText().trim());
            c.setApellido(apellido.getText().trim());
            c.setDni(dni.getText().trim());
            c.setTelefono(telefono.getText().trim());
            c.setDireccion(direccion.getText().trim());

            boolean agregado = dao.agregarCliente(c);

            if (agregado) {
                listarClientes();
                JOptionPane.showMessageDialog(this, "Cliente agregado correctamente");
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo agregar el cliente");
            }
        }
    }
}
