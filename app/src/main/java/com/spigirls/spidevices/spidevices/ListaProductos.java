package com.spigirls.spidevices.spidevices;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ListaProductos extends AppCompatActivity {

    private static final int INIS_ID = Menu.FIRST;
    private static final int HOME_ID = Menu.FIRST+1;
    private Menu menu;

    private ListView mList;
    List<BeanProducto> l;
    private int posicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_productos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mList = (ListView)findViewById(R.id.list);
        posicion = 0;
        fillData();

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                posicion = position;
                Intent intent = new Intent(ListaProductos.this, InfoProducto.class);
                intent.putExtra("producto", l.get(position));
                startActivity(intent);

            }
        });
    }

    private void fillData(){

        RecProductos p = (RecProductos) new RecProductos().execute();

        try{
            l=p.get();
            if(l != null){
                mList.setAdapter(new AdaptadorProducto(this,l));
                mList.setSelection(posicion);
            }
        }
        catch(Exception e){

        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        this.menu=menu;

        boolean result = super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE, INIS_ID, Menu.NONE, R.string.menu_inicioSesion);

        menu.add(Menu.NONE, HOME_ID, Menu.NONE, R.string.menu_Home);

        return result;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        Intent i =null;

        switch (item.getItemId()) {

            case INIS_ID:

                i = new Intent(this, LoginActivity.class);
                startActivityForResult(i, 0);
                return true;

            case HOME_ID:
                i = new Intent(this, AdminHome.class);
                startActivity(i);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

    public class RecProductos extends AsyncTask<Void,Void,List > {


        RecProductos(){
        }

        @Override
        protected List  doInBackground(Void ... params) {
            List<BeanProducto>  k=new ArrayList<BeanProducto>();
            try{
                BDConnection bd = BDConnection.getInstance();
                Connection connection = bd.getConnection();
                Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery("SELECT Referencia, Nombre, Descripcion, Precio," +
                        " Color, Fabricante, Foto, URL, Tipo from Producto");
                while(rs.next()){
                    k.add(new BeanProducto(rs.getString("Tipo"),rs.getString("Fabricante"),rs.getString("Nombre"),rs.getString("Referencia"),rs.getString("Descripcion"),
                            rs.getString("Foto"),rs.getString("Color"),rs.getString("Precio"),rs.getString("URL")));
                }
                return k;
            }
            catch(Exception e){
                return null;
            }
        }
    }

}
