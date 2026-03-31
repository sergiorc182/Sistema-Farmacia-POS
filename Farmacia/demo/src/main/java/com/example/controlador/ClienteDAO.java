package com.example.controlador;

import com.example.modelo.ClienteVO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public List<ClienteVO> listarClientes() {

        List<ClienteVO> lista = new ArrayList<>();

        String sql = "SELECT id_cliente, nombre, apellido, dni, telefono, direccion FROM clientes";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }

        } catch (Exception e) {
            System.out.println("Error listar clientes " + e);
        }

        return lista;
    }

    public List<ClienteVO> buscarClientes(String texto) {

        List<ClienteVO> lista = new ArrayList<>();

        String sql = "SELECT id_cliente, nombre, apellido, dni, telefono, direccion " +
                "FROM clientes WHERE dni LIKE ? OR nombre LIKE ? OR apellido LIKE ?";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            String filtro = "%" + texto + "%";
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            ps.setString(3, filtro);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }

        } catch (Exception e) {
            System.out.println("Error buscar clientes " + e);
        }

        return lista;
    }

    public boolean agregarCliente(ClienteVO c) {

        if (c == null ||
                c.getNombre() == null || c.getNombre().trim().isEmpty() ||
                c.getApellido() == null || c.getApellido().trim().isEmpty() ||
                c.getDni() == null || c.getDni().trim().isEmpty() ||
                c.getTelefono() == null || c.getTelefono().trim().isEmpty() ||
                c.getDireccion() == null || c.getDireccion().trim().isEmpty()) {
            return false;
        }

        if (existeClientePorDni(c.getDni().trim())) {
            System.out.println("Error agregar cliente: ya existe un cliente con ese DNI");
            return false;
        }

        String sql = "INSERT INTO clientes(nombre, apellido, dni, telefono, direccion) VALUES(?,?,?,?,?)";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, c.getNombre().trim());
            ps.setString(2, c.getApellido().trim());
            ps.setString(3, c.getDni().trim());
            ps.setString(4, c.getTelefono().trim());
            ps.setString(5, c.getDireccion().trim());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error agregar cliente " + e.getMessage());
            return false;
        }
    }

    public boolean existeClientePorDni(String dni) {

        String sql = "SELECT 1 FROM clientes WHERE dni = ?";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, dni);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            System.out.println("Error validar cliente por DNI " + e.getMessage());
            return false;
        }
    }

    public boolean editarCliente(ClienteVO c) {

        String sql = "UPDATE clientes SET nombre=?, apellido=?, dni=?, telefono=?, direccion=? WHERE id_cliente=?";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido());
            ps.setString(3, c.getDni());
            ps.setString(4, c.getTelefono());
            ps.setString(5, c.getDireccion());
            ps.setInt(6, c.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error editar cliente " + e);
            return false;
        }
    }

    public boolean eliminarCliente(int id) {

        String sql = "DELETE FROM clientes WHERE id_cliente=?";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error eliminar cliente " + e);
            return false;
        }
    }

    private ClienteVO mapearCliente(ResultSet rs) throws SQLException {

        ClienteVO c = new ClienteVO();

        c.setId(rs.getInt("id_cliente"));
        c.setNombre(rs.getString("nombre"));
        c.setApellido(rs.getString("apellido"));
        c.setDni(rs.getString("dni"));
        c.setTelefono(rs.getString("telefono"));
        c.setDireccion(rs.getString("direccion"));

        return c;
    }
}
