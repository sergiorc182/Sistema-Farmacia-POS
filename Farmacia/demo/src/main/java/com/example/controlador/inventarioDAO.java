package com.example.controlador;

import com.example.modelo.inventarioVO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class inventarioDAO {

    public List<inventarioVO> listarProductos() {

        List<inventarioVO> lista = new ArrayList<>();

        String sql = "SELECT p.id_producto, p.nombre, " +
                "COALESCE(c.nombre_categoria, 'Sin categoria') AS nombre_categoria, " +
                "p.precio_compra, p.precio_venta, p.stock, p.fecha_vencimiento " +
                "FROM productos p " +
                "LEFT JOIN categorias c ON p.id_categoria = c.id_categoria";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearProducto(rs));
            }

        } catch (Exception e) {
            System.out.println("Error listar productos: " + e);
        }

        return lista;
    }

    public List<inventarioVO> buscarProducto(String texto) {

        List<inventarioVO> lista = new ArrayList<>();

        String sql = "SELECT p.id_producto, p.nombre, " +
                "COALESCE(c.nombre_categoria, 'Sin categoria') AS nombre_categoria, " +
                "p.precio_compra, p.precio_venta, p.stock, p.fecha_vencimiento " +
                "FROM productos p " +
                "LEFT JOIN categorias c ON p.id_categoria = c.id_categoria " +
                "WHERE p.nombre LIKE ? OR COALESCE(c.nombre_categoria, '') LIKE ?";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, "%" + texto + "%");
            ps.setString(2, "%" + texto + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearProducto(rs));
            }

        } catch (Exception e) {
            System.out.println("Error buscar producto: " + e);
        }

        return lista;
    }

    public void descontarStock(int idProducto, int cantidad) {

        String sql = "UPDATE productos SET stock = stock - ? WHERE id_producto=?";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, cantidad);
            ps.setInt(2, idProducto);

            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error actualizar stock " + e);
        }
    }

    public boolean editarProducto(inventarioVO p) {

        String sql = "UPDATE productos SET nombre=?, id_categoria=?, precio_compra=?, " +
                "precio_venta=?, stock=?, fecha_vencimiento=? WHERE id_producto=?";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            int idCategoria = obtenerOCrearCategoria(cn, p.getCategoria());

            ps.setString(1, p.getNombre());
            if (idCategoria > 0) {
                ps.setInt(2, idCategoria);
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setDouble(3, p.getPrecioCompra());
            ps.setDouble(4, p.getPrecioVenta());
            ps.setInt(5, p.getStock());
            ps.setDate(6, new java.sql.Date(p.getVencimiento().getTime()));
            ps.setInt(7, p.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error editar producto: " + e);
            return false;
        }
    }

    public boolean eliminarProducto(int id) {

        String sql = "DELETE FROM productos WHERE id_producto=?";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error eliminar producto: " + e);
            return false;
        }
    }

    public boolean agregarProducto(inventarioVO p) {

        String sql = "INSERT INTO productos " +
                "(nombre, id_categoria, precio_compra, precio_venta, stock, fecha_vencimiento) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            int idCategoria = obtenerOCrearCategoria(cn, p.getCategoria());

            ps.setString(1, p.getNombre());
            if (idCategoria > 0) {
                ps.setInt(2, idCategoria);
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setDouble(3, p.getPrecioCompra());
            ps.setDouble(4, p.getPrecioVenta());
            ps.setInt(5, p.getStock());
            ps.setDate(6, new java.sql.Date(p.getVencimiento().getTime()));

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error agregar producto: " + e);
            return false;
        }
    }

    public List<inventarioVO> productosPorVencer() {

        List<inventarioVO> lista = new ArrayList<>();

        String sql = "SELECT nombre, fecha_vencimiento FROM productos " +
                "WHERE fecha_vencimiento <= DATE_ADD(NOW(), INTERVAL 30 DAY)";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                inventarioVO p = new inventarioVO();

                p.setNombre(rs.getString(1));
                p.setVencimiento(rs.getDate(2));

                lista.add(p);
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return lista;
    }

    public inventarioVO obtenerProductoPorNombreExacto(String nombre) {

        String sql = "SELECT p.id_producto, p.nombre, " +
                "COALESCE(c.nombre_categoria, 'Sin categoria') AS nombre_categoria, " +
                "p.precio_compra, p.precio_venta, p.stock, p.fecha_vencimiento " +
                "FROM productos p " +
                "LEFT JOIN categorias c ON p.id_categoria = c.id_categoria " +
                "WHERE p.nombre = ? LIMIT 1";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, nombre);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearProducto(rs);
            }

        } catch (Exception e) {
            System.out.println("Error obtener producto exacto: " + e);
        }

        return null;
    }

    public boolean agregarProductoDesdeCompra(String nombreProducto, int idProveedor, double costoUnitario) {

        String sql = "INSERT INTO productos " +
                "(nombre, id_categoria, precio_compra, precio_venta, stock, fecha_vencimiento, id_proveedor) " +
                "VALUES (?, ?, ?, ?, 0, NULL, ?)";

        try (Connection cn = ConectarBase.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            int idCategoria = obtenerOCrearCategoria(cn, "General");

            ps.setString(1, nombreProducto.trim());
            if (idCategoria > 0) {
                ps.setInt(2, idCategoria);
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setDouble(3, costoUnitario);
            ps.setDouble(4, costoUnitario);
            ps.setInt(5, idProveedor);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error agregar producto desde compra: " + e);
            return false;
        }
    }

    private inventarioVO mapearProducto(ResultSet rs) throws SQLException {

        inventarioVO p = new inventarioVO();

        p.setId(rs.getInt("id_producto"));
        p.setNombre(rs.getString("nombre"));
        p.setCategoria(rs.getString("nombre_categoria"));
        p.setPrecioCompra(rs.getDouble("precio_compra"));
        p.setPrecioVenta(rs.getDouble("precio_venta"));
        p.setStock(rs.getInt("stock"));
        p.setVencimiento(rs.getDate("fecha_vencimiento"));

        return p;
    }

    private int obtenerOCrearCategoria(Connection cn, String categoria) throws SQLException {

        if (categoria == null || categoria.trim().isEmpty()) {
            return 0;
        }

        String nombreCategoria = categoria.trim();

        String sqlBuscar = "SELECT id_categoria FROM categorias WHERE nombre_categoria = ? LIMIT 1";

        try (PreparedStatement psBuscar = cn.prepareStatement(sqlBuscar)) {
            psBuscar.setString(1, nombreCategoria);

            ResultSet rs = psBuscar.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_categoria");
            }
        }

        String sqlInsertar = "INSERT INTO categorias(nombre_categoria) VALUES(?)";

        try (PreparedStatement psInsertar = cn.prepareStatement(sqlInsertar, Statement.RETURN_GENERATED_KEYS)) {
            psInsertar.setString(1, nombreCategoria);
            psInsertar.executeUpdate();

            ResultSet keys = psInsertar.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        }

        return 0;
    }
}
