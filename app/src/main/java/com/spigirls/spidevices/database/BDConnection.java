package com.spigirls.spidevices.database;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Clase BDConnection que implementa los metodos necesarios para conectar con la base de datos
 */
public class BDConnection {

    private static BDConnection instance = null;
    private Connection connection = null;
    private final static String URL = "jdbc:mysql://db4free.net:3306/spidevices";
    private final static String USER = "";
    private final static String PASS = "";

    /**
     * Constructor de la clase BDConnection
     */
    private BDConnection(){}

    /**
     * Metodo que crea una nueva instancia de DBConnection si no existe ninguna, o devuelve la que a estaba creada
     * @return instance, instacia de la clase BDConnection
     */
    public static BDConnection getInstance(){
        if (instance == null)
            instance = new BDConnection();
        return instance;
    }

    /**
     * Metodo que crea una nueva conexion a una base de datos en caso de que no haya ninguna estabecida anteriormente
     * o devuelve la conexion que ya habia sido establecida
     * @return connection, conexion a la base de datos
     */
    public Connection getConnection(){
        if (connection == null)
            connection = conectar();
        return connection;
    }

    /**
     * Metodo que conecta con la base de datos
     * @return conn, conexion con la base de datos
     */
    private Connection conectar(){
        Connection conn = null;
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(URL, USER, PASS);
        }catch(Exception e){
            e.printStackTrace();
        }
        return conn;
    }


}
