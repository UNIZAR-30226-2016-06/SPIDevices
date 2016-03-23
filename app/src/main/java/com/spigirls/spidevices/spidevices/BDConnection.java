package com.spigirls.spidevices.spidevices;

import com.mysql.jdbc.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by YolandaGC on 18/3/16.
 */
public class BDConnection {

    private static BDConnection instance = null;
    private Connection connection = null;
    private final static String URL = "jdbc:mysql://db4free.net:3306/spidevices";
    private final static String USER = "";
    private final static String PASS = "";

    private BDConnection(){}

    public static BDConnection getInstance(){
        if (instance == null)
            instance = new BDConnection();
        return instance;
    }

    public Connection getConnection(){
        if (connection == null)
            connection = conectar();
        return connection;
    }

    private Connection conectar(){
        Connection conn = null;
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(URL, USER, PASS);
        }catch(Exception e){

        }
        return conn;
    }


}
