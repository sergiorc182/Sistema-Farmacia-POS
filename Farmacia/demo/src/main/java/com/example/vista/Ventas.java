package com.example.vista;

import com.example.controlador.CajaDAO;
import com.example.controlador.VentasDAO;
import com.example.controlador.inventarioDAO;
import com.example.modelo.UsuarioVO;
import com.example.modelo.VentasVO;
import com.example.modelo.inventarioVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class Ventas extends JFrame {

    JTable tablaProductos;
    JTable tablaCarrito;

    JTextField txtBuscar;

    JComboBox<String> comboPago;
    JComboBox<String> comboCliente;

    DefaultTableModel modeloProductos;
    DefaultTableModel modeloCarrito;

    JLabel lblTotal;

    inventarioDAO inventarioDAO = new inventarioDAO();
    VentasDAO ventasDAO = new VentasDAO();
    CajaDAO cajaDAO = new CajaDAO();
    private final UsuarioVO usuarioLogueado;

    List<VentasVO> carrito = new ArrayList<>();

    public Ventas(UsuarioVO usuarioLogueado) {

        this.usuarioLogueado = usuarioLogueado;

        setTitle("Punto de Venta");
        setSize(1200, 600);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        add(panelProductos());
        add(panelCarrito());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                cargarProductos();
            }
        });

        cargarProductos();
    }

    private JPanel panelProductos() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JPanel superior = new JPanel();
        superior.setBackground(Color.WHITE);

        txtBuscar = new JTextField(20);

        JButton btnBuscar = new JButton("Buscar");
        JButton btnActualizar = new JButton("Actualizar");

        btnBuscar.setBackground(new Color(0, 150, 90));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);

        btnActualizar.setBackground(new Color(46, 125, 107));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setFocusPainted(false);

        btnBuscar.addActionListener(e -> buscarProducto());
        btnActualizar.addActionListener(e -> cargarProductos());

        superior.add(new JLabel("Buscar producto"));
        superior.add(txtBuscar);
        superior.add(btnBuscar);
        superior.add(btnActualizar);

        panel.add(superior, BorderLayout.NORTH);

        String columnas[] = {"ID", "Nombre", "Categoria", "Precio", "Stock"};

        modeloProductos = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaProductos = new JTable(modeloProductos);
        tablaProductos.setRowHeight(28);

        tablaProductos.getTableHeader().setBackground(new Color(0, 150, 90));
        tablaProductos.getTableHeader().setForeground(Color.WHITE);

        tablaProductos.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseClicked(java.awt.event.MouseEvent evt) {

                int fila = tablaProductos.getSelectedRow();

                if (fila == -1) {
                    return;
                }

                int id = Integer.parseInt(modeloProductos.getValueAt(fila, 0).toString());
                String nombre = modeloProductos.getValueAt(fila, 1).toString();
                double precio = Double.parseDouble(modeloProductos.getValueAt(fila, 3).toString());

                agregarCarrito(id, nombre, precio);
            }
        });

        panel.add(new JScrollPane(tablaProductos), BorderLayout.CENTER);

        return panel;
    }

    private JPanel panelCarrito() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 250, 247));

        JPanel superior = new JPanel();
        superior.setBackground(Color.WHITE);

        comboPago = new JComboBox<>(new String[]{"Efectivo", "Tarjeta", "Transferencia"});
        comboCliente = new JComboBox<>(new String[]{"General", "Frecuente", "Mayorista"});

        superior.add(new JLabel("Metodo pago"));
        superior.add(comboPago);

        superior.add(new JLabel("Cliente"));
        superior.add(comboCliente);

        panel.add(superior, BorderLayout.NORTH);

        String columnas[] = {"Producto", "Precio", "Cantidad", "Subtotal"};

        modeloCarrito = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCarrito = new JTable(modeloCarrito);
        tablaCarrito.setRowHeight(28);

        panel.add(new JScrollPane(tablaCarrito), BorderLayout.CENTER);

        JPanel inferior = new JPanel(new BorderLayout());

        JPanel botones = new JPanel();

        JButton btnEliminar = new JButton("Eliminar");
        JButton btnCobrar = new JButton("Cobrar");
        JButton btnLimpiar = new JButton("Limpiar");

        btnCobrar.setBackground(new Color(0, 150, 90));
        btnCobrar.setForeground(Color.WHITE);

        btnEliminar.addActionListener(e -> eliminarProducto());
        btnCobrar.addActionListener(e -> cobrar());
        btnLimpiar.addActionListener(e -> limpiarCarrito());

        botones.add(btnEliminar);
        botones.add(btnCobrar);
        botones.add(btnLimpiar);

        lblTotal = new JLabel("Total: $0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotal.setForeground(new Color(0, 120, 70));

        inferior.add(lblTotal, BorderLayout.WEST);
        inferior.add(botones, BorderLayout.EAST);

        panel.add(inferior, BorderLayout.SOUTH);

        return panel;
    }

    private void cargarProductos() {

        modeloProductos.setRowCount(0);

        List<inventarioVO> lista = inventarioDAO.listarProductos();

        for (inventarioVO p : lista) {

            Object fila[] = {
                    p.getId(),
                    p.getNombre(),
                    p.getCategoria(),
                    p.getPrecioVenta(),
                    p.getStock()
            };

            modeloProductos.addRow(fila);
        }
    }

    private void buscarProducto() {

        String texto = txtBuscar.getText().trim();

        if (texto.isEmpty()) {
            cargarProductos();
            return;
        }

        modeloProductos.setRowCount(0);

        List<inventarioVO> lista = inventarioDAO.buscarProducto(texto);

        for (inventarioVO p : lista) {

            Object fila[] = {
                    p.getId(),
                    p.getNombre(),
                    p.getCategoria(),
                    p.getPrecioVenta(),
                    p.getStock()
            };

            modeloProductos.addRow(fila);
        }

        if (modeloProductos.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay productos para mostrar");
        }
    }

    private void agregarCarrito(int id, String nombre, double precio) {

        String cantidadStr = JOptionPane.showInputDialog("Cantidad");

        if (cantidadStr == null || cantidadStr.trim().isEmpty()) {
            return;
        }

        int cantidad = Integer.parseInt(cantidadStr);

        VentasVO item = new VentasVO();

        item.setIdProducto(id);
        item.setNombre(nombre);
        item.setPrecio(precio);
        item.setCantidad(cantidad);
        item.calcularSubtotal();

        carrito.add(item);

        modeloCarrito.addRow(new Object[]{
                nombre,
                precio,
                cantidad,
                item.getSubtotal()
        });

        calcularTotal();
    }

    private void eliminarProducto() {

        int fila = tablaCarrito.getSelectedRow();

        if (fila == -1) {

            JOptionPane.showMessageDialog(this, "Seleccione un producto");
            return;
        }

        carrito.remove(fila);
        modeloCarrito.removeRow(fila);

        calcularTotal();
    }

    private void calcularTotal() {

        double total = 0;

        for (VentasVO item : carrito) {

            total += item.getSubtotal();
        }

        lblTotal.setText("Total: $" + total);
    }

    private void cobrar() {

        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Agrega productos al carrito antes de cobrar");
            return;
        }

        if (!cajaDAO.estaCajaAbierta()) {
            JOptionPane.showMessageDialog(this, "Debes abrir la caja antes de cobrar una venta");
            return;
        }

        String metodo = comboPago.getSelectedItem().toString();
        String cliente = comboCliente.getSelectedItem().toString();

        boolean r = ventasDAO.registrarVenta(metodo, cliente, carrito, usuarioLogueado);

        if (r) {

            for (VentasVO item : carrito) {

                inventarioDAO.descontarStock(
                        item.getIdProducto(),
                        item.getCantidad()
                );
            }

            JOptionPane.showMessageDialog(this, "Venta registrada");

            limpiarCarrito();
            cargarProductos();

        } else {

            JOptionPane.showMessageDialog(this, "Error venta");
        }
    }

    private void limpiarCarrito() {

        carrito.clear();
        modeloCarrito.setRowCount(0);

        calcularTotal();
    }
}
