package com.spigirls.spidevices.spidevices;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    private BeanProducto producto;
    private String url;
    private Bitmap bm;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        producto = (BeanProducto)getIntent().getSerializableExtra("producto");

        mNombre = (TextView) findViewById(R.id.nombre);
        mReferencia = (TextView) findViewById(R.id.referencia);
        mFabricante = (TextView) findViewById(R.id.fabricante);
        mColor = (TextView) findViewById(R.id.color);
        mPrecio = (TextView) findViewById(R.id.precio);
        mImagen = (ImageView) findViewById(R.id.imagen);
        mDescripcion = (TextView) findViewById(R.id.descripcion);

        populateFields();

        Button comprar = (Button) findViewById(R.id.URL);

        comprar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Uri uriUrl = Uri.parse(url);
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });

        SharedPreferences prefs = this.getSharedPreferences(
                "com.spigirls.spidevices.spidevices", Context.MODE_PRIVATE);

        String email = prefs.getString("emailKey", null);

        Button modificarProd = (Button) findViewById(R.id.modificar_producto);

        Button eliminarProd = (Button) findViewById(R.id.eliminar_producto);

        if(email != null){

            modificarProd.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    modificarProducto();

                }
            });

            eliminarProd.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    deleteProduct();
                }
            });
        }
        else{
            modificarProd.setVisibility(View.INVISIBLE);
            eliminarProd.setVisibility(View.INVISIBLE);
        }

    }

    private void populateFields(){
        mNombre.setText(producto.getNombre());
        mReferencia.setText(producto.getReferencia());
        mFabricante.setText(producto.getmFabricante());
        mColor.setText(producto.getColor());
        mPrecio.setText(producto.getPrecio()+" €");
        url=producto.getUrl();
        mDescripcion.setText(producto.getDescripcion());
        CargarImagen c=(CargarImagen) new CargarImagen(producto.getImagen()).execute();
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

    /*
    Método que ejecuta un nuevo hilo que accede a la base de datos y borra el producto.
     */
    private void borrar(){
        irMain();
        Thread hilo1 = new Thread(new Runnable() {
            @Override
            public void run() {

                DelProducto p = (DelProducto) new DelProducto(producto.getReferencia()).execute();

                try{
                    boolean c=p.get();
                    if(!c){
                        AlertDialog alertDialog;
                        alertDialog = new AlertDialog.Builder(context).create();
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
        });
        hilo1.start();

        irMain();
    }

    private void modificarProducto(){
        Intent i = new Intent(InfoProducto.this,ModificarProducto.class);
        i.putExtra("producto",producto);
        startActivity(i);
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
        AlertDialog.Builder alertDialog;
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("¿Está seguro?");
        alertDialog.setMessage("Va a borrar el producto con referencia: " + producto.getReferencia() + ". " +
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

        SharedPreferences prefs = this.getSharedPreferences(
                "com.spigirls.spidevices.spidevices", Context.MODE_PRIVATE);

        String email = prefs.getString("emailKey", null);

        Button modificarProd = (Button) findViewById(R.id.modificar_producto);

        Button eliminarProd = (Button) findViewById(R.id.eliminar_producto);

        if(email != null){
            modificarProd.setVisibility(View.VISIBLE);
            eliminarProd.setVisibility(View.VISIBLE);

        }
        else{
            modificarProd.setVisibility(View.INVISIBLE);
            eliminarProd.setVisibility(View.INVISIBLE);
        }
    }

}
