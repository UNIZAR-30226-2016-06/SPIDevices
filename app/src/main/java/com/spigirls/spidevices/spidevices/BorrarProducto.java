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

        Producto p = (Producto) new Producto(ref).execute();

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
                Intent i =  new Intent(this, ListaProductos.class);
                startActivity(i);
            }
        }catch (InterruptedException e){

        }catch (ExecutionException e){

        }
    }


    public class Producto extends AsyncTask<Void,Void,Boolean> {

        private final String referencia2;

        Producto(String ref){
            referencia2=ref;
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
