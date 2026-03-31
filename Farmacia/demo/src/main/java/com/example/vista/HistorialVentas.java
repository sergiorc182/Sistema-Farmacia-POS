package com.example.vista;

import com.example.controlador.HistorialVentasDAO;
import com.example.modelo.HistorialVentaVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HistorialVentas extends JFrame {

    JTable tabla;
    JTextField txtBuscar;

    DefaultTableModel modelo;

    HistorialVentasDAO dao = new HistorialVentasDAO();

    public HistorialVentas() {

        setTitle("Historial de Ventas");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(245, 250, 245));

        add(panelSuperior(), BorderLayout.NORTH);
        add(panelTabla(), BorderLayout.CENTER);

        listarVentas();
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

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setBackground(new Color(46, 125, 107));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnActualizar.addActionListener(e -> listarVentas());

        izquierda.add(new JLabel("Buscar venta: "));
        izquierda.add(txtBuscar);
        izquierda.add(btnBuscar);
        izquierda.add(btnActualizar);

        JPanel derecha = new JPanel();
        derecha.setBackground(Color.WHITE);

        JButton btnDetalle = new JButton("Ver Detalle");
        btnDetalle.setBackground(new Color(0, 150, 90));
        btnDetalle.setForeground(Color.WHITE);
        btnDetalle.setFocusPainted(false);
        btnDetalle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnDetalle.addActionListener(e -> verDetalle());

        JButton btnAnular = new JButton("Anular Venta");
        btnAnular.setBackground(new Color(200, 60, 60));
        btnAnular.setForeground(Color.WHITE);
        btnAnular.setFocusPainted(false);
        btnAnular.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAnular.addActionListener(e -> anularVenta());

        derecha.add(btnDetalle);
        derecha.add(btnAnular);

        panel.add(izquierda, BorderLayout.WEST);
        panel.add(derecha, BorderLayout.EAST);

        return panel;
    }

    private JScrollPane panelTabla() {

        String columnas[] = {
                "ID", "N Venta", "Fecha", "Cliente", "Metodo Pago", "Total", "Cajero"
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

    private void listarVentas() {

        modelo.setRowCount(0);

        List<HistorialVentaVO> lista = dao.listarVentas();

        for (HistorialVentaVO v : lista) {

            Object fila[] = {
                    v.getId(),
                    v.getNumeroVenta(),
                    v.getFecha(),
                    v.getCliente(),
                    v.getMetodoPago(),
                    v.getTotal(),
                    v.getCajero()
            };

            modelo.addRow(fila);
        }
    }

    private void buscar() {

        modelo.setRowCount(0);

        List<HistorialVentaVO> lista = dao.buscarVentas(txtBuscar.getText().trim());

        for (HistorialVentaVO v : lista) {

            Object fila[] = {
                    v.getId(),
                    v.getNumeroVenta(),
                    v.getFecha(),
                    v.getCliente(),
                    v.getMetodoPago(),
                    v.getTotal(),
                    v.getCajero()
            };

            modelo.addRow(fila);
        }
    }

    private void anularVenta() {
        JOptionPane.showMessageDialog(this, "Anular venta no esta disponible con la estructura actual de la base");
    }

    private void verDetalle() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una venta");
            return;
        }

        int idVenta = Integer.parseInt(modelo.getValueAt(fila, 0).toString());

        JOptionPane.showMessageDialog(this, "Detalle de venta ID: " + idVenta);
    }
}
