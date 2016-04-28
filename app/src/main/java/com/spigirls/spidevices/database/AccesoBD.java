package com.spigirls.spidevices.database;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.spigirls.spidevices.producto.BeanProducto;
import com.spigirls.spidevices.spidevices.ListaProductos;
import com.spigirls.spidevices.spidevices.R;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AccesoBD extends AppCompatActivity {

    private String orden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceso_bd);

        orden = (String)getIntent().getSerializableExtra("Orden");

        Cargando p = (Cargando) new Cargando(this, ProgressDialog.STYLE_SPINNER, orden).execute();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

    public class Cargando extends AsyncTask<Void,Integer,List > {
        private Context mContext;
        ProgressDialog mProgress;
        private int mProgressDialog=0;
        private String orden;

        Cargando(Context context, int progressDialog, String ord){
            this.mContext = context;
            this.mProgressDialog = progressDialog;
            this.orden=ord;
        }

        @Override
        public void onPreExecute() {
            mProgress = new ProgressDialog(mContext);
            mProgress.setMessage("Cargando...");
            if (mProgressDialog==ProgressDialog.STYLE_HORIZONTAL){

                mProgress.setIndeterminate(false);
                mProgress.setMax(100);
                mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgress.setCancelable(true);
            }
            mProgress.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (mProgressDialog==ProgressDialog.STYLE_HORIZONTAL){
                mProgress.setProgress(values[0]);
            }
        }

        @Override
        protected List doInBackground(Void... values) {
            List<BeanProducto>  k=new ArrayList<BeanProducto>();
            int i = 0;
            try {
                BDConnection bd = BDConnection.getInstance();
                Connection connection = bd.getConnection();
                Statement st = connection.createStatement();
                String query="SELECT P.Referencia, P.Nombre, P.Descripcion, P.Precio," +
                        " P.Color, F.Nombre AS Fabricante, P.Foto, P.URL, P.Tipo from Producto P, Fabricante F where F.CIF" +
                        "= P.Fabricante order by P.Nombre ";
                if(orden.equals("Precio Asc")){
                    query="SELECT P.Referencia, P.Nombre, P.Descripcion, P.Precio," +
                            " P.Color, F.Nombre AS Fabricante, P.Foto, P.URL, P.Tipo from Producto P, Fabricante F where F.CIF" +
                            "= P.Fabricante order by cast(P.Precio as unsigned), P.Nombre ";
                }
                else if(orden.equals("Precio Desc")){
                    query="SELECT P.Referencia, P.Nombre, P.Descripcion, P.Precio," +
                            " P.Color, F.Nombre AS Fabricante, P.Foto, P.URL, P.Tipo from Producto P, Fabricante F where F.CIF" +
                            "= P.Fabricante order by cast(P.Precio as unsigned) DESC, P.Nombre ";
                }
                ResultSet rs = st.executeQuery(query);
                while(rs.next()){
                    i++;
                    publishProgress(i*10);
                    k.add(new BeanProducto(rs.getString("Tipo"),rs.getString("Fabricante"),rs.getString("Nombre"),rs.getString("Referencia"),rs.getString("Descripcion"),
                            rs.getString("Foto"),rs.getString("Color"),rs.getString("Precio"),rs.getString("URL")));
                }
                publishProgress((i+1)*10);
                return k;
            } catch (Exception e) {
                return null;
            }

        }

        @Override
        protected void onPostExecute(List result) {
            mProgress.dismiss();
            Intent intent = new Intent(mContext, ListaProductos.class);
            intent.putExtra("lista", (Serializable) result);
            intent.putExtra("Orden", (Serializable) orden);
            startActivity(intent);
        }
    }

}
