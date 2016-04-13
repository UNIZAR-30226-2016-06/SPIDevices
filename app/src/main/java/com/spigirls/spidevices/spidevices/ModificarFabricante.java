package com.spigirls.spidevices.spidevices;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;

public class ModificarFabricante extends AppCompatActivity{

    private String cif;
    private EditText cif1;
    private String nombre;
    private EditText nombre1;
    private String url;
    private EditText url1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir_fabricante);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cif1 = (EditText) findViewById(R.id.cif);
        nombre1 = (EditText) findViewById(R.id.nombref);
        url1 = (EditText) findViewById(R.id.urlf);

        Button modificarFab = (Button) findViewById(R.id.anadirfab);
        modificarFab.setText("MODIFICAR");

        modificarFab.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                updateFab();
            }

        });

    }
    private void updateFab(){
        cif=cif1.getText().toString();
        nombre=nombre1.getText().toString();
        url=url1.getText().toString();

        Fabricante f = (Fabricante) new Fabricante(nombre, cif, url).execute();

        try{
            boolean c=f.get();
            if(!c){
                AlertDialog alertDialog;
                alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("El fabricante que ha intentado no " +
                        "exite en la base de datos.");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
            else{
                Intent i =  new Intent(this, MainActivity.class);
                startActivity(i);
            }
        }catch (InterruptedException e){

        }catch (ExecutionException e){

        }
    }
    public class Fabricante extends AsyncTask<Void,Void,Boolean> {

        private final String nNombre;
        private final String cif;
        private final String url;

        Fabricante(String nom, String cif, String url){
            this.nNombre =nom;
            this.cif=cif;
            this.url=url;
        }

        @Override
        protected Boolean doInBackground(Void ... params) {
            try{
                BDConnection bd = BDConnection.getInstance();
                Connection connection = bd.getConnection();
                Statement st = connection.createStatement();
                int i = st.executeUpdate("UPDATE Fabricante set Nombre='" + nNombre + "', URL='" + url + "' " +
                        "where CIF='" + cif + "'");
                return (i>0);
            }
            catch(Exception e){
                return false;
            }
        }
    }
}
