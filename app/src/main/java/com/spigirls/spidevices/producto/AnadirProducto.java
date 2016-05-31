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

        Button añadir_button = (Button) findViewById(R.id.añadir_button);

        añadir_button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                setProducto();
            }

        });

        Spinner spinner = (Spinner) findViewById(R.id.tipo);
        String[] valores = {"Móvil","Tablet"};
        spinner.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, valores));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                mTipo = (String) adapterView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        ObtenerFabricante obF = (ObtenerFabricante) new ObtenerFabricante().execute();
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

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                mFabricante = (String) adapterView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void setProducto(){
        nombre = mNombre.getText().toString();
        referencia = mReferencia.getText().toString();
        color= mColor.getText().toString();
        precio = mPrecio.getText().toString();
        imagen = mImagen.getText().toString();
        descripcion = mDescripcion.getText().toString();
        url = mUrl.getText().toString();

        Producto p = (Producto) new Producto(nombre, referencia, color, precio,
                imagen, descripcion, url, mTipo, mFabricante).execute();

        try{
            boolean c=p.get();
            if(!c){
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
            else{
                Intent intent = new Intent(this, AccesoBD.class);
                intent.putExtra("Orden", "Nombre");
                startActivity(intent);
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
                ResultSet rs = st.executeQuery("SELECT CIF from Fabricante where Nombre='"+fabricante2+"'");
                String cif="";
                while(rs.next()){
                    cif=rs.getString("CIF");
                }
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

    public class ObtenerFabricante extends AsyncTask<Void,Void,String[]>{

        ObtenerFabricante(){
        }

        @Override
        protected String[] doInBackground(Void ... params) {
            try{
                BDConnection bd = BDConnection.getInstance();
                Connection connection = bd.getConnection();
                Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery("SELECT Nombre FROM Fabricante");
                String[] fabs = new String[100];
                int i = 0;
                while(rs.next()){
                    fabs[i] = rs.getString("Nombre");
                    i++;
                }
                String[] s = new String[i];
                for(i = 0; i<s.length; i++){
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
