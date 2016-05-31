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
import com.spigirls.spidevices.producto.BeanProducto;
import com.spigirls.spidevices.spidevices.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;

public class ModificarProducto extends AppCompatActivity{

    private EditText nombre1;
    private String nombre;
    private String referencia;
    //private EditText referencia1;
    private String descripcion;
    private EditText descripcion1;
    private String imagen;
    private EditText imagen1;
    private String color;
    private EditText color1;
    private String precio;
    private EditText precio1;
    private String url;
    private EditText url1;
    private String mTipo="Móvil";
    private String mFabricante ="";
    private BeanProducto producto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir_producto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        producto = (BeanProducto) getIntent().getSerializableExtra("producto");

        nombre1 = (EditText) findViewById(R.id.nombre);
        //referencia1 = (EditText) findViewById(R.id.referencia);
        descripcion1 = (EditText) findViewById(R.id.descripcion);
        imagen1 = (EditText) findViewById(R.id.imagen);
        color1 = (EditText) findViewById(R.id.color);
        precio1 = (EditText) findViewById(R.id.precio);
        url1 = (EditText)findViewById(R.id.c);

        rellenar();

        Button modificarProd = (Button) findViewById(R.id.añadir_button);
        //Cambio de nombre al boton
        modificarProd.setText("MODIFICAR");

        //Adicion de funcionalidad al boton
        modificarProd.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                updateProduct();
            }

        });
        
        Spinner spinner = (Spinner) findViewById(R.id.tipo);
        String[] valores = {"Móvil","Tablet"};
        spinner.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, valores));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

        	//Se establece el tipo del dispositivo
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                mTipo = (String) adapterView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ObtenerFabricante obF = (ObtenerFabricante) new ObtenerFabricante(producto.getmFabricante()).execute();
        String[] fab=null;
        try{
            fab=obF.get();
        }catch (InterruptedException e){

        }catch (ExecutionException e){

        }
        Spinner spinnerfab = (Spinner) findViewById(R.id.fabs);
        spinnerfab.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, fab));
        spinnerfab.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            //Se establece quien el fabricante del producto
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                mFabricante = (String) adapterView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Metodo que se ejecuta al pulsar el boton para modificar un producto
     */
    private void updateProduct(){
        nombre = nombre1.getText().toString();
       // referencia = referencia1.getText().toString();
        color= color1.getText().toString();
        precio = precio1.getText().toString();
        imagen = imagen1.getText().toString();
        descripcion = descripcion1.getText().toString();
        url = url1.getText().toString();

        //Se crea un objeto Producto y se ejecuta su metodo doInBackground asociado 
        Producto p = (Producto) new Producto(nombre, referencia, color, precio,
                imagen, descripcion, url, mTipo, mFabricante).execute();

        try{
            boolean c=p.get();//c es el resultado del metodo doInBackground que realiza
            				  // la modificacion del producto
            if(!c){//c==false
                AlertDialog alertDialog;
                alertDialog = new AlertDialog.Builder(this).create();
                //Se informa de que ha habido un error
                alertDialog.setTitle("Error");
                alertDialog.setMessage("El producto que ha intentado modificar no " +
                        "existe en la base de datos.");
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
     * Metodo para que aparezcan los campos del producto rellenos con los datos que corresponda
     */
    private void rellenar(){
        nombre1.setText(producto.getNombre());
       // referencia1.setText(producto.getReferencia());
        url1.setText(producto.getUrl());
        color1.setText(producto.getColor());
        imagen1.setText(producto.getImagen());
        precio1.setText(producto.getPrecio());
        descripcion1.setText(producto.getDescripcion());

    }

    /**
     * Clase auxiliar necesaria para la modificacion de productos
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
        private String cif2;

        /**
         * Metodo constructor de objetos tipo Producto
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
            cif2="";
        }

        /**
         *  Metodo que ejecuta en segundo plano la modificacion de un producto.
         *  Devuelve true si la modificacion se realiza de manera correcta y false
         *  en caso contrario.
         */
        @Override
        protected Boolean doInBackground(Void ... params) {
            try{
            	//Se establece conexion con la base de datos
                BDConnection bd = BDConnection.getInstance();
                Connection connection = bd.getConnection();
                Statement st = connection.createStatement();
                //Consulta que selecciona el CIF del fabricante del producto que queremos modificar
                ResultSet rs = st.executeQuery("SELECT CIF from Fabricante where Nombre='"+fabricante2+"'");
                while(rs.next()){
                    cif2=rs.getString("CIF");
                }
                //Se ejecuta la query que hace que se actualice un producto
                int i = st.executeUpdate("UPDATE Producto set Nombre ='" + nombre2 + "', Descripcion = '" + descripcion2 + "',"+
                        " Precio = '" + precio2 + "', Color = '" + color2 + "', Fabricante = '"+ cif2 +"', " +
                        " Administrador = '1234567', Foto = '"+imagen2+"', URL = '"+url2+"', Tipo = '"+tipo2+"'"+
                        " where Referencia = '"+referencia2+"'");
                return (i>0);
            }
            catch(Exception e){
                return false;
            }
        }
    }
    
    /**
     * Clase auxiliar necesaria en la modificacion de un producto para obtener el fabricante
     * del producto que queremos modificar
     */
    public class ObtenerFabricante extends AsyncTask<Void,Void,String[]>{

        String fab;
        
        /**
         * Metodo constructor de objetos de tipo ObtenerFabricante
         */
        ObtenerFabricante(String f){
            fab=f;
        }

        /**
         * Metodo que se ejecuta en segundo plano y que busca el fabricante del producto que
         * deseamos modificar
         */
        @Override
        protected String[] doInBackground(Void ... params) {
            try{
            	//Se establece conexion con la BD
                BDConnection bd = BDConnection.getInstance();
                Connection connection = bd.getConnection();
                Statement st = connection.createStatement();
                //Se ejecuta la query que obtiene CIF y nombre de todos los fabricantes
                ResultSet rs = st.executeQuery("SELECT CIF,Nombre FROM Fabricante");
                String[] fabs = new String[100];
                String f="";
                int i = 0;
                while(rs.next()){
                    if(rs.getString("CIF").compareTo(fab)==0){
                    	//Si el CIF es igual a fab, f es el nombre del fabricante
                        f=rs.getString("Nombre");
                    }
                    //Se guarda el nombre del fabricante en una tabla de nombres
                    fabs[i] = rs.getString("Nombre");
                    i++;
                }
                String[] s = new String[i];
                for(i = 0; i<s.length; i++){
                    if(fabs[i].compareTo(f)==0){//Si el nombre del fabricante es igual a f
                        s[i]=s[0];
                        s[0]=f;
                    }else {//Si el nombre del fabricante es diferente a f
                        s[i] = fabs[i];
                    }
                }
                //Devuelve la tabla de String s que contiene los nombres de los fabricantes
                return s;
            }
            catch(Exception e){
            }
            return null;
        }

    }
}
