package com.spigirls.spidevices.spidevices;

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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by YolandaGC on 13/4/16.
 */
public class AdaptadorProducto extends BaseAdapter {

    private final Activity actividad;
    private final List<BeanProducto> lista;

    public AdaptadorProducto(Activity actividad, List<BeanProducto> lista) {
        super();
        this.actividad = actividad;
        this.lista = lista;
    }
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
