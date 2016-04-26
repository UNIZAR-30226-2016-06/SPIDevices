package com.spigirls.spidevices.spidevices;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;

public class BorrarFabricante extends AppCompatActivity {

    private String cif;
    private EditText cif1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrar_fabricante);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cif1 = (EditText) findViewById(R.id.cif);

        Button eliminarFab = (Button) findViewById(R.id.eliminarFab);

        eliminarFab.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                deleteFab();
            }

        });

    }

    private void deleteFab(){
        cif=cif1.getText().toString();

        Fabricante f = (Fabricante) new Fabricante(cif).execute();

        try{
            boolean c=f.get();
            if(!c){
                AlertDialog alertDialog;
                alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("El fabricante que ha intentado borrar ya " +
                        "no esta en la base de datos.");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
            else{
                finish();
            }
        }catch (InterruptedException e){

        }catch (ExecutionException e){

        }
    }

    public class Fabricante extends AsyncTask<Void,Void,Boolean> {

        private final String cif;

        Fabricante(String cif){
            this.cif=cif;
        }

        @Override
        protected Boolean doInBackground(Void ... params) {
            try{
                BDConnection bd = BDConnection.getInstance();
                Connection connection = bd.getConnection();
                Statement st = connection.createStatement();
                int i = st.executeUpdate("DELETE from Fabricante " +
                        "where CIF='"+cif+"'");
                return (i>0);
            }
            catch(Exception e){
                return false;
            }
        }


    }
}


