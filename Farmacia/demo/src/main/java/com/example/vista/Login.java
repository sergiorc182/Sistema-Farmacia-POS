package com.example.vista;

import com.example.controlador.UsuarioDAO;
import com.example.modelo.UsuarioVO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Login extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public Login() {

        setTitle("Farmacia - Login");
        setSize(400,300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(null);

        JLabel titulo = new JLabel("Sistema Farmacia");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(new Color(0,150,90));
        titulo.setBounds(110,20,200,30);

        JLabel lblUsuario = new JLabel("Usuario");
        lblUsuario.setBounds(70,80,100,20);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(70,100,250,30);

        JLabel lblPassword = new JLabel("Contraseña");
        lblPassword.setBounds(70,140,100,20);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(70,160,250,30);

        btnLogin = new JButton("Ingresar");
        btnLogin.setBounds(140,210,120,35);
        btnLogin.setBackground(new Color(0,150,90));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);

        btnLogin.addActionListener((ActionEvent e) -> validarLogin());

        panel.add(titulo);
        panel.add(lblUsuario);
        panel.add(txtUsuario);
        panel.add(lblPassword);
        panel.add(txtPassword);
        panel.add(btnLogin);

        add(panel);
    }

    private void validarLogin(){

        String usuario = txtUsuario.getText();
        String password = String.valueOf(txtPassword.getPassword());

        UsuarioVO vo = new UsuarioVO(usuario,password);
        UsuarioDAO dao = new UsuarioDAO();

        if(dao.login(vo)){

            UsuarioVO usuarioLogueado = dao.obtenerUsuarioLogin(vo);

            JOptionPane.showMessageDialog(this,"Bienvenido al sistema");

            Dashboard dashboard = new Dashboard(usuarioLogueado);
            dashboard.setVisible(true);

            dispose();

        }else{

            JOptionPane.showMessageDialog(this,"Usuario o contraseña incorrectos");

        }
    }
}
