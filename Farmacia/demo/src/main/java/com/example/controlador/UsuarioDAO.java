package com.example.controlador;

import com.example.modelo.UsuarioVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public boolean login(UsuarioVO usuario) {

        boolean acceso = false;

        String sql = "SELECT * FROM usuarios WHERE nombre = ? AND password = ?";

        try {

            Connection conn = ConectarBase.conectar();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getPassword());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                acceso = true;
            }

        } catch (Exception e) {
            System.out.println("Error login: " + e.getMessage());
        }

        return acceso;
    }

    public UsuarioVO obtenerUsuarioLogin(UsuarioVO usuario) {

        String sql = "SELECT * FROM usuarios WHERE nombre = ? AND password = ?";

        try {

            Connection conn = ConectarBase.conectar();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getPassword());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                UsuarioVO usuarioEncontrado = new UsuarioVO();
                usuarioEncontrado.setIdUsuario(rs.getInt("id_usuario"));
                usuarioEncontrado.setNombre(rs.getString("nombre"));
                usuarioEncontrado.setPassword(rs.getString("password"));
                usuarioEncontrado.setIdRol(rs.getInt("id_rol"));
                return usuarioEncontrado;
            }

        } catch (Exception e) {
            System.out.println("Error obtener usuario login: " + e.getMessage());
        }

        return null;
    }

    public List<UsuarioVO> listarUsuarios() {

        List<UsuarioVO> lista = new ArrayList<>();

        String sql = "SELECT * FROM usuarios";

        try {

            Connection conn = ConectarBase.conectar();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                UsuarioVO u = new UsuarioVO();

                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setPassword(rs.getString("password"));
                u.setIdRol(rs.getInt("id_rol"));

                lista.add(u);
            }

        } catch (Exception e) {
            System.out.println("Error listar usuarios " + e);
        }

        return lista;
    }

    public List<UsuarioVO> buscarUsuarios(String texto) {

        List<UsuarioVO> lista = new ArrayList<>();

        String sql = "SELECT * FROM usuarios WHERE nombre LIKE ? OR CAST(id_rol AS CHAR) LIKE ?";

        try {

            Connection conn = ConectarBase.conectar();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                UsuarioVO u = new UsuarioVO();

                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setPassword(rs.getString("password"));
                u.setIdRol(rs.getInt("id_rol"));

                lista.add(u);
            }

        } catch (Exception e) {
            System.out.println("Error buscar usuarios " + e);
        }

        return lista;
    }

    public void insertarUsuario(UsuarioVO u) {

        String sql = "INSERT INTO usuarios(nombre,password,id_rol) VALUES(?,?,?)";

        try {

            Connection conn = ConectarBase.conectar();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, u.getNombre());
            ps.setString(2, u.getPassword());
            ps.setInt(3, u.getIdRol());

            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error insertar usuario " + e);
        }
    }

    public boolean eliminarUsuario(int idUsuario) {

        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";

        try {

            Connection conn = ConectarBase.conectar();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, idUsuario);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error eliminar usuario " + e);
        }

        return false;
    }
}
