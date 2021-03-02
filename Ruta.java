package com.example.tracker;

import java.util.ArrayList;

public class Ruta {
private String nombre;
private ArrayList<Punto> lista_puntos;

    public Ruta(String nombre, ArrayList<Punto> lista_puntos) {
        this.nombre = nombre;
        this.lista_puntos = lista_puntos;
    }
    public Ruta() {

    }
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<Punto> getLista_puntos() {
        return lista_puntos;
    }

    public void setLista_puntos(ArrayList<Punto> lista_puntos) {
        this.lista_puntos = lista_puntos;
    }

    @Override
    public String toString() {
        return  nombre ;
    }
}
