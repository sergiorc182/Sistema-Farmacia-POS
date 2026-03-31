package com.example.vista;

import com.example.controlador.CompraDAO;
import com.example.controlador.ProveedorDAO;
import com.example.controlador.inventarioDAO;
import com.example.modelo.DetalleCompraVO;
import com.example.modelo.ProveedorVO;
import com.example.modelo.inventarioVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Compras extends JFrame {

    JTable tabla;
    DefaultTableModel modelo;

    JComboBox<String> comboProveedores;
    JTextField txtProveedorConfirmado;
    JTextField txtBuscarProducto;
    JTextField txtProducto;
    JTextField txtCantidad;
    JTextField txtCostoUnitario;
    JTextField txtFechaCompra;

    JLabel lblTotal = new JLabel("Total: $0");

    ProveedorDAO proveedorDAO = new ProveedorDAO();
    inventarioDAO productoDAO = new inventarioDAO();
    CompraDAO compraDAO = new CompraDAO();

    List<ProveedorVO> proveedores = new ArrayList<>();
    List<DetalleCompraVO> listaDetalle = new ArrayList<>();

    int idProveedorSeleccionado;
    int idProductoSeleccionado;
    double total = 0;

    public Compras() {

        setTitle("Gestion de Compras");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(245, 250, 245));

        add(panelFormulario(), BorderLayout.NORTH);
        add(panelTabla(), BorderLayout.CENTER);
        add(panelAcciones(), BorderLayout.SOUTH);

        cargarProveedores();
        txtFechaCompra.setText(LocalDate.now().toString());
    }

    private JPanel panelFormulario() {

        JPanel panel = new JPanel(new GridLayout(3, 1, 0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel filaProveedor = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filaProveedor.setBackground(Color.WHITE);

        comboProveedores = new JComboBox<>();
        comboProveedores.setPreferredSize(new Dimension(240, 30));

        JButton btnConfirmarProveedor = new JButton("Confirmar Proveedor");
        btnConfirmarProveedor.setBackground(new Color(0, 150, 90));
        btnConfirmarProveedor.setForeground(Color.WHITE);
        btnConfirmarProveedor.setFocusPainted(false);
        btnConfirmarProveedor.addActionListener(e -> confirmarProveedor());

        txtProveedorConfirmado = new JTextField(25);
        txtProveedorConfirmado.setEditable(false);

        filaProveedor.add(new JLabel("Proveedor:"));
        filaProveedor.add(comboProveedores);
        filaProveedor.add(btnConfirmarProveedor);
        filaProveedor.add(new JLabel("Confirmado:"));
        filaProveedor.add(txtProveedorConfirmado);

        JPanel filaProducto = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filaProducto.setBackground(Color.WHITE);

        txtBuscarProducto = new JTextField(18);
        txtProducto = new JTextField(20);
        txtProducto.setEditable(false);

        JButton btnBuscarProducto = new JButton("Buscar Producto");
        btnBuscarProducto.setBackground(new Color(0, 150, 90));
        btnBuscarProducto.setForeground(Color.WHITE);
        btnBuscarProducto.setFocusPainted(false);
        btnBuscarProducto.addActionListener(e -> buscarOAgregarProducto());

        filaProducto.add(new JLabel("Buscar producto:"));
        filaProducto.add(txtBuscarProducto);
        filaProducto.add(btnBuscarProducto);
        filaProducto.add(new JLabel("Producto:"));
        filaProducto.add(txtProducto);

        JPanel filaCompra = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filaCompra.setBackground(Color.WHITE);

        txtCantidad = new JTextField(8);
        txtCostoUnitario = new JTextField(8);
        txtFechaCompra = new JTextField(10);

        JButton btnAgregar = new JButton("Agregar Compra");
        btnAgregar.setBackground(new Color(0, 150, 90));
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFocusPainted(false);
        btnAgregar.addActionListener(e -> agregarCompraAlPanel());

        filaCompra.add(new JLabel("Cantidad:"));
        filaCompra.add(txtCantidad);
        filaCompra.add(new JLabel("Costo Unitario:"));
        filaCompra.add(txtCostoUnitario);
        filaCompra.add(new JLabel("Fecha Compra:"));
        filaCompra.add(txtFechaCompra);
        filaCompra.add(btnAgregar);

        panel.add(filaProveedor);
        panel.add(filaProducto);
        panel.add(filaCompra);

        return panel;
    }

    private JScrollPane panelTabla() {

        String columnas[] = {
                "ID", "Producto", "Cantidad", "Costo Unitario", "Fecha Compra", "Subtotal"
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
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        return scroll;
    }

    private JPanel panelAcciones() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        panel.setBackground(Color.WHITE);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(200, 60, 60));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.addActionListener(e -> eliminarFila());

        JButton btnConfirmarCompra = new JButton("Confirmar Compra");
        btnConfirmarCompra.setBackground(new Color(46, 125, 107));
        btnConfirmarCompra.setForeground(Color.WHITE);
        btnConfirmarCompra.setFocusPainted(false);
        btnConfirmarCompra.addActionListener(e -> confirmarCompra());

        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(new Color(0, 150, 90));

        panel.add(lblTotal);
        panel.add(btnEliminar);
        panel.add(btnConfirmarCompra);

        return panel;
    }

    private void cargarProveedores() {

        comboProveedores.removeAllItems();
        proveedores = proveedorDAO.listarProveedores();

        for (ProveedorVO proveedor : proveedores) {
            comboProveedores.addItem(proveedor.getNombre());
        }
    }

    private void confirmarProveedor() {

        int indice = comboProveedores.getSelectedIndex();

        if (indice < 0 || indice >= proveedores.size()) {
            JOptionPane.showMessageDialog(this, "Selecciona un proveedor");
            return;
        }

        ProveedorVO proveedor = proveedores.get(indice);
        idProveedorSeleccionado = proveedor.getId();
        txtProveedorConfirmado.setText(proveedor.getNombre());
    }

    private void buscarOAgregarProducto() {

        if (idProveedorSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Confirma un proveedor primero");
            return;
        }

        String nombreProducto = txtBuscarProducto.getText().trim();

        if (nombreProducto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa un producto");
            return;
        }

        inventarioVO producto = productoDAO.obtenerProductoPorNombreExacto(nombreProducto);

        if (producto == null) {

            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "El producto no existe. Deseas agregarlo a este proveedor y al inventario?",
                    "Nuevo producto",
                    JOptionPane.YES_NO_OPTION
            );

            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }

            String costoTexto = JOptionPane.showInputDialog(this, "Costo inicial del nuevo producto");
            if (costoTexto == null || costoTexto.trim().isEmpty()) {
                return;
            }

            try {
                double costoInicial = Double.parseDouble(costoTexto.trim().replace(",", "."));
                boolean creado = productoDAO.agregarProductoDesdeCompra(nombreProducto, idProveedorSeleccionado, costoInicial);

                if (!creado) {
                    JOptionPane.showMessageDialog(this, "No se pudo crear el producto");
                    return;
                }

                producto = productoDAO.obtenerProductoPorNombreExacto(nombreProducto);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Costo invalido");
                return;
            }
        }

        if (producto != null) {
            idProductoSeleccionado = producto.getId();
            txtProducto.setText(producto.getNombre());
            if (txtCostoUnitario.getText().trim().isEmpty()) {
                txtCostoUnitario.setText(String.valueOf(producto.getPrecioCompra()));
            }
        }
    }

    private void agregarCompraAlPanel() {

        if (idProveedorSeleccionado == 0) {
            JOptionPane.showMessageDialog(this, "Confirma un proveedor");
            return;
        }

        if (idProductoSeleccionado == 0 || txtProducto.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Busca o crea un producto");
            return;
        }

        if (txtCantidad.getText().trim().isEmpty() || txtCostoUnitario.getText().trim().isEmpty() || txtFechaCompra.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa cantidad, costo unitario y fecha");
            return;
        }

        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            double costo = Double.parseDouble(txtCostoUnitario.getText().trim().replace(",", "."));
            String fechaCompra = txtFechaCompra.getText().trim();
            Date.valueOf(fechaCompra);

            double subtotal = cantidad * costo;

            modelo.addRow(new Object[]{
                    idProductoSeleccionado,
                    txtProducto.getText().trim(),
                    cantidad,
                    costo,
                    fechaCompra,
                    subtotal
            });

            DetalleCompraVO detalle = new DetalleCompraVO();
            detalle.setIdProducto(idProductoSeleccionado);
            detalle.setProducto(txtProducto.getText().trim());
            detalle.setCantidad(cantidad);
            detalle.setCostoUnitario(costo);
            detalle.setSubtotal(subtotal);

            listaDetalle.add(detalle);

            recalcularTotal();
            limpiarProducto();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Datos de compra invalidos");
        }
    }

    private void eliminarFila() {

        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una compra del panel");
            return;
        }

        listaDetalle.remove(fila);
        modelo.removeRow(fila);
        recalcularTotal();
    }

    private void recalcularTotal() {

        total = 0;

        for (int i = 0; i < modelo.getRowCount(); i++) {
            total += Double.parseDouble(modelo.getValueAt(i, 5).toString());
        }

        lblTotal.setText("Total: $" + total);
    }

    private void confirmarCompra() {

        if (idProveedorSeleccionado == 0 || txtProveedorConfirmado.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Confirma un proveedor");
            return;
        }

        if (listaDetalle.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Agrega al menos un producto al panel");
            return;
        }

        Date fechaCompra;

        try {
            fechaCompra = Date.valueOf(txtFechaCompra.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Fecha de compra invalida. Usa YYYY-MM-DD");
            return;
        }

        int idCompra = compraDAO.guardarCompra(idProveedorSeleccionado, total, fechaCompra);

        if (idCompra == 0) {
            JOptionPane.showMessageDialog(this, "No se pudo registrar la compra");
            return;
        }

        boolean detalleGuardado = compraDAO.guardarDetalle(idCompra, listaDetalle);

        if (detalleGuardado) {
            JOptionPane.showMessageDialog(this, "Compra exitosa con " + txtProveedorConfirmado.getText().trim());
            limpiarTodo();
        } else {
            JOptionPane.showMessageDialog(this, "La compra no pudo completarse correctamente");
        }
    }

    private void limpiarProducto() {
        idProductoSeleccionado = 0;
        txtBuscarProducto.setText("");
        txtProducto.setText("");
        txtCantidad.setText("");
        txtCostoUnitario.setText("");
    }

    private void limpiarTodo() {
        modelo.setRowCount(0);
        listaDetalle.clear();
        total = 0;
        idProveedorSeleccionado = 0;
        lblTotal.setText("Total: $0");
        txtProveedorConfirmado.setText("");
        txtFechaCompra.setText(LocalDate.now().toString());
        limpiarProducto();
        cargarProveedores();
    }
}
