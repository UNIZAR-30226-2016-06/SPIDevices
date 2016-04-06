package com.spigirls.spidevices.spidevices;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;

public class InfoProducto extends AppCompatActivity {

    private TextView mNombre;
    private TextView mReferencia;
    private TextView mFabricante;
    private TextView mColor;
    private TextView mPrecio;
    private ImageView mImagen;
    private TextView mDescripcion;

    private String referencia;
    private String url;
    private Bitmap bm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        referencia=getIntent().getExtras().getString("referencia");

        mNombre = (TextView) findViewById(R.id.nombre);
        mReferencia = (TextView) findViewById(R.id.referencia);
        mFabricante = (TextView) findViewById(R.id.fabricante);
        mColor = (TextView) findViewById(R.id.color);
        mPrecio = (TextView) findViewById(R.id.precio);
        mImagen = (ImageView) findViewById(R.id.imagen);
        mDescripcion = (TextView) findViewById(R.id.descripcion);

        recuperarProducto();

        Button eliminarProd = (Button) findViewById(R.id.eliminar_producto);

        eliminarProd.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                deleteProduct();
            }
        });

        Button comprar = (Button) findViewById(R.id.URL);

        comprar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Uri uriUrl = Uri.parse(url);
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });

        Button modificarProd = (Button) findViewById(R.id.modificar_producto);

        modificarProd.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

            }
        });

    }

    /*
    Ejecuta un nuevo hilo que accede a la base de datos para recuperar la información
    del producto que se va a mostrar.
    Si se realiza todo de forma correcta muestra la pantalla del producto sino vuelve
    a la actividad principal.
     */
    private void recuperarProducto(){
        RecProducto p = (RecProducto) new RecProducto(referencia).execute();

        try{
            String[] elementos=p.get();
            if(elementos==null){

            }
            if(elementos==null || elementos[0]==null){
                AlertDialog alertDialog;
                alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("El producto no existe.");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        irMain();
                    }
                });
                alertDialog.show();
            }
            else{
                mNombre.setText(elementos[0]);
                mReferencia.setText(referencia);
                mFabricante.setText(elementos[5]);
                mColor.setText(elementos[3]);
                mPrecio.setText(elementos[2]);
                url=elementos[4];
                mDescripcion.setText(elementos[1]);
                CargarImagen c=(CargarImagen) new CargarImagen(elementos[6]).execute();
                try{
                    bm=c.get();
                    if(bm!=null){
                        mImagen.setAdjustViewBounds(true);
                        mImagen.setImageBitmap(bm);
                    }
                }
                catch(Exception e){

                }
            }
        }catch (InterruptedException e){

        }catch (ExecutionException e){

        }
    }

    /*
    Método que ejecuta un nuevo hilo que accede a la base de datos y borra el producto.
     */
    private void borrar(){
        DelProducto p = (DelProducto) new DelProducto(referencia).execute();

        try{
            boolean c=p.get();
            if(!c){
                AlertDialog alertDialog;
                alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("El producto que ha intentado borrar no " +
                        "existe.");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        irMain();
                    }
                });
                alertDialog.show();
            }
            else{
               irMain();
            }
        }catch (InterruptedException e){

        }catch (ExecutionException e){

        }
    }

    /*
    Método que inicia una nueva actividad principal.
     */
    private void irMain(){
        Intent i =  new Intent(this, MainActivity.class);
        startActivity(i);
    }

    /*
    Método de seguridad que muestra un mensaje para confirmar que desea borrar el producto
    definitivamente.
     */
    private void deleteProduct(){
        referencia = mReferencia.getText().toString();
        AlertDialog.Builder alertDialog;
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("¿Está seguro?");
        alertDialog.setMessage("Va a borrar el producto con referencia: " + referencia + ". " +
                "Pulse confirmar para eliminarlo definitivamente.");
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                borrar();
            }
        });
        alertDialog.setNegativeButton("Atrás", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    /*
    Clase que ejecuta un hilo para cargar una imagen de internet
     */
    public class CargarImagen extends AsyncTask<Void,Void,Bitmap>{

        private String url;

        CargarImagen(String url){
            this.url=url;
        }

        @Override
        protected Bitmap doInBackground(Void ... params) {
            try {

                URL imageUrl = new URL(url);
                HttpURLConnection con = (HttpURLConnection)imageUrl.openConnection();
                con.connect();
                InputStream is = new BufferedInputStream(con.getInputStream());
                Bitmap bm = BitmapFactory.decodeStream(is);
                is.close();
                return bm;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /*
    Clase que ejecuta un hilo para recuperar los campos de un producto de la base de datos.
     */
    public class RecProducto extends AsyncTask<Void,Void,String[]>{

        private final String referencia2;

        RecProducto(String ref){
            referencia2=ref;
        }

        @Override
        protected String[] doInBackground(Void ... params) {
            try{
                BDConnection bd = BDConnection.getInstance();
                Connection connection = bd.getConnection();
                Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery("SELECT Nombre, Descripcion, Precio," +
                        " Color, URL, Fabricante, Foto from Producto WHERE " +
                        "Referencia = '" + referencia2 + "'");
                String[] array = new String[8];
                while(rs.next()){
                    array[0] = rs.getString("Nombre");
                    array[1] = rs.getString("Descripcion");
                    array[2] = rs.getString("Precio");
                    array[3] = rs.getString("Color");
                    array[4] = rs.getString("URL");
                    array[5] = rs.getString("Fabricante");
                    array[6] = rs.getString("Foto");
                }
                return array;
            }
            catch(Exception e){
                return null;
            }
        }
    }

    /*
    Clase que ejecuta un hilo que borra un producto de la base de datos.
     */
    public class DelProducto extends AsyncTask<Void,Void,Boolean> {

        private final String referencia2;

        DelProducto(String ref){
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

    @Override
    protected void onResume () {

        super.onResume();
        mImagen.setAdjustViewBounds(true);
        mImagen.setImageBitmap(bm);
    }

}
