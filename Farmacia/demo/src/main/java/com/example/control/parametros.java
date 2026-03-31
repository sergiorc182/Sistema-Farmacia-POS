package com.example.control;

public class parametros {
    static final String DRIVER = "jdbc:mysql";  // Cambiado de "jdbc:mysql" a "mysql"
    static final String IP = "127.0.0.1";
    static final String PUERTO = "3306";
    static final String BASE = "farmacia_db";
    static final String USER = "root";
    static final String PASSWORD = "";
    
    // URL completa por si acaso
    static final String URL = "jdbc:mysql://" + IP + ":" + PUERTO + "/" + BASE;
}

