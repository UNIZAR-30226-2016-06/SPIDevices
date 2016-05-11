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
import com.spigirls.spidevices.spidevices.ListaProductos;
import com.spigirls.spidevices.spidevices.R;

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

        Button eliminarProd = (Button) findViewById(R.id.eliminarPr);//boton de eliminar

        //Se da la funcionalidad al boton de borrar
        eliminarProd.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                deleteProduct();
            }
        });
    }
    
    /**
     * Metodo que se ejecutara cuando se pulse el boton de eliminar producto
     */
    private void deleteProduct(){
    	
        ref = referencia.getText().toString();

        //Se crea un producto con la referencia del producto que se quiere borrar y se ejecuta
        // el metodo en doInBackground necesario para eliminar el producto
        Producto p = (Producto) new Producto(ref).execute();

        try{
            boolean c=p.get();//c resultado del metodo doInBackground
            if(!c){//Al borrar el producto se ha producido un error y no se ha podido
            	   // completar con exito la operacion
                AlertDialog alertDialog;
                alertDialog = new AlertDialog.Builder(this).create();
                //Se muestra un mensaje informativo de error
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
     * Clase auxiliar Producto que permite realizar la operación de borrado de un producto
     */
    public class Producto extends AsyncTask<Void,Void,Boolean> {

        private final String referencia2;

        /**
         * Metodo constructor de los objetos de tipo Producto
         */
        Producto(String ref){
            referencia2=ref;
        }

        /**
         * Metodo que  borra el Producto en segundo plano.
         * Devuelve true en caso de que el borrado haya sido correcto y false en caso contrario.
         */
        @Override
        protected Boolean doInBackground(Void ... params) {
            try{
            	//Se crea una conexion con la BD
                BDConnection bd = BDConnection.getInstance();
                Connection connection = bd.getConnection();
                Statement st = connection.createStatement();
                //Se ejecuta la query encargada de borrar el producto
                int i = st.executeUpdate("DELETE from Producto WHERE Referencia = '" + referencia2 +"'");
                return (i>0);
            }
            catch(Exception e){
                return false;
            }
        }
    }
}
