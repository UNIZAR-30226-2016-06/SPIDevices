package com.spigirls.spidevices.spidevices;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Cargando p = (Cargando) new Cargando(MainActivity.this, ProgressDialog.STYLE_SPINNER).execute();
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

        Cargando(Context context, int progressDialog){
            this.mContext = context;
            this.mProgressDialog = progressDialog;
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
                ResultSet rs = st.executeQuery("SELECT P.Referencia, P.Nombre, P.Descripcion, P.Precio," +
                        " P.Color, F.Nombre AS Fabricante, P.Foto, P.URL, P.Tipo from Producto P, Fabricante F where F.CIF" +
                        "= P.Fabricante ");
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
            startActivity(intent);
        }
    }
}
