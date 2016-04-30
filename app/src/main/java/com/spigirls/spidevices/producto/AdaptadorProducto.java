package com.spigirls.spidevices.producto;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.spigirls.spidevices.spidevices.R;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Clase AdaptadorProducto que implementa un adaptador de la lista de productos. Implementando
 * su vista dentro de la lista. Además tiene diferentes métodos para filtrar los productos
 * de la lista de acuerdo a diferentes características.
 */
public class AdaptadorProducto extends BaseAdapter {

    private final Activity actividad;
    private final List<BeanProducto> lista;
    private ArrayList<BeanProducto> arraylist;

    public AdaptadorProducto(Activity actividad, List<BeanProducto> lista) {
        super();
        this.actividad = actividad;
        this.lista = lista;
        this.arraylist = new ArrayList<BeanProducto>();
        this.arraylist.addAll(lista);
    }

    /*
    Método que devuelve la vista de el producto en la lista con su imagen, nombre y precio
     */
    public View getView(int position, View convertView,
                        ViewGroup parent) {
        LayoutInflater inflater = actividad.getLayoutInflater();
        View view = inflater.inflate(R.layout.product_row, null, true);
        TextView textView =(TextView)view.findViewById(R.id.row_nombre);
        TextView priceView =(TextView)view.findViewById(R.id.row_precio);
        ImageView imageView=(ImageView)view.findViewById(R.id.imageView);
        textView.setText(lista.get(position).getNombre());
        priceView.setText(lista.get(position).getPrecio()+" €");
        String img=lista.get(position).getImagen();

        CargarImagen c=(CargarImagen) new CargarImagen(img).execute();
        try{
            Bitmap bm=c.get();
            if(bm!=null){
                imageView.setAdjustViewBounds(true);
                imageView.setImageBitmap(bm);
            }
        }
        catch(Exception e){
        }
        return view;
    }

    public int getCount() {
        return lista.size();
    }
    public Object getItem(int arg0) {
        return lista.get(arg0);
    }
    public long getItemId(int position) {
        return position;
    }

    /*
    Método que filtra los productos por coincidencias en el nombre
     */
    public void filterNombre(String charText, String tipo) {
        charText = charText.toLowerCase(Locale.getDefault());
        lista.clear();
        if (charText.length() == 0) {
            filterTipo(tipo);
        }
        else {
            for (BeanProducto wp : arraylist)
            {
                if (wp.getNombre().toLowerCase(Locale.getDefault()).contains(charText)
                        && (tipo.equals("Todos") || (tipo.equals("Móvil") && wp.esMovil()
                        || (tipo.equals("Tablet") && !wp.esMovil())))){
                    lista.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    /*
    Método que filtra los productos por coincidencias en el fabricante
     */
    public void filterFabricante(String charText, String tipo) {
        charText = charText.toLowerCase(Locale.getDefault());
        lista.clear();
        if (charText.length() == 0) {
            filterTipo(tipo);
        }
        else {
            for (BeanProducto wp : arraylist)
            {
                if (wp.getmFabricante().toLowerCase(Locale.getDefault()).contains(charText)
                && (tipo.equals("Todos") || (tipo.equals("Móvil") && wp.esMovil()
                        || (tipo.equals("Tablet") && !wp.esMovil())))){
                    lista.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    /*
    Método que filtra los productos por coincidencias en la referencia
     */
    public void filterReferencia(String charText, String tipo) {
        charText = charText.toLowerCase(Locale.getDefault());
        lista.clear();
        if (charText.length() == 0) {
            filterTipo(tipo);
        }
        else {
            for (BeanProducto wp : arraylist)
            {
                if (wp.getReferencia().toLowerCase(Locale.getDefault()).contains(charText)
                && (tipo.equals("Todos") || (tipo.equals("Móvil") && wp.esMovil()
                    || (tipo.equals("Tablet") && !wp.esMovil())))){
                    lista.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    /*
    Método que filtra los productos por tipo
     */
    public void filterTipo(String tipo) {
        lista.clear();
        if(tipo.equals("Todos")) {
            lista.addAll(arraylist);
        }
        else{
            for (BeanProducto wp : arraylist) {
                if ((tipo.equals("Móvil") && wp.esMovil())
                        || (tipo.equals("Tablet") && !wp.esMovil())){
                    lista.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    /*
    Clase que ejecuta un hilo para cargar una imagen de internet
     */
    public class CargarImagen extends AsyncTask<Void,Void,Bitmap> {

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

}
