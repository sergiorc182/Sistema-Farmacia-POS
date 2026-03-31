package com.example.controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConectarBase {

    private static final String URL = "jdbc:mysql://localhost:3306/farmacia_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection conectar() {

        Connection conn = null;

        try {

            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexion exitosa a la base de datos");

        } catch (SQLException e) {

            System.out.println("Error de conexion: " + e.getMessage());

        }

        return conn;
    }
}