package com.spigirls.spidevices.spidevices;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;

public class BorrarProducto extends AppCompatActivity {

    private EditText referencia;
    private String ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrar_producto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        referencia = (EditText) findViewById(R.id.referencia);

        Button eliminarProd = (Button) findViewById(R.id.eliminarPr);

        eliminarProd.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                deleteProduct();
            }
        });
    }
    private void deleteProduct(){
        ref = referencia.getText().toString();

        Producto p = (Producto) new Producto(null, ref, null, null,
                null, null, null, null, null).execute();

        try{
            boolean c=p.get();
            if(!c){
                AlertDialog alertDialog;
                alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("El producto que ha intentado borrar ya " +
                        "esta borrado.");
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


    public class Producto extends AsyncTask<Void,Void,Boolean> {

        private final String nombre2;
        private final String referencia2;
        private final String descripcion2;
        private final String imagen2;
        private final String color2;
        private final String precio2;
        private final String tipo2;
        private final String fabricante2;
        private final String url2;

        Producto(String nom, String ref, String col, String pre, String img,
                 String des, String dir, String tip, String fab){
            nombre2=nom;
            referencia2=ref;
            descripcion2 =des;
            imagen2=img;
            color2=col;
            precio2=pre;
            tipo2=tip;
            fabricante2=fab;
            url2=dir;
        }

        @Override
        protected Boolean doInBackground(Void ... params) {
            try{
                BDConnection bd = BDConnection.getInstance();
                Connection connection = bd.getConnection();
                Statement st = connection.createStatement();
                int i = st.executeUpdate("DELETE from Producto WHERE Referencia = '" + referencia2 +"'");
                return (i>0);
            }
            catch(Exception e){
                return false;
            }
        }
    }
}