package com.example.controlador;

import com.example.modelo.HistorialVentaVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HistorialVentasDAO {

    public List<HistorialVentaVO> listarVentas() {

        List<HistorialVentaVO> lista = new ArrayList<>();

        String sql = "SELECT v.id_venta, v.fecha_hora, v.total, " +
                "COALESCE(NULLIF(TRIM(CONCAT_WS(' ', c.nombre, c.apellido)), ''), 'Sin cliente') AS cliente, " +
                "COALESCE(mp.nombre_metodo, 'Sin metodo') AS metodo_pago, " +
                "COALESCE(u.nombre, 'Sin usuario') AS cajero " +
                "FROM ventas v " +
                "LEFT JOIN clientes c ON v.id_cliente = c.id_cliente " +
                "LEFT JOIN metodos_pago mp ON v.id_metodo_pago = mp.id_metodo_pago " +
                "LEFT JOIN usuarios u ON v.id_usuario = u.id_usuario " +
                "ORDER BY v.fecha_hora DESC, v.id_venta DESC";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearVenta(rs));
            }

        } catch (Exception e) {
            System.out.println("Error listar ventas " + e);
        }

        return lista;
    }

    public List<HistorialVentaVO> buscarVentas(String texto) {

        List<HistorialVentaVO> lista = new ArrayList<>();

        String sql = "SELECT v.id_venta, v.fecha_hora, v.total, " +
                "COALESCE(NULLIF(TRIM(CONCAT_WS(' ', c.nombre, c.apellido)), ''), 'Sin cliente') AS cliente, " +
                "COALESCE(mp.nombre_metodo, 'Sin metodo') AS metodo_pago, " +
                "COALESCE(u.nombre, 'Sin usuario') AS cajero " +
                "FROM ventas v " +
                "LEFT JOIN clientes c ON v.id_cliente = c.id_cliente " +
                "LEFT JOIN metodos_pago mp ON v.id_metodo_pago = mp.id_metodo_pago " +
                "LEFT JOIN usuarios u ON v.id_usuario = u.id_usuario " +
                "WHERE CAST(v.id_venta AS CHAR) LIKE ? " +
                "OR CAST(v.fecha_hora AS CHAR) LIKE ? " +
                "OR COALESCE(NULLIF(TRIM(CONCAT_WS(' ', c.nombre, c.apellido)), ''), '') LIKE ? " +
                "OR COALESCE(mp.nombre_metodo, '') LIKE ? " +
                "OR COALESCE(u.nombre, '') LIKE ? " +
                "ORDER BY v.fecha_hora DESC, v.id_venta DESC";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            String filtro = "%" + texto + "%";
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            ps.setString(3, filtro);
            ps.setString(4, filtro);
            ps.setString(5, filtro);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearVenta(rs));
            }

        } catch (Exception e) {
            System.out.println("Error buscar ventas " + e);
        }

        return lista;
    }

    public boolean anularVenta(int idVenta) {
        System.out.println("Anular venta no disponible con el esquema actual para id_venta=" + idVenta);
        return false;
    }

    private HistorialVentaVO mapearVenta(ResultSet rs) throws Exception {

        HistorialVentaVO v = new HistorialVentaVO();

        int idVenta = rs.getInt("id_venta");

        v.setId(idVenta);
        v.setNumeroVenta("V-" + idVenta);
        v.setFecha(rs.getTimestamp("fecha_hora"));
        v.setCliente(rs.getString("cliente"));
        v.setMetodoPago(rs.getString("metodo_pago"));
        v.setTotal(rs.getDouble("total"));
        v.setCajero(rs.getString("cajero"));

        return v;
    }
}
