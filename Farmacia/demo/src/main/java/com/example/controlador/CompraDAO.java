package com.example.controlador;

import com.example.modelo.DetalleCompraVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Date;
import java.util.List;

public class CompraDAO {

    public int guardarCompra(int idProveedor, double total, Date fechaCompra) {

        int idCompra = 0;

        String sql = "INSERT INTO compras(id_proveedor, fecha_compra, id_usuario, total) VALUES(?, ?, NULL, ?)";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, idProveedor);
            ps.setDate(2, fechaCompra);
            ps.setDouble(3, total);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                idCompra = rs.getInt(1);
            }

        } catch (Exception e) {
            System.out.println("Error guardar compra " + e);
        }

        return idCompra;
    }

    public boolean guardarDetalle(int idCompra, List<DetalleCompraVO> lista) {

        String sql = "INSERT INTO detalle_compra(id_compra,id_producto,cantidad,costo_unitario,subtotal) VALUES(?,?,?,?,?)";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            for (DetalleCompraVO d : lista) {

                ps.setInt(1, idCompra);
                ps.setInt(2, d.getIdProducto());
                ps.setInt(3, d.getCantidad());
                ps.setDouble(4, d.getCostoUnitario());
                ps.setDouble(5, d.getSubtotal());

                ps.executeUpdate();

                actualizarStock(d.getIdProducto(), d.getCantidad());
            }

            return true;

        } catch (Exception e) {
            System.out.println("Error guardar detalle compra " + e);
            return false;
        }
    }

    public void actualizarStock(int idProducto, int cantidad) {

        String sql = "UPDATE productos SET stock = stock + ? WHERE id_producto=?";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, cantidad);
            ps.setInt(2, idProducto);

            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error actualizar stock compra " + e);
        }
    }
}
