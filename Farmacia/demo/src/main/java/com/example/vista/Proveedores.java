package com.example.vista;

import com.example.controlador.ProveedorDAO;
import com.example.modelo.ProveedorVO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Proveedores extends JFrame {

    JTable tabla;
    JTextField txtBuscar;

    DefaultTableModel modelo;

    ProveedorDAO dao = new ProveedorDAO();

    public Proveedores(){

        setTitle("Gestión de Proveedores");
        setSize(1000,600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(245,250,247));

        add(panelTitulo(),BorderLayout.NORTH);
        add(panelTabla(),BorderLayout.CENTER);
        add(panelBotones(),BorderLayout.SOUTH);

        listarProveedores();
    }

    /* =========================
       TITULO
    ========================= */

    private JPanel panelTitulo(){

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0,150,90));
        panel.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));

        JLabel titulo = new JLabel("Gestión de Proveedores");

        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI",Font.BOLD,18));

        JPanel buscador = new JPanel();
        buscador.setBackground(new Color(0,150,90));

        txtBuscar = new JTextField(20);

        JButton btnBuscar = new JButton("Buscar");

        btnBuscar.addActionListener(e -> buscar());

        buscador.add(txtBuscar);
        buscador.add(btnBuscar);

        panel.add(titulo,BorderLayout.WEST);
        panel.add(buscador,BorderLayout.EAST);

        return panel;
    }

    /* =========================
       TABLA
    ========================= */

    private JScrollPane panelTabla(){

        String columnas[]={
                "ID","Nombre","Empresa/Laboratorio","Telefono","Email"
        };

        modelo = new DefaultTableModel(null,columnas);

        tabla = new JTable(modelo);

        tabla.setRowHeight(28);

        tabla.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,14));
        tabla.getTableHeader().setBackground(new Color(0,150,90));
        tabla.getTableHeader().setForeground(Color.WHITE);

        tabla.addMouseListener(new java.awt.event.MouseAdapter(){

            public void mouseClicked(java.awt.event.MouseEvent evt){

                if(evt.getClickCount()==2){

                    editarProveedor();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(Color.WHITE);

        return scroll;
    }

    /* =========================
       BOTONES
    ========================= */

    private JPanel panelBotones(){

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);

        JButton btnNuevo = new JButton("Nuevo");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");

        btnNuevo.setBackground(new Color(0,150,90));
        btnNuevo.setForeground(Color.WHITE);

        btnEditar.setBackground(new Color(255,170,0));
        btnEditar.setForeground(Color.WHITE);

        btnEliminar.setBackground(new Color(200,60,60));
        btnEliminar.setForeground(Color.WHITE);

        btnNuevo.addActionListener(e -> agregarProveedor());
        btnEditar.addActionListener(e -> editarProveedor());
        btnEliminar.addActionListener(e -> eliminarProveedor());

        panel.add(btnNuevo);
        panel.add(btnEditar);
        panel.add(btnEliminar);

        return panel;
    }

    /* =========================
       LISTAR PROVEEDORES
    ========================= */

    private void listarProveedores(){

        modelo.setRowCount(0);

        List<ProveedorVO> lista = dao.listarProveedores();

        for(ProveedorVO p : lista){

            Object fila[]={
                    p.getId(),
                    p.getNombre(),
                    p.getEmpresa(),
                    p.getTelefono(),
                    p.getEmail()
            };

            modelo.addRow(fila);
        }
    }

    /* =========================
       BUSCAR
    ========================= */

    private void buscar(){

        modelo.setRowCount(0);

        List<ProveedorVO> lista = dao.buscarProveedor(txtBuscar.getText());

        for(ProveedorVO p : lista){

            Object fila[]={
                    p.getId(),
                    p.getNombre(),
                    p.getEmpresa(),
                    p.getTelefono(),
                    p.getEmail()
            };

            modelo.addRow(fila);
        }
    }

    /* =========================
       AGREGAR
    ========================= */

    private void agregarProveedor(){

        JTextField nombre = new JTextField();
        JTextField empresa = new JTextField();
        JTextField telefono = new JTextField();
        JTextField email = new JTextField();

        Object[] campos={
                "Nombre",nombre,
                "Empresa/Laboratorio",empresa,
                "Telefono",telefono,
                "Email",email
        };

        int opcion = JOptionPane.showConfirmDialog(this,campos,"Nuevo proveedor",JOptionPane.OK_CANCEL_OPTION);

        if(opcion==JOptionPane.OK_OPTION){

            ProveedorVO p = new ProveedorVO();

            p.setNombre(nombre.getText());
            p.setEmpresa(empresa.getText());
            p.setTelefono(telefono.getText());
            p.setEmail(email.getText());

            dao.agregarProveedor(p);

            listarProveedores();
        }
    }

    /* =========================
       EDITAR
    ========================= */

    private void editarProveedor(){

        int fila = tabla.getSelectedRow();

        if(fila==-1){

            JOptionPane.showMessageDialog(this,"Seleccione proveedor");
            return;
        }

        int id = Integer.parseInt(tabla.getValueAt(fila,0).toString());

        JTextField nombre = new JTextField(tabla.getValueAt(fila,1).toString());
        JTextField empresa = new JTextField(tabla.getValueAt(fila,2).toString());
        JTextField telefono = new JTextField(tabla.getValueAt(fila,3).toString());
        JTextField email = new JTextField(tabla.getValueAt(fila,4).toString());

        Object[] campos={
                "Nombre",nombre,
                "Empresa/Laboratorio",empresa,
                "Telefono",telefono,
                "Email",email
        };

        int opcion = JOptionPane.showConfirmDialog(this,campos,"Editar proveedor",JOptionPane.OK_CANCEL_OPTION);

        if(opcion==JOptionPane.OK_OPTION){

            ProveedorVO p = new ProveedorVO();

            p.setId(id);
            p.setNombre(nombre.getText());
            p.setEmpresa(empresa.getText());
            p.setTelefono(telefono.getText());
            p.setEmail(email.getText());

            dao.editarProveedor(p);

            listarProveedores();
        }
    }

    /* =========================
       ELIMINAR
    ========================= */

    private void eliminarProveedor(){

        int fila = tabla.getSelectedRow();

        if(fila==-1){

            JOptionPane.showMessageDialog(this,"Seleccione proveedor");
            return;
        }

        int id = Integer.parseInt(tabla.getValueAt(fila,0).toString());

        int confirm = JOptionPane.showConfirmDialog(this,"¿Eliminar proveedor?");

        if(confirm==JOptionPane.YES_OPTION){

            dao.eliminarProveedor(id);

            listarProveedores();
        }
    }

    public static class SeleccionarProveedor extends JDialog {

    JTable tabla;
    DefaultTableModel modelo;

    ProveedorDAO dao = new ProveedorDAO();

    int idProveedor;
    String nombreProveedor;

    public SeleccionarProveedor(JFrame parent) {

        super(parent, "Seleccionar Proveedor", true);

        setSize(600,400);
        setLocationRelativeTo(parent);

        add(panelTabla());

        listarProveedores();
    }

    private JScrollPane panelTabla(){

        String columnas[]={
                "ID","Nombre","Empresa","Telefono"
        };

        modelo = new DefaultTableModel(null,columnas);

        tabla = new JTable(modelo);

        tabla.addMouseListener(new java.awt.event.MouseAdapter(){

            public void mouseClicked(java.awt.event.MouseEvent evt){

                if(evt.getClickCount()==2){

                    seleccionarProveedor();
                }

            }

        });

        return new JScrollPane(tabla);
    }

    private void listarProveedores(){

        modelo.setRowCount(0);

        List<ProveedorVO> lista = dao.listarProveedores();

        for(ProveedorVO p:lista){

            Object fila[]={
                    p.getId(),
                    p.getNombre(),
                    p.getEmpresa(),
                    p.getTelefono()
            };

            modelo.addRow(fila);
        }
    }

    private void seleccionarProveedor(){

        int fila = tabla.getSelectedRow();

        idProveedor = Integer.parseInt(tabla.getValueAt(fila,0).toString());
        nombreProveedor = tabla.getValueAt(fila,1).toString();

        dispose();
    }

    public int getIdProveedor(){
        return idProveedor;
    }

    public String getNombreProveedor(){
        return nombreProveedor;
    }
}
}