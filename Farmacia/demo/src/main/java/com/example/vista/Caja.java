package com.example.vista;

import com.example.controlador.CajaDAO;
import com.example.modelo.CajaVO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Caja extends JFrame {

    JLabel lblEstado = new JLabel();
    JLabel lblInicial = new JLabel();
    JLabel lblEfectivo = new JLabel();
    JLabel lblTarjeta = new JLabel();
    JLabel lblEsperado = new JLabel();
    JLabel lblUsuario = new JLabel();

    CajaDAO dao = new CajaDAO();

    public Caja() {

        setTitle("Control de Caja");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        getContentPane().setBackground(new Color(245, 250, 247));

        add(panelTitulo(), BorderLayout.NORTH);
        add(panelDatos(), BorderLayout.CENTER);
        add(panelBotones(), BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                cargarDatos();
            }
        });

        cargarDatos();
    }

    private JPanel panelTitulo() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 150, 90));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel titulo = new JLabel("Control de Caja");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setFocusPainted(false);
        btnActualizar.addActionListener(e -> cargarDatos());

        panel.add(titulo, BorderLayout.WEST);
        panel.add(btnActualizar, BorderLayout.EAST);

        return panel;
    }

    private JPanel panelDatos() {

        JPanel panel = new JPanel(new GridLayout(2, 3, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(245, 250, 247));

        panel.add(crearTarjeta("Estado Caja", lblEstado));
        panel.add(crearTarjeta("Monto Inicial", lblInicial));
        panel.add(crearTarjeta("Ventas Efectivo", lblEfectivo));
        panel.add(crearTarjeta("Ventas Tarjeta", lblTarjeta));
        panel.add(crearTarjeta("Efectivo Esperado", lblEsperado));
        panel.add(crearTarjeta("Usuario", lblUsuario));

        return panel;
    }

    private JPanel crearTarjeta(String titulo, JLabel valor) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(0, 150, 90)));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));

        valor.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valor.setHorizontalAlignment(SwingConstants.CENTER);
        valor.setForeground(new Color(0, 120, 70));

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(valor, BorderLayout.CENTER);

        return card;
    }

    private JPanel panelBotones() {

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);

        JButton btnAbrir = new JButton("Abrir Caja");
        JButton btnCerrar = new JButton("Cerrar Caja");
        JButton btnRetiro = new JButton("Retiro Dinero");

        btnAbrir.setBackground(new Color(0, 150, 90));
        btnAbrir.setForeground(Color.WHITE);

        btnCerrar.setBackground(new Color(200, 60, 60));
        btnCerrar.setForeground(Color.WHITE);

        btnRetiro.setBackground(new Color(255, 170, 0));
        btnRetiro.setForeground(Color.WHITE);

        btnAbrir.addActionListener(e -> abrirCaja());
        btnCerrar.addActionListener(e -> cerrarCaja());
        btnRetiro.addActionListener(e -> retiroDinero());

        panel.add(btnAbrir);
        panel.add(btnRetiro);
        panel.add(btnCerrar);

        return panel;
    }

    private void cargarDatos() {

        CajaVO c = dao.obtenerEstadoCaja();

        double efectivo = dao.ventasEfectivo();
        double tarjeta = dao.ventasTarjeta();
        double esperado = c.getMontoInicial() + efectivo;

        lblEstado.setText(c.getEstado() == null ? "CERRADA" : c.getEstado());
        lblInicial.setText("$" + c.getMontoInicial());
        lblEfectivo.setText("$" + efectivo);
        lblTarjeta.setText("$" + tarjeta);
        lblEsperado.setText("$" + esperado);
        lblUsuario.setText(c.getUsuario() == null ? "-" : c.getUsuario());
    }

    private void abrirCaja() {

        if (dao.estaCajaAbierta()) {
            JOptionPane.showMessageDialog(this, "La caja ya esta abierta");
            return;
        }

        String usuario = JOptionPane.showInputDialog("Usuario que abre caja");
        if (usuario == null || usuario.trim().isEmpty()) return;

        String monto = JOptionPane.showInputDialog("Monto inicial");
        if (monto == null || monto.trim().isEmpty()) return;

        try {
            double montoInicial = Double.parseDouble(monto.trim().replace(",", "."));
            boolean r = dao.abrirCaja(usuario.trim(), montoInicial);

            if (r) {
                JOptionPane.showMessageDialog(this, "Caja abierta");
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo abrir la caja. Verifica que el usuario exista.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Monto invalido");
        }
    }

    private void cerrarCaja() {

        if (!dao.estaCajaAbierta()) {
            JOptionPane.showMessageDialog(this, "La caja ya esta cerrada");
            return;
        }

        String usuario = JOptionPane.showInputDialog("Usuario que cierra caja");
        if (usuario == null || usuario.trim().isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Cerrar caja?");

        if (confirm == JOptionPane.YES_OPTION) {

            boolean r = dao.cerrarCaja(usuario.trim());

            if (r) {
                JOptionPane.showMessageDialog(this, "Caja cerrada");
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo cerrar la caja");
            }
        }
    }

    private void retiroDinero() {

        if (!dao.estaCajaAbierta()) {
            JOptionPane.showMessageDialog(this, "Debes abrir la caja antes de registrar un retiro");
            return;
        }

        String monto = JOptionPane.showInputDialog("Monto a retirar");
        if (monto == null || monto.trim().isEmpty()) return;

        String motivo = JOptionPane.showInputDialog("Motivo del retiro");
        if (motivo == null || motivo.trim().isEmpty()) return;

        try {
            boolean r = dao.retirarDinero(Double.parseDouble(monto.trim().replace(",", ".")), motivo.trim());

            if (r) {
                JOptionPane.showMessageDialog(this, "Retiro registrado");
                cargarDatos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar el retiro");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Monto invalido");
        }
    }
}
