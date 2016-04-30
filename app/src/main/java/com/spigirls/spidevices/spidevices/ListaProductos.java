package com.spigirls.spidevices.spidevices;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import com.spigirls.spidevices.administrador.AdminHome;
import com.spigirls.spidevices.administrador.LoginActivity;
import com.spigirls.spidevices.database.AccesoBD;
import com.spigirls.spidevices.producto.AdaptadorProducto;
import com.spigirls.spidevices.producto.BeanProducto;
import java.util.List;
import java.util.Locale;

/*
    Clase ListaProductos, clase principal de la aplicación.
    Muestra en la cabecera un menú desplegable que permite al administrador de la aplicación
    iniciar sesión e ir a su página de administración.
    Al principio de la pantalla se muestra una barra de búsqueda, con los filtros de búsqueda.
    A continuación aparecen la lista de productos que cumplen con las condiciones de búsqueda
    seleccionadas.
 */
public class ListaProductos extends AppCompatActivity {

    //Pestañas del menu
    private Menu menu;
    private static final int INIS_ID = Menu.FIRST;
    private static final int HOME_ID = Menu.FIRST + 1;

    private ListView mList;
    List<BeanProducto> l;
    private AdaptadorProducto adapter;
    private int posicion;
    private EditText inputSearch;
    private String orden = "Nombre";
    private String busqueda = "Nombre";
    private String tipo = "Todos";
    private boolean firstOrden=true;
    private boolean firstTipo=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_productos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.logo_toolbar);
        setSupportActionBar(toolbar);

        //Obtiene la lista de productos a mostrar
        l = (List<BeanProducto>) getIntent().getSerializableExtra("lista");
        //Obtiene el orden en el que aparecen los productos
        orden = (String) getIntent().getSerializableExtra("Orden");
        //Crea un adaptador de la lista de productos
        adapter = new AdaptadorProducto(this, l);

        inputSearch = (EditText) findViewById(R.id.buscar);
        mList = (ListView) findViewById(R.id.list);
        posicion = 0;
        fillData();

        //Cuando se selecciona un producto se inicia una nueva actividad en la que se muestra
        //toda la información del producto seleccionado.
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

        //Se define el spinner con las diferentes opciones del orden en el que aparecen
        //los productos
        Spinner spinnerOrden = (Spinner) findViewById(R.id.orden);
        String[] valores = new String[3];
        if(orden.equals("Nombre")){
            valores[0] = "Nombre";
            valores[1] = "Precio Asc";
            valores[2] = "Precio Desc";
        }
        else if(orden.equals("Precio Asc")){
            valores[0] = "Precio Asc";
            valores[1] = "Nombre";
            valores[2] = "Precio Desc";
        }
        else if(orden.equals("Precio Desc")){
            valores[0] = "Precio Desc";
            valores[1] = "Nombre";
            valores[2] = "Precio Asc";
        }
        else{
            valores[0] = "Nombre";
            valores[1] = "Precio Asc";
            valores[2] = "Precio Desc";
        }

        spinnerOrden.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, valores));
        //Cuando se selecciona un tipo de orden se reordena la lista
        spinnerOrden.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                orden = (String) adapterView.getItemAtPosition(position);
                if(firstOrden){
                    firstOrden=false;
                }
                else {
                    reordenarLista();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Se define un spinner que muestra las diferentes opciones de búsqueda de un producto
        Spinner spinnerBusqueda = (Spinner) findViewById(R.id.buscar_por);
        String[] valoresb = {"Nombre", "Fabricante", "Referencia"};
        spinnerBusqueda.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, valoresb));
        spinnerBusqueda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                busqueda = (String) adapterView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Se define un spinner con el tipo de productos que se muestran en la lista
        Spinner spinnerBusquedaTipo = (Spinner) findViewById(R.id.buscar_tipo);
        String[] valoresbt = {"Todos", "Móvil", "Tablet"};
        spinnerBusquedaTipo.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, valoresbt));
        //Cuando se selecciona un tipo se filtran los productos para que aparezcan solo los
        //de el tipo seleccionado.
        spinnerBusquedaTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                tipo = (String) adapterView.getItemAtPosition(position);
                if(firstTipo){
                    firstTipo=false;
                }
                else {
                    adapter.filterTipo(tipo);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /* Activando el filtro de busqueda */
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            /*
            Cuando se detecta un cambio en el campo introductido en la barra de búsqueda se filtran
            los productos que aparecen en la lista de forma que aparecen los que tienen algúna
            coincidencia en el campo nombre, fabricante o referencia, dependiendo de la opción
            seleccionada en el spinner.
             */
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = inputSearch.getText().toString().toLowerCase(Locale.getDefault());
                if(busqueda.equals("Nombre")){
                    adapter.filterNombre(text, tipo);
                }
                else if(busqueda.equals("Fabricante")){
                    adapter.filterFabricante(text, tipo);
                }
                else if(busqueda.equals("Referencia")){
                    adapter.filterReferencia(text, tipo);
                }
                else{
                    adapter.filterNombre(text, tipo);
                }
            }
        });

    }

    /*
    Método que se invoca cuando se selecciona un tipo determinado de orden de la lista de
    productos. Ejecuta una nueva actividad que accede a la base de datos y obtiene los productos
    en el orden seleccionado.
     */
    private void reordenarLista() {
        Intent intent = new Intent(this, AccesoBD.class);
        intent.putExtra("Orden", orden);
        startActivity(intent);
    }

    /*
    Método que muestra la lista de productos
     */
    private void fillData() {

        if (l != null) {
            mList.setAdapter(adapter);
            mList.setSelection(posicion);
        }
    }

    /*
    Método que define el menú
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        boolean result = super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE, INIS_ID, Menu.NONE, R.string.menu_inicioSesion);

        menu.add(Menu.NONE, HOME_ID, Menu.NONE, R.string.menu_Home);

        return result;
    }

    /*
    Método que implementa las acciones del menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = null;

        switch (item.getItemId()) {

            //Ejecuta pantalla de inicio de sesión de el administrador
            case INIS_ID:

                i = new Intent(this, LoginActivity.class);
                startActivityForResult(i, 0);
                return true;

            //Ejecuta pantalla home administrador
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
