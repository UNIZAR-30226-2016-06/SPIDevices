package com.spigirls.spidevices.spidevices;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AdminHome extends AppCompatActivity {

    TextView nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nombre = (TextView) findViewById(R.id.hola);

        Button anadir = (Button) findViewById(R.id.añadir_producto);
        anadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anadirProd();
            }
        });

        Button eliminarProd = (Button) findViewById(R.id.eliminar_producto);
        eliminarProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarProd();
            }
        });

        Button anadirFab = (Button) findViewById(R.id.añadir_fabricante);
        anadirFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anadirFab();
            }
        });

        Button eliminarFab = (Button) findViewById(R.id.eliminar_fabricante);
        eliminarFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarFab();
            }
        });

        Button cerraSesion = (Button) findViewById(R.id.button_cerrar_sesion);
        cerraSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cerrarSesion();
            }
        });

        Button iniSesion = (Button) findViewById(R.id.ini_sesion);
        iniSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniSesion();
            }
        });

        Button modificarFab = (Button) findViewById(R.id.modificar_fabricante);
        modificarFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                modificarFab();

            }
        });

        SharedPreferences prefs = this.getSharedPreferences(
                "com.spigirls.spidevices.spidevices", Context.MODE_PRIVATE);

        String email = prefs.getString("emailKey", null);

        if(email == null){
            anadir.setVisibility(View.INVISIBLE);
            eliminarProd.setVisibility(View.INVISIBLE);
            anadirFab.setVisibility(View.INVISIBLE);
            eliminarFab.setVisibility(View.INVISIBLE);
            modificarFab.setVisibility(View.INVISIBLE);
            cerraSesion.setVisibility(View.INVISIBLE);
        }
        else{
            nombre.append(" "+email);
            iniSesion.setVisibility(View.INVISIBLE);
        }

    }

    private void anadirProd(){
        Intent i = new Intent(this, AnadirProducto.class);
        startActivity(i);
    }

    private void anadirFab(){
        Intent i = new Intent(this, AnadirFabricante.class);
        startActivity(i);
    }

    private void eliminarFab(){
        Intent i = new Intent(this, BorrarFabricante.class);
        startActivity(i);
    }

    private void eliminarProd(){
        Intent i = new Intent(this, BorrarProducto.class);
        startActivity(i);
    }

    private void cerrarSesion(){

        SharedPreferences prefs = this.getSharedPreferences(
                "com.spigirls.spidevices.spidevices", Context.MODE_PRIVATE);

        prefs.edit().putString("emailKey", null).apply();

        Intent i = new Intent(this, ListaProductos.class);
        startActivity(i);
    }

    private void iniSesion(){
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    private void modificarFab(){
        Intent i = new Intent(this, ModificarFabricante.class);
        startActivity(i);
    }

}
