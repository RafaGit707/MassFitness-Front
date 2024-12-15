package com.example.massfitness.entidades;

public class Logro {
    private static int id;
    private String nombre;
    private String descripcion;
    private static int requisitosPuntos;
    private static int cantidadPuntos;
    private String recompensa;
    private boolean desbloqueado;

    public Logro() {
    }

    public Logro(int id, String nombre, String descripcion, int requisitosPuntos, int cantidadPuntos, String recompensa, boolean desbloqueado) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.requisitosPuntos = requisitosPuntos;
        this.cantidadPuntos = cantidadPuntos;
        this.recompensa = recompensa;
        this.desbloqueado = desbloqueado;
    }

    public static int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public static int getRequisitosPuntos() {
        return requisitosPuntos;
    }

    public void setRequisitosPuntos(int requisitosPuntos) {
        this.requisitosPuntos = requisitosPuntos;
    }

    public String getRecompensa() {
        return recompensa;
    }

    public void setRecompensa(String recompensa) {
        this.recompensa = recompensa;
    }

    public boolean isDesbloqueado() {
        return desbloqueado;
    }

    public void setDesbloqueado(boolean desbloqueado) {
        this.desbloqueado = desbloqueado;
    }

    public static int getCantidadPuntos() {
        return cantidadPuntos;
    }

    public void setCantidadPuntos(int cantidadPuntos) {
        this.cantidadPuntos = cantidadPuntos;
    }
}

