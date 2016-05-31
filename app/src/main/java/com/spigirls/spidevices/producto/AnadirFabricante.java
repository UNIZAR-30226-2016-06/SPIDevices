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

        //Se pone el nombre correspondiente al boton
        Button añadirfab = (Button) findViewById(R.id.anadirfab);

        //Se da funcionalidad al boton anterior, en este caso sirve
        // para añadir un fabricante
        añadirfab.setOnClickListener(new View.OnClickListener() {

        	//Cuando se pulsa el boton anterior se llama a setFabricante()
            public void onClick(View view) {
                setFabricante();
            }

        });

    }

    /**
     * Metodo que se ejecuta al pulsar el boton de añadir fabricanten y que
     * añade el fabricante a la base de datos 
     */
    private void setFabricante(){

        nombre2 = mNombre.getText().toString();
        cif2 = mCif.getText().toString();
        url2 = mRrl.getText().toString();

        //Se crea un objeto de tipo Fabricante y se ejecuta el metodo doInBackground()
        // asociado a el
        Fabricante f = (Fabricante) new Fabricante(nombre2, cif2, url2).execute();

        try{
            boolean c=f.get();// c = resultado de doInBackground
            if(!c){//Si hay un error al insertar el fabricante (c=false)
                AlertDialog alertDialog;
                alertDialog = new AlertDialog.Builder(this).create();
                //Se muestra un mensaje de error con lo sucedido
                alertDialog.setTitle("Error");
                alertDialog.setMessage("El fabricante que ha intentado insertar no " +
                        "ha podido insertarse en la base de datos.");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
            else{//c==true, se ha introducido el fabricante
                Intent intent = new Intent(this, AccesoBD.class);
                intent.putExtra("Orden", "Nombre");
                startActivity(intent);
            }
        }catch (InterruptedException e){

        }catch (ExecutionException e){

        }
    }

    /**
     * Clase auxiliar Fabricante que permite ejecutar el metodo doInBackground que
     * permite la inclusion de un objeto Fabricante en la base de datos
     */
    public class Fabricante extends AsyncTask<Void,Void,Boolean> {

        private final String nombre;//Nombre del fabricante
        private final String cif;//CIF del fabricante
        private final String url;//URL de la pagina web del fabricante

        /**
         * Metodo constructor de objetos de tipo Fabricante
         */
        Fabricante(String nom, String cif, String url){
            nombre=nom;
            this.cif=cif;
            this.url=url;
        }

        /**
         * Metodo que ejecuta la inlusion de un objeto Fabricante en segundo plano
         * en la base de datos.
         * Devuelve true en caso de que se haya ejecutado con exito y 
         * false en caso contrario.
         */
        @Override
        protected Boolean doInBackground(Void ... params) {
            try{
            	//Se establece la conexion con la base de datos
                BDConnection bd = BDConnection.getInstance();
                Connection connection = bd.getConnection();
                Statement st = connection.createStatement();
                if (cif!="") {//Si el campo CIF no esta vacio
                    if (nombre.equals("")){//Si el campo de nombre no es igual a vacio 
                    	// No se ejecuta la operacion y se devuelve false
                        return false;
                    }
                    else{
                    	//Cuando ni el CIF ni el nombre son vacios se ejecuta la adicion del
                    	//fabricante a la BD
                        int i = st.executeUpdate("INSERT into Fabricante (CIF, Nombre, URL) " +
                                "values ('" + cif + "','" + nombre + "','" + url + "')");
                        return (i > 0);
                    }
                }
                else{//En caso de que el CIF sea vacio no se ejecuta la adicion del fabricante
                    return false;
                }
            }
            catch(Exception e){
                return false;
            }
        }
    }
}