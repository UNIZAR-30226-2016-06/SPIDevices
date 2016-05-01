package com.spigirls.spidevices.administrador;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.spigirls.spidevices.database.BDConnection;
import com.spigirls.spidevices.spidevices.R;

import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.Statement;

import java.util.concurrent.ExecutionException;

/**
 * Clase LoginActivity que permite la autentificacion de un administrador mediante email/contraseña
 */
public class LoginActivity extends AppCompatActivity  {

    // Referencias de la interfaz
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);


        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    /**
     * Metodo que incia un nuevo hilo para comprobar si la autentifiacion del administrador es correcta
     * En caso de que sea correcta inicia una nueva actividad y pone la aplicacion en modo administrador
     */
    private void attemptLogin() {
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        LoginAdmin l = (LoginAdmin) new LoginAdmin(email,password).execute();
        try{
            Boolean correcto=l.get();
            if(correcto){
                SharedPreferences prefs = this.getSharedPreferences(
                        "com.spigirls.spidevices.spidevices", Context.MODE_PRIVATE);

                prefs.edit().putString("emailKey", email).apply();

                Intent i = new Intent(this, AdminHome.class);
                startActivity(i);
            }
        }catch (InterruptedException e){

        }catch (ExecutionException e){

        }
    }

    /**
     * Metodo que muestra el proceso de la interfaz y oculta el formulario de login
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }





    /**
     * Clase LoginAdmin que representa un hilo asincrono en el cual se consulta la base de datos
     * para autentificar al administrador
     */
    public class LoginAdmin extends AsyncTask<Void,Void,Boolean> {


        private final String mEmail;
        private final String mPassword;
        private Boolean c;

        /**
         * Metodo constructor de la clase LoginAdmin
         * @param email - correo electronico identificativo del administrador
         * @param password - contraseña del administrador
         */
        LoginAdmin(String email,String password){
            mEmail=email;
            mPassword=password;
        }

        /**
         * Metodo que se ejecuta en segundo plano en el cual se consulta si existe un administrado en la base de datos
         * con un determinado correo y en caso de que exista se comprueba que la contraseña sea igual a la introducida
         * @param params
         * @return true en caso de que el acceso sea correcto (email y contraseña coincidan) y false en otro caso
         */
        @Override
        protected Boolean doInBackground(Void ... params) {
            Boolean admin =false;
            try{
                Connection connection = BDConnection.getInstance().getConnection();
                Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery("SELECT contraseña from Administradores WHERE Email='"+mEmail+"'");

                if(rs.next()){
                    c=true;
                    String pass = rs.getString("Contraseña");
                    if(mPassword.compareTo(pass)==0) {
                        admin = true;
                    }
                }
                else{
                    c=false;
                }
                return admin;
            }
            catch(Exception e){

            }
            return null;
        }

        /**
         * Metodo que muestra los errores del logueo, esto puede ocurrir si no existe ningun email en la base de datos
         * o en caso de que exista pero la contrasea sea incorrecta
         * @param success - el administrador ha sido logeado correctamente o ha habido algun fallo
         */
        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            if (success) {
                finish();
            } else {
                if(!c){
                    mEmailView.setError(getString(R.string.error_invalid_email));
                    mEmailView.requestFocus();
                }
                else if(c && !success){
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }

            }
        }


    }


}

