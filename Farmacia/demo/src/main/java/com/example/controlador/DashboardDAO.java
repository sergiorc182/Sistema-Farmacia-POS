package com.example.controlador;

import com.example.controlador.ConectarBase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashboardDAO {

    public int totalVentas() {

        int total = 0;

        try {

            Connection conn = ConectarBase.conectar();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM ventas"
            );

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                total = rs.getInt(1);
            }

        } catch (Exception e) {
            System.out.println("Error total ventas " + e);
        }

        return total;
    }


    public int totalClientes() {

        int total = 0;

        try {

            Connection conn = ConectarBase.conectar();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM clientes"
            );

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                total = rs.getInt(1);
            }

        } catch (Exception e) {
            System.out.println("Error total clientes " + e);
        }

        return total;
    }


    public int totalProductos() {

        int total = 0;

        try {

            Connection conn = ConectarBase.conectar();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM productos"
            );

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                total = rs.getInt(1);
            }

        } catch (Exception e) {
            System.out.println("Error total productos " + e);
        }

        return total;
    }


    public int stockBajo() {

        int total = 0;

        try {

            Connection conn = ConectarBase.conectar();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM productos WHERE stock < 10"
            );

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                total = rs.getInt(1);
            }

        } catch (Exception e) {
            System.out.println("Error stock bajo " + e);
        }

        return total;
    }


    public double ventasUltimos7Dias(){

        double total = 0;

        try {

            Connection conn = ConectarBase.conectar();

            PreparedStatement ps = conn.prepareStatement(
                "SELECT SUM(total) FROM ventas WHERE fecha_hora >= DATE_SUB(NOW(), INTERVAL 7 DAY)"
            );

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                total = rs.getDouble(1);
            }

        } catch (Exception e) {
            System.out.println("Error ventas 7 dias " + e);
        }

        return total;
    }

    public double comprasUltimos7Dias() {

        double total = 0;

        try {

            Connection conn = ConectarBase.conectar();

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT SUM(total) FROM compras WHERE fecha_compra >= DATE_SUB(NOW(), INTERVAL 7 DAY)"
            );

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                total = rs.getDouble(1);
            }

        } catch (Exception e) {
            System.out.println("Error compras 7 dias " + e);
        }

        return total;
    }
}
