package com.spigirls.spidevices.spidevices;

import java.io.Serializable;

/**
 * Created by beatriz on 06/04/2016.
 */
public class BeanProducto implements Serializable {

    private String mTipo;
    private String mFabricante;
    private String nombre;
    private String referencia;
    private String descripcion;
    private String imagen;
    private String color;
    private String precio;
    private String url;

    public BeanProducto (String tipo, String fabricante, String nombre, String referencia, String descripcion, String imagen,
                         String color, String precio, String url){
        this.mTipo=tipo;
        this.mFabricante=fabricante;
        this.nombre=nombre;
        this.referencia=referencia;
        this.descripcion=descripcion;
        this.imagen=imagen;
        this.color=color;
        this.precio=precio;
        this.url=url;
    }

    public void setmTipo(String mTipo){
        this.mTipo=mTipo;
    }

    public void setmFabricante(String mFabricante){
        this.mFabricante=mFabricante;
    }

    public void setNombre(String nombre){
        this.nombre=nombre;
    }

    public void setReferencia(String referencia){
        this.referencia=referencia;
    }

    public void setDescripcion(String descripcion){
        this.descripcion=descripcion;
    }

    public void setImagen(String imagen){
        this.imagen=imagen;
    }

    public void setColor(String color){
        this.color=color;
    }

    public void setPrecio(String precio){
        this.precio=precio;
    }

    public void setUrl(String url){
        this.url=url;
    }

    public String getmTipo(){
        return mTipo;
    }

    public String getmFabricante(){
        return mFabricante;
    }

    public String getNombre(){
        return nombre;
    }

    public String getReferencia(){
        return referencia;
    }

    public String getDescripcion(){
        return descripcion;
    }

    public String getImagen(){
        return imagen;
    }

    public String getColor(){
        return color;
    }

    public String getPrecio(){
        return precio;
    }

    public String getUrl(){
        return url;
    }

    public String toString(){
        return this.nombre+" " +this.precio;
    }
}
