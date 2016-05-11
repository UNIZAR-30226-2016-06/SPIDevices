package com.spigirls.spidevices.producto;

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

import com.spigirls.spidevices.database.AccesoBD;
import com.spigirls.spidevices.database.BDConnection;
import com.spigirls.spidevices.spidevices.R;

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
        //Cambio de nombre al boton
        modificarFab.setText("MODIFICAR");

        //Adicion de funcionalidad al boton
        modificarFab.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                updateFab();
            }

        });

    }
    
    /**
     * Metodo que se ejecuta al pulsar el boton para modificar un fabricante
     */
    private void updateFab(){
        cif=cif1.getText().toString();
        nombre=nombre1.getText().toString();
        url=url1.getText().toString();

        //Se crea un objeto Fabricante y se ejecuta su metodo doInBackground asociado 
        Fabricante f = (Fabricante) new Fabricante(nombre, cif, url).execute();

        try{
            boolean c=f.get();//c es el resultado del metodo doInBackground que realiza
            				  // la modificacion del fabricante
            if(!c){//c==false
                AlertDialog alertDialog;
                alertDialog = new AlertDialog.Builder(this).create();
                //Se informa de que ha habido un error
                alertDialog.setTitle("Error");
                alertDialog.setMessage("El fabricante que ha intentado modificar no " +
                        "exite en la base de datos.");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
            else{//c==true
                Intent intent = new Intent(this, AccesoBD.class);
                intent.putExtra("Orden", "Nombre");
                startActivity(intent);
            }
        }catch (InterruptedException e){

        }catch (ExecutionException e){

        }
    }
    
    /**
     * Clase auxiliar necesaria para la modificacion de fabricantes
     */
    public class Fabricante extends AsyncTask<Void,Void,Boolean> {

        private final String nNombre;
        private final String cif;
        private final String url;

        /**
         * Metodo constructor de objetos tipo Fabricante
         */
        Fabricante(String nom, String cif, String url){
            this.nNombre =nom;
            this.cif=cif;
            this.url=url;
        }
        
        /**
         *  Metodo que ejecuta en segundo plano la modificacion de un fabricante.
         *  Devuelve true si la modificacion se realiza de manera correcta y false
         *  en caso contrario
         */
        @Override
        protected Boolean doInBackground(Void ... params) {
            try{
            	//Se establece conexion con la base de datos
                BDConnection bd = BDConnection.getInstance();
                Connection connection = bd.getConnection();
                Statement st = connection.createStatement();
                //Se ejecuta la query que hace que se actualice un fabricante
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
