package com.example.controlador;

import com.example.modelo.UsuarioVO;
import com.example.modelo.VentasVO;

import java.sql.*;
import java.util.List;

public class VentasDAO {

    public boolean registrarVenta(String metodoPago, String tipoCliente, List<VentasVO> carrito, UsuarioVO usuarioLogueado) {

        if (carrito == null || carrito.isEmpty() || usuarioLogueado == null) {
            return false;
        }

        CajaDAO cajaDAO = new CajaDAO();
        if (!cajaDAO.estaCajaAbierta()) {
            return false;
        }

        Connection cn = null;
        PreparedStatement psVenta = null;
        PreparedStatement psDetalle = null;
        ResultSet rs = null;

        try {

            cn = ConectarBase.conectar();
            cn.setAutoCommit(false);

            Integer idMetodoPago = obtenerIdMetodoPago(cn, metodoPago);
            Integer idCliente = obtenerOCrearClienteTipo(cn, tipoCliente);

            if (idMetodoPago == null || idCliente == null) {
                cn.rollback();
                return false;
            }

            double total = 0;
            for (VentasVO v : carrito) {
                total += v.getSubtotal();
            }

            String sqlVenta = "INSERT INTO ventas(fecha_hora, id_cliente, id_metodo_pago, id_usuario, total) " +
                    "VALUES(NOW(), ?, ?, ?, ?)";

            psVenta = cn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS);
            psVenta.setInt(1, idCliente);
            psVenta.setInt(2, idMetodoPago);
            psVenta.setInt(3, usuarioLogueado.getIdUsuario());
            psVenta.setDouble(4, total);
            psVenta.executeUpdate();

            rs = psVenta.getGeneratedKeys();
            if (!rs.next()) {
                cn.rollback();
                return false;
            }

            int idVenta = rs.getInt(1);

            String sqlDetalle = "INSERT INTO detalle_venta(id_venta,id_producto,cantidad,precio_unitario,subtotal) VALUES(?,?,?,?,?)";
            psDetalle = cn.prepareStatement(sqlDetalle);

            for (VentasVO v : carrito) {
                psDetalle.setInt(1, idVenta);
                psDetalle.setInt(2, v.getIdProducto());
                psDetalle.setInt(3, v.getCantidad());
                psDetalle.setDouble(4, v.getPrecio());
                psDetalle.setDouble(5, v.getSubtotal());
                psDetalle.executeUpdate();
            }

            cn.commit();
            return true;

        } catch (Exception e) {

            System.out.println("Error registrar venta " + e);

            try {
                if (cn != null) {
                    cn.rollback();
                }
            } catch (Exception ex) {
                System.out.println("Error rollback venta " + ex);
            }

            return false;
        } finally {
            try {
                if (rs != null) rs.close();
            } catch (Exception ignored) {
            }
            try {
                if (psDetalle != null) psDetalle.close();
            } catch (Exception ignored) {
            }
            try {
                if (psVenta != null) psVenta.close();
            } catch (Exception ignored) {
            }
            try {
                if (cn != null) cn.close();
            } catch (Exception ignored) {
            }
        }
    }

    private Integer obtenerIdMetodoPago(Connection cn, String nombreMetodo) throws SQLException {

        String sql = "SELECT id_metodo_pago FROM metodos_pago WHERE nombre_metodo = ? LIMIT 1";

        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombreMetodo);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_metodo_pago");
            }
        }

        return null;
    }

    private Integer obtenerOCrearClienteTipo(Connection cn, String tipoCliente) throws SQLException {

        String sqlBuscar = "SELECT id_cliente FROM clientes WHERE nombre = ? AND (apellido = '' OR apellido IS NULL) LIMIT 1";

        try (PreparedStatement psBuscar = cn.prepareStatement(sqlBuscar)) {
            psBuscar.setString(1, tipoCliente);

            ResultSet rs = psBuscar.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_cliente");
            }
        }

        String sqlInsertar = "INSERT INTO clientes(dni, nombre, apellido, telefono, direccion) VALUES(NULL, ?, '', '', '')";

        try (PreparedStatement psInsertar = cn.prepareStatement(sqlInsertar, Statement.RETURN_GENERATED_KEYS)) {
            psInsertar.setString(1, tipoCliente);
            psInsertar.executeUpdate();

            ResultSet keys = psInsertar.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        }

        return null;
    }
}
