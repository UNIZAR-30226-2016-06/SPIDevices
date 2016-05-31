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

public class BorrarFabricante extends AppCompatActivity {

    private String cif;
    private EditText cif1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrar_fabricante);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cif1 = (EditText) findViewById(R.id.cif);

        //Se pone nombre al boton
        Button eliminarFab = (Button) findViewById(R.id.eliminarFab);

        //Se da la funcionalidad de borrar fabricante al boton anterior
        eliminarFab.setOnClickListener(new View.OnClickListener() {

        	//Cuando se pulse el boton se llamara al metodo deleteFab()
            public void onClick(View view) {
                deleteFab();
            }

        });

    }

    /**
     * Metodo que se ejecuta al pulsar el boton de Borrar fabricante 
     */
    private void deleteFab(){
    	
        cif=cif1.getText().toString();

        //Se crea un objeto de tipo Fabricante y se ejecuta el metodo doInBackground asociado
        Fabricante f = (Fabricante) new Fabricante(cif).execute();

        try{
            boolean c=f.get();//c es el resultado del metodo doInBackground asociado a f
            if(!c){//Si hay algun error al borrar el fabricante
                AlertDialog alertDialog;
                alertDialog = new AlertDialog.Builder(this).create();
                //Se muestra un mensaje indicando lo sucedido
                alertDialog.setTitle("Error");
                alertDialog.setMessage("El fabricante que ha intentado borrar ya " +
                        "no esta en la base de datos.");
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
     * Clase auxiliar Fabricante que permite realizar la operaci�n de borrado de un fabricante
     */
    public class Fabricante extends AsyncTask<Void,Void,Boolean> {

        private final String cif;

        /**
         * Metodo constructor de los objetos Fabricante
         */
        Fabricante(String cif){
            this.cif=cif;
        }


        /**
         * Metodo que  borra el Fabricante en segundo plano.
         * Devuelve true en caso de que el borrado haya sido correcto y false en caso contrario.
         */
        @Override
        protected Boolean doInBackground(Void ... params) {
            try{
            	//Se establece conexion con la base de datos
                BDConnection bd = BDConnection.getInstance();
                Connection connection = bd.getConnection();
                Statement st = connection.createStatement();
                //Se ejecuta la query pertinente para borrar al fabricante de la BD
                int i = st.executeUpdate("DELETE from Fabricante " +
                        "where CIF='"+cif+"'");
                return (i>0);
            }
            catch(Exception e){
                return true;
            }
        }


    }
}


