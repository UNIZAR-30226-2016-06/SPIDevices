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

        l = (List<BeanProducto>)getIntent().getSerializableExtra("lista");

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

        if(l != null){
            mList.setAdapter(new AdaptadorProducto(this,l));
            mList.setSelection(posicion);
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

}
