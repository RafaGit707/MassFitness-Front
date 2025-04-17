package com.example.massfitness.entidades;

public class Clase {

    private int idClase;
    private String nombre;
    private int capacidad_maxima;
    private Entrenador entrenador;

    public Clase() {
    }

    public Clase(int idClase, String nombre, int capacidad_maxima, Entrenador entrenador) {
        this.idClase = idClase;
        this.nombre = nombre;
        this.capacidad_maxima = capacidad_maxima;
        this.entrenador = entrenador;
    }

    public int getIdClase() {
        return idClase;
    }

    public void setIdClase(int idClase) {
        this.idClase = idClase;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCapacidad_maxima() {
        return capacidad_maxima;
    }

    public void setCapacidad_maxima(int capacidad_maxima) {
        this.capacidad_maxima = capacidad_maxima;
    }

    public Entrenador getEntrenador() {
        return entrenador;
    }

    public void setEntrenador(Entrenador entrenador) {
        this.entrenador = entrenador;
    }

}
