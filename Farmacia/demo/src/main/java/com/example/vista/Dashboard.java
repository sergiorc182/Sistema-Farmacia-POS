package com.example.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import com.example.controlador.DashboardDAO;
import com.example.controlador.inventarioDAO;
import com.example.modelo.UsuarioVO;
import com.example.modelo.inventarioVO;

public class Dashboard extends JFrame {

    JLabel lblVentas;
    JLabel lblClientes;
    JLabel lblProductos;
    JLabel lblStock;
    JLabel lblVentasSemana;
    JLabel lblComprasSemana;
    private final UsuarioVO usuarioLogueado;

    DashboardDAO dao = new DashboardDAO();

    JButton btnDashboard = new JButton("Dashboard");
    JButton btnInventario = new JButton("Inventario");
    JButton btnVentas = new JButton("Ventas");
    JButton btnHistorial = new JButton("Historial");
    JButton btnCaja = new JButton("Caja");
    JButton btnClientes = new JButton("Clientes");
    JButton btnProveedores = new JButton("Proveedores");
    JButton btnCompras = new JButton("Compras");
    JButton btnUsuarios = new JButton("Usuarios");
    JButton btnAuditoria = new JButton("Auditoria");

    public Dashboard(UsuarioVO usuarioLogueado) {

        this.usuarioLogueado = usuarioLogueado;

        setTitle("Sistema Farmacia");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(245, 247, 250));

        add(menuLateral(), BorderLayout.WEST);
        add(panelPrincipal(), BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                cargarDatos();
            }
        });

        cargarDatos();
        eventosMenu();
        alertaVencimientos();
    }

    private JPanel menuLateral() {

        JPanel menu = new JPanel();
        menu.setPreferredSize(new Dimension(220, 800));
        menu.setBackground(new Color(33, 37, 41));
        menu.setLayout(new BorderLayout());

        JLabel titulo = new JLabel("FARMACIA", SwingConstants.CENTER);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JPanel contenedor = new JPanel();
        contenedor.setBackground(new Color(33, 37, 41));
        contenedor.setLayout(new GridLayout(10, 1, 0, 8));
        contenedor.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JButton[] botones = {
                btnDashboard, btnInventario, btnVentas,
                btnHistorial, btnCaja, btnClientes,
                btnProveedores, btnCompras, btnUsuarios
        };

        for (JButton b : botones) {
            b.setFocusPainted(false);
            b.setBackground(new Color(52, 58, 64));
            b.setForeground(Color.WHITE);
            b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            b.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            contenedor.add(b);
        }

        menu.add(titulo, BorderLayout.NORTH);
        menu.add(contenedor, BorderLayout.CENTER);

        return menu;
    }

    private JPanel panelPrincipal() {

        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 247, 250));
        panel.setLayout(new GridLayout(2, 3, 25, 25));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        lblVentas = new JLabel();
        lblClientes = new JLabel();
        lblProductos = new JLabel();
        lblStock = new JLabel();
        lblVentasSemana = new JLabel();
        lblComprasSemana = new JLabel();

        panel.add(crearTarjeta("Ventas Totales", lblVentas));
        panel.add(crearTarjeta("Clientes", lblClientes));
        panel.add(crearTarjeta("Productos", lblProductos));
        panel.add(crearTarjeta("Stock Bajo", lblStock));
        panel.add(crearTarjeta("Ventas ultimos 7 dias", lblVentasSemana));
        panel.add(crearTarjeta("Compras ultimos 7 dias", lblComprasSemana));

        return panel;
    }

    private JPanel crearTarjeta(String titulo, JLabel valor) {

        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitulo.setForeground(new Color(120, 120, 120));

        valor.setFont(new Font("Segoe UI", Font.BOLD, 34));
        valor.setForeground(new Color(46, 125, 107));
        valor.setHorizontalAlignment(SwingConstants.LEFT);

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(valor, BorderLayout.CENTER);

        return card;
    }

    private void eventosMenu() {

        btnInventario.addActionListener(e -> abrirInventario());
        btnVentas.addActionListener(e -> new Ventas(usuarioLogueado).setVisible(true));
        btnHistorial.addActionListener(e -> new HistorialVentas().setVisible(true));
        btnCaja.addActionListener(e -> new Caja().setVisible(true));
        btnClientes.addActionListener(e -> new Clientes().setVisible(true));
        btnProveedores.addActionListener(e -> new Proveedores().setVisible(true));
        btnCompras.addActionListener(e -> new Compras().setVisible(true));
        btnUsuarios.addActionListener(e -> new GestionUsuarios(usuarioLogueado).setVisible(true));
    }

    private void cargarDatos() {

        lblVentas.setText(String.valueOf(dao.totalVentas()));
        lblClientes.setText(String.valueOf(dao.totalClientes()));
        lblProductos.setText(String.valueOf(dao.totalProductos()));
        lblStock.setText(String.valueOf(dao.stockBajo()));
        lblVentasSemana.setText("$ " + dao.ventasUltimos7Dias());
        lblComprasSemana.setText("$ " + dao.comprasUltimos7Dias());
    }

    private void abrirInventario() {

        Inventario inv = new Inventario();
        inv.setVisible(true);
    }

    private void alertaVencimientos() {

        inventarioDAO inventarioDao = new inventarioDAO();
        List<inventarioVO> lista = inventarioDao.productosPorVencer();

        if (!lista.isEmpty()) {

            String mensaje = "Medicamentos por vencer:\n\n";

            for (inventarioVO p : lista) {
                mensaje += p.getNombre() + " - " + p.getVencimiento() + "\n";
            }

            JOptionPane.showMessageDialog(this, mensaje);
        }
    }
}
