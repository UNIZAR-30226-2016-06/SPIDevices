package com.spigirls.spidevices.spidevices;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

}
