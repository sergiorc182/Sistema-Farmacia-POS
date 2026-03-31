package com.example.controlador;

import com.example.modelo.CajaVO;

import java.sql.*;

public class CajaDAO {

    public CajaVO obtenerEstadoCaja() {

        CajaVO c = new CajaVO();

        String sql = "SELECT c.id_caja, c.fecha_apertura, c.fecha_cierre, c.monto_inicial, " +
                "u.nombre " +
                "FROM caja c " +
                "LEFT JOIN usuarios u ON c.id_usuario = u.id_usuario " +
                "ORDER BY c.id_caja DESC LIMIT 1";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                c.setIdCaja(rs.getInt("id_caja"));
                c.setMontoInicial(rs.getDouble("monto_inicial"));
                c.setUsuario(rs.getString("nombre"));
                c.setEstado(rs.getTimestamp("fecha_cierre") == null ? "ABIERTA" : "CERRADA");
            } else {
                c.setEstado("CERRADA");
                c.setMontoInicial(0);
                c.setUsuario("-");
            }

        } catch (Exception e) {
            System.out.println("Error estado caja " + e);
        }

        return c;
    }

    public boolean estaCajaAbierta() {

        String sql = "SELECT 1 FROM caja WHERE fecha_cierre IS NULL ORDER BY id_caja DESC LIMIT 1";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next();

        } catch (Exception e) {
            System.out.println("Error validar caja abierta " + e);
            return false;
        }
    }

    public boolean abrirCaja(String nombre, double montoInicial) {

        if (estaCajaAbierta()) {
            return false;
        }

        String sql = "INSERT INTO caja(fecha_apertura, monto_inicial, monto_final, id_usuario) VALUES(NOW(), ?, NULL, ?)";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            Integer idUsuario = obtenerIdUsuarioPorNombre(cn, nombre);
            if (idUsuario == null) {
                return false;
            }

            ps.setDouble(1, montoInicial);
            ps.setInt(2, idUsuario);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error abrir caja " + e);
            return false;
        }
    }

    public boolean cerrarCaja(String nombre) {

        String sql = "UPDATE caja SET fecha_cierre = NOW(), monto_final = ? WHERE id_caja = ?";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            Integer idCaja = obtenerIdCajaAbierta(cn);
            if (idCaja == null) {
                return false;
            }

            double montoFinal = obtenerMontoInicialCaja(cn, idCaja) + ventasEfectivo() - totalEgresosCaja(idCaja);

            ps.setDouble(1, montoFinal);
            ps.setInt(2, idCaja);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error cerrar caja " + e);
            return false;
        }
    }

    public boolean retirarDinero(double monto, String motivo) {

        if (!estaCajaAbierta()) {
            return false;
        }

        String sql = "INSERT INTO movimientos_caja(id_caja, tipo_movimiento, monto, descripcion, fecha_hora) " +
                "VALUES(?, 'egreso', ?, ?, NOW())";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            Integer idCaja = obtenerIdCajaAbierta(cn);
            if (idCaja == null) {
                return false;
            }

            ps.setInt(1, idCaja);
            ps.setDouble(2, monto);
            ps.setString(3, motivo);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error retiro " + e);
            return false;
        }
    }

    public double ventasEfectivo() {
        return totalVentasPorMetodo("Efectivo");
    }

    public double ventasTarjeta() {
        return totalVentasPorMetodo("Tarjeta", "Transferencia");
    }

    private double totalVentasPorMetodo(String... metodos) {

        StringBuilder sql = new StringBuilder(
                "SELECT IFNULL(SUM(v.total),0) " +
                "FROM ventas v " +
                "JOIN metodos_pago mp ON v.id_metodo_pago = mp.id_metodo_pago " +
                "JOIN caja c ON c.id_caja = (SELECT id_caja FROM caja ORDER BY id_caja DESC LIMIT 1) " +
                "WHERE v.fecha_hora >= c.fecha_apertura " +
                "AND (c.fecha_cierre IS NULL OR v.fecha_hora <= c.fecha_cierre) "
        );

        if (metodos.length > 0) {
            sql.append("AND mp.nombre_metodo IN (");
            for (int i = 0; i < metodos.length; i++) {
                if (i > 0) {
                    sql.append(",");
                }
                sql.append("?");
            }
            sql.append(")");
        }

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {

            for (int i = 0; i < metodos.length; i++) {
                ps.setString(i + 1, metodos[i]);
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (Exception e) {
            System.out.println("Error total ventas caja " + e);
        }

        return 0;
    }

    private Integer obtenerIdUsuarioPorNombre(Connection cn, String nombre) {

        String sql = "SELECT id_usuario FROM usuarios WHERE nombre = ? LIMIT 1";

        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_usuario");
            }
        } catch (SQLException e) {
            System.out.println("Error obtener id usuario caja " + e.getMessage());
        }

        return null;
    }

    private Integer obtenerIdCajaAbierta(Connection cn) throws SQLException {

        String sql = "SELECT id_caja FROM caja WHERE fecha_cierre IS NULL ORDER BY id_caja DESC LIMIT 1";

        try (PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("id_caja");
            }
        }

        return null;
    }

    private double obtenerMontoInicialCaja(Connection cn, int idCaja) throws SQLException {

        String sql = "SELECT monto_inicial FROM caja WHERE id_caja = ?";

        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCaja);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("monto_inicial");
            }
        }

        return 0;
    }

    private double totalEgresosCaja(int idCaja) {

        String sql = "SELECT IFNULL(SUM(monto),0) FROM movimientos_caja WHERE id_caja = ? AND tipo_movimiento = 'egreso'";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idCaja);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (Exception e) {
            System.out.println("Error egresos caja " + e);
        }

        return 0;
    }
}
