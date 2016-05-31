package com.spigirls.spidevices.producto;

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

import com.spigirls.spidevices.database.AccesoBD;
import com.spigirls.spidevices.database.BDConnection;
import com.spigirls.spidevices.spidevices.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;

public class AnadirProducto extends AppCompatActivity {

    private EditText mNombre;
    private EditText mReferencia;
    private EditText mDescripcion;
    private EditText mImagen;
    private EditText mColor;
    private EditText mPrecio;
    private EditText mUrl;

    private String mTipo="Móvil";
    private String mFabricante ="";
    private String nombre;
    private String referencia;
    private String descripcion;
    private String imagen;
    private String color;
    private String precio;
    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir_producto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNombre = (EditText) findViewById(R.id.nombre);
        mReferencia = (EditText) findViewById(R.id.referencia);
        mDescripcion = (EditText) findViewById(R.id.descripcion);
        mImagen = (EditText) findViewById(R.id.imagen);
        mColor = (EditText) findViewById(R.id.color);
        mPrecio = (EditText) findViewById(R.id.precio);
        mUrl = (EditText)findViewById(R.id.c);

        //Se da nombre al boton
        Button añadir_button = (Button) findViewById(R.id.añadir_button);

        //Se añade funcionalidad al boton
        añadir_button.setOnClickListener(new View.OnClickListener() {

        	//Cuando se pulsa el boton de añadir producto se ejecuta el metodo
        	// setProducto()
            public void onClick(View view) {
                setProducto();
            }

        });

        //Objeto de tipo Spinner con los valores "Móvil" o "Tablet" para seleccionar uno de ellos
        //segun el producto que se vaya a añadir a la BD
        Spinner spinner = (Spinner) findViewById(R.id.tipo);
        String[] valores = {"Móvil","Tablet"};
        //Se cambian los valores del spinner por los nombrados
        spinner.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, valores));
        //Cuando se pulsa el spinner del tipo
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

        	/**
        	 * Metodo que se ejecuta al seleccionar un elemento del spinner.
        	 * Cambia el atributo mTipo por el valor seleccionado.
        	 */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                mTipo = (String) adapterView.getItemAtPosition(position);
            }

            /**
             * Metodo que se ejecuta cuando no se pulsa ningun elemento del spinner
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Se crea un objeto ObtenerFabricante y se ejecuta el metodo a él asociado
        ObtenerFabricante obF = (ObtenerFabricante) new ObtenerFabricante().execute();
        //Tabla para guardar los fabricantes
        String[] fab=null;
        try{
            fab=obF.get();
        }catch (InterruptedException e){

        }catch (ExecutionException e){

        }
        //Objeto Spinner de fabricantes
        Spinner spinnerfab = (Spinner) findViewById(R.id.fabs);
        //Se cambian los valores del spinner por el nombre de los fabricante que haya en la BD
        //en ese momento
        spinnerfab.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, fab));
        //Cuando se pulsa el spinner de los fabricantes
        spinnerfab.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

        	/**
        	 * Metodo que se ejecuta al seleccionar un fabricante concreto del spinner
        	 */
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                mFabricante = (String) adapterView.getItemAtPosition(position);
            }

            /**
             * Metodo que se ejecuta si no se selecciona ningun fabricante
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    /**
     * Metodo que se ejecuta cuando se pulsa el boton de añadir producto y que añade
     * el producto a la BD
     */
    private void setProducto(){
        nombre = mNombre.getText().toString();
        referencia = mReferencia.getText().toString();
        color= mColor.getText().toString();
        precio = mPrecio.getText().toString();
        imagen = mImagen.getText().toString();
        descripcion = mDescripcion.getText().toString();
        url = mUrl.getText().toString();

        //Se crea un objeto de tipo Producto y se ejecuta el metodo doInBackground asociadoa éste
        Producto p = (Producto) new Producto(nombre, referencia, color, precio,
                imagen, descripcion, url, mTipo, mFabricante).execute();

        try{
            boolean c=p.get();// c = resultado de doInBackgroun
            if(!c){//Si hay un error al insertar el fabricante (c=false)
                if(nombre.compareTo("")==0){//Si el nombre es vacio, indica que el campo es imprescindible
                    mNombre.setError(getString(R.string.error_field_required));
                    mNombre.requestFocus();
                }else if(referencia.compareTo("")==0){//Si la referencia es vacia, indica que el campo es imprescindible
                    mReferencia.setError(getString(R.string.error_field_required));
                    mReferencia.requestFocus();
                }else if(url .compareTo("")==0){//Si la url es vacia, indica que el campo es imprescindible
                    mUrl.setError(getString(R.string.error_field_required));
                    mUrl.requestFocus();
                }else if(precio.compareTo("")==0){//Si el precio es vacio, indica que el campo es imprescindible
                    mPrecio.setError(getString(R.string.error_field_required));
                    mPrecio.requestFocus();
                }
                else{
                	/*Si no hay  campos vacios se muestra un mensaje de error informando de que el
                	 * objeto ya ha sido añadido anteriormente, por esta razon no se puede volver a
                	 * insertar
                	 */
                    AlertDialog alertDialog;
                    alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Error");
                    alertDialog.setMessage("El producto que ha intentado insertar ya " +
                            "existe en la base de datos.");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }
            }
            else{//Si no hay problemas (c == true)
                Intent intent = new Intent(this, AccesoBD.class);
                intent.putExtra("Orden", "Nombre");
                startActivity(intent);
            }



        }catch (InterruptedException e){

        }catch (ExecutionException e){

        }


    }

    /**
     * Clase auxiliar Producto que permite ejecutar el metodo que permite la inclusion
     * de un objeto Producto en la base de datos en segundo plano
     */
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
        private boolean c;
        
        /**
         * Metodo constructor de objetos del tipo Producto
         */
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

        /**
         * Metodo que añade un objeto Producto en la base de datos.
         * Devuelve true en caso de que se haya ejecutado con exito y false en caso contrario.
         */
        @Override
        protected Boolean doInBackground(Void ... params) {
            if(nombre2.compareTo("")==0 || referencia2.compareTo("")==0 || precio2.compareTo("")==0 || url2.compareTo("")==0){
            	//Si el nombre del producto, la referencia, el precio o la url de la pagina en la que se puede comprar no
            	// tiene valor, se devuelve false
                return false;
            }else{//En caso contrario se procede a añadirlo
                try{
                	//Se ectablece conexion con la base de datos
                    BDConnection bd = BDConnection.getInstance();
                    Connection connection = bd.getConnection();
                    Statement st = connection.createStatement();
                    //Se selecciona de la BD el fabricante indicado en el objeto Spinner
                    ResultSet rs = st.executeQuery("SELECT CIF from Fabricante where Nombre='"+fabricante2+"'");
                    String cif="";
                    while(rs.next()){//Mientras que en el resultado del SELECT haya campos por mirar
                    	//Se obtiene el CIF del fabricante
                        cif=rs.getString("CIF");
                    }
                    //Se realiza la ejecucion de la insercion del producto en la BD con todos sus datos
                    int i = st.executeUpdate("INSERT INTO Producto (Referencia, Nombre, " +
                            " Descripcion, Precio, Color, Fabricante, Administrador, Foto," +
                            " URL, Tipo) VALUES ('" + referencia2 + "','" + nombre2 + "'"+
                            ",'" + descripcion2 + "','" + precio2 + "','" + color2 + "','" + cif +
                            "','1234567','"+imagen2+"','"+url2+"','"+tipo2+"')");

                    return (i>0);
                }
                catch(Exception e){
                    return false;
                }
            }
        }
    }

    /**
     * Clase auxiliar ObtenerFabricante que sirve para obtener el nombre de los fabricantes
     */
    public class ObtenerFabricante extends AsyncTask<Void,Void,String[]>{

        ObtenerFabricante(){
        }

        /**
         *  Metodo que se ejecuta en segundo plano y que devuelve una tabla de objeto de 
         *  tipo String
         */
        @Override
        protected String[] doInBackground(Void ... params) {
            try{
            	//Se establece conexion con la BD
                BDConnection bd = BDConnection.getInstance();
                Connection connection = bd.getConnection();
                Statement st = connection.createStatement();
                //Se ejecuta la query en la que se selecciona el nombre de los fabricantes
                ResultSet rs = st.executeQuery("SELECT Nombre FROM Fabricante");
                //Tabla en la que se guarda el resultado de la query
                String[] fabs = new String[100];
                int i = 0;
                while(rs.next()){
                	//Bucle para guardar todos los nombres del resultado del select
                    fabs[i] = rs.getString("Nombre");
                    i++;
                }
                // s = fabs
                String[] s = new String[i];
                for(i = 0; i<s.length; i++){//Bucle en el que se copian las tablas
                    s[i]=fabs[i];
                }
                return s;
            }
            catch(Exception e){
            }
            return null;
        }
    }
}