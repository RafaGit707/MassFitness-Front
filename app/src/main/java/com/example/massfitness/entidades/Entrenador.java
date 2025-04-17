package com.example.massfitness.entidades;

public class Entrenador {

    private int idEntrenador;
    private String nombre_entrenador;
    private String especializacion;

    public Entrenador() {
    }

    public Entrenador(int idEntrenador, String nombre_entrenador, String especializacion) {
        this.idEntrenador = idEntrenador;
        this.nombre_entrenador = nombre_entrenador;
        this.especializacion = especializacion;
    }

    public int getIdEntrenador() {
        return idEntrenador;
    }

    public void setIdEntrenador(int idEntrenador) {
        this.idEntrenador = idEntrenador;
    }

    public String getNombre_entrenador() {
        return nombre_entrenador;
    }

    public void setNombre_entrenador(String nombre_entrenador) {
        this.nombre_entrenador = nombre_entrenador;
    }

    public String getEspecializacion() {
        return especializacion;
    }

    public void setEspecializacion(String especializacion) {
        this.especializacion = especializacion;
    }

}
