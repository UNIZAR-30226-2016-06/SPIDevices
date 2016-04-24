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

public class AnadirFabricante extends AppCompatActivity {

    private EditText mNombre;
    private String nombre2;
    private String cif2;
    private String url2;
    private EditText mCif;
    private EditText mRrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir_fabricante);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNombre = (EditText) findViewById(R.id.nombref);
        mCif = (EditText) findViewById(R.id.cif);
        mRrl = (EditText) findViewById(R.id.urlf);

        Button añadirfab = (Button) findViewById(R.id.anadirfab);

        añadirfab.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setFabricante();
            }

        });

    }

    private void setFabricante(){

        nombre2 = mNombre.getText().toString();
        cif2 = mCif.getText().toString();
        url2 = mRrl.getText().toString();

        Fabricante f = (Fabricante) new Fabricante(nombre2, cif2, url2).execute();

        try{
            boolean c=f.get();
            if(!c){
                AlertDialog alertDialog;
                alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("El fabricante que ha intentado insertar ya " +
                        "existe en la base de datos.");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
            else{
                finish();
            }
        }catch (InterruptedException e){

        }catch (ExecutionException e){

        }
    }

    public class Fabricante extends AsyncTask<Void,Void,Boolean> {

        private final String nombre;
        private final String cif;
        private final String url;

        Fabricante(String nom, String cif, String url){
            nombre=nom;
            this.cif=cif;
            this.url=url;
        }

        @Override
        protected Boolean doInBackground(Void ... params) {
            try{
                BDConnection bd = BDConnection.getInstance();
                Connection connection = bd.getConnection();
                Statement st = connection.createStatement();
                int i = st.executeUpdate("INSERT into Fabricante (CIF, Nombre, URL) " +
                        "values ('"+cif+"','"+nombre+"','"+url+"')");
                return (i>0);
            }
            catch(Exception e){
                return false;
            }
        }

    }

}
