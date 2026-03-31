package com.example.vista;

import com.example.controlador.inventarioDAO;
import com.example.modelo.inventarioVO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Inventario extends JFrame {

    JTable tabla;
    JTextField txtBuscar;
    DefaultTableModel modelo;

    inventarioDAO dao = new inventarioDAO();

    public Inventario() {

        setTitle("Inventario - Sistema Farmacia");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        getContentPane().setBackground(new Color(245, 250, 247));

        add(panelSuperior(), BorderLayout.NORTH);
        add(panelTabla(), BorderLayout.CENTER);
        add(panelBotones(), BorderLayout.SOUTH);

        listarProductos();
    }

    private JPanel panelSuperior() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 220, 210)));

        JLabel lblBuscar = new JLabel("Buscar producto:");
        lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txtBuscar = new JTextField(18);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(new Color(46, 125, 107));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.addActionListener(e -> buscar());

        panel.add(lblBuscar);
        panel.add(txtBuscar);
        panel.add(btnBuscar);

        return panel;
    }

    private JScrollPane panelTabla() {

        String columnas[] = {
                "ID", "Nombre", "Categoria",
                "Precio Compra", "Precio Venta",
                "Stock", "Vencimiento"
        };

        modelo = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(28);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabla.getTableHeader().setBackground(new Color(0, 150, 90));
        tabla.getTableHeader().setForeground(Color.WHITE);

        tabla.setSelectionBackground(new Color(200, 240, 220));
        tabla.setDefaultRenderer(Object.class, new ColorInventario());

        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editarProducto();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        return scroll;
    }

    private JPanel panelBotones() {

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);

        JButton btnAgregar = new JButton("Agregar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");

        btnAgregar.setBackground(new Color(0, 150, 90));
        btnAgregar.setForeground(Color.WHITE);

        btnEditar.setBackground(new Color(255, 170, 0));
        btnEditar.setForeground(Color.WHITE);

        btnEliminar.setBackground(new Color(200, 60, 60));
        btnEliminar.setForeground(Color.WHITE);

        btnAgregar.addActionListener(e -> agregarProducto());
        btnEditar.addActionListener(e -> editarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());

        panel.add(btnAgregar);
        panel.add(btnEditar);
        panel.add(btnEliminar);

        return panel;
    }

    private void listarProductos() {

        modelo.setRowCount(0);

        List<inventarioVO> lista = dao.listarProductos();

        for (inventarioVO p : lista) {
            agregarFilaProducto(p);
        }
    }

    private void buscar() {

        modelo.setRowCount(0);

        List<inventarioVO> lista = dao.buscarProducto(txtBuscar.getText().trim());

        for (inventarioVO p : lista) {
            agregarFilaProducto(p);
        }
    }

    private void agregarFilaProducto(inventarioVO p) {

        modelo.addRow(new Object[]{
                p.getId(),
                p.getNombre(),
                p.getCategoria(),
                p.getPrecioCompra(),
                p.getPrecioVenta(),
                p.getStock(),
                p.getVencimiento()
        });
    }

    private void agregarProducto() {

        JTextField nombre = new JTextField();
        JTextField categoria = new JTextField();
        JTextField precioCompra = new JTextField();
        JTextField precioVenta = new JTextField();
        JTextField stock = new JTextField();
        JTextField vencimiento = new JTextField();

        Object[] campos = {
                "Nombre:", nombre,
                "Categoria:", categoria,
                "Precio Compra:", precioCompra,
                "Precio Venta:", precioVenta,
                "Stock:", stock,
                "Vencimiento (YYYY-MM-DD):", vencimiento
        };

        int opcion = JOptionPane.showConfirmDialog(
                this,
                campos,
                "Agregar Producto",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (opcion == JOptionPane.OK_OPTION) {

            try {

                inventarioVO p = new inventarioVO();

                p.setNombre(nombre.getText().trim());
                p.setCategoria(categoria.getText().trim());
                p.setPrecioCompra(Double.parseDouble(precioCompra.getText().trim()));
                p.setPrecioVenta(Double.parseDouble(precioVenta.getText().trim()));
                p.setStock(Integer.parseInt(stock.getText().trim()));
                p.setVencimiento(java.sql.Date.valueOf(vencimiento.getText().trim()));

                boolean agregado = dao.agregarProducto(p);

                if (agregado) {
                    listarProductos();
                    JOptionPane.showMessageDialog(this, "Producto agregado correctamente");
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo agregar el producto");
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Datos invalidos");
            }
        }
    }

    private void editarProducto() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto");
            return;
        }

        int id = Integer.parseInt(modelo.getValueAt(fila, 0).toString());

        JTextField nombre = new JTextField(modelo.getValueAt(fila, 1).toString());
        JTextField categoria = new JTextField(modelo.getValueAt(fila, 2).toString());
        JTextField precioCompra = new JTextField(modelo.getValueAt(fila, 3).toString());
        JTextField precioVenta = new JTextField(modelo.getValueAt(fila, 4).toString());
        JTextField stock = new JTextField(modelo.getValueAt(fila, 5).toString());
        JTextField vencimiento = new JTextField(modelo.getValueAt(fila, 6).toString());

        Object[] campos = {
                "Nombre:", nombre,
                "Categoria:", categoria,
                "Precio Compra:", precioCompra,
                "Precio Venta:", precioVenta,
                "Stock:", stock,
                "Vencimiento (YYYY-MM-DD):", vencimiento
        };

        int opcion = JOptionPane.showConfirmDialog(this, campos, "Editar Producto", JOptionPane.OK_CANCEL_OPTION);

        if (opcion == JOptionPane.OK_OPTION) {
            try {
                inventarioVO p = new inventarioVO();

                p.setId(id);
                p.setNombre(nombre.getText().trim());
                p.setCategoria(categoria.getText().trim());
                p.setPrecioCompra(Double.parseDouble(precioCompra.getText().trim()));
                p.setPrecioVenta(Double.parseDouble(precioVenta.getText().trim()));
                p.setStock(Integer.parseInt(stock.getText().trim()));
                p.setVencimiento(java.sql.Date.valueOf(vencimiento.getText().trim()));

                boolean editado = dao.editarProducto(p);

                if (editado) {
                    listarProductos();
                    JOptionPane.showMessageDialog(this, "Producto editado correctamente");
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo editar el producto");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Datos invalidos");
            }
        }
    }

    private void eliminarProducto() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto");
            return;
        }

        int id = Integer.parseInt(modelo.getValueAt(fila, 0).toString());
        String nombre = modelo.getValueAt(fila, 1).toString();

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "Deseas eliminar el producto \"" + nombre + "\"?",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION
        );

        if (opcion == JOptionPane.YES_OPTION) {

            boolean eliminado = dao.eliminarProducto(id);

            if (eliminado) {
                listarProductos();
                JOptionPane.showMessageDialog(this, "Producto eliminado correctamente");
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar el producto");
            }
        }
    }

    class ColorInventario extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (isSelected) {
                c.setBackground(table.getSelectionBackground());
                return c;
            }

            int stock = Integer.parseInt(table.getValueAt(row, 5).toString());
            Object valorVencimiento = table.getValueAt(row, 6);

            if (!(valorVencimiento instanceof Date)) {
                c.setBackground(Color.WHITE);
                return c;
            }

            Date vencimiento = (Date) valorVencimiento;
            long dias = vencimiento.getTime() - new Date().getTime();
            dias = TimeUnit.DAYS.convert(dias, TimeUnit.MILLISECONDS);

            if (stock <= 5) {
                c.setBackground(new Color(255, 200, 200));
            } else if (dias <= 30) {
                c.setBackground(new Color(255, 255, 180));
            } else {
                c.setBackground(Color.WHITE);
            }

            return c;
        }
    }
}
