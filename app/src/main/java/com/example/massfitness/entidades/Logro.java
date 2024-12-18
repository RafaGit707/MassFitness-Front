package com.example.massfitness.entidades;

import java.sql.Timestamp;

public class Logro {
    private int id_usuario_logro;
    private int id_logro;
    private int id_usuario;
    private String nombre;
    private String descripcion;
    private int requisitosPuntos;
    private int cantidadPuntos;
    private String recompensa;
    private boolean desbloqueado;
    private Timestamp fechaObtenido;

    public Logro() {
    }

    public Logro(int cantidadPuntos) {
        this.cantidadPuntos = cantidadPuntos;
    }

    public Logro(int id_usuario_logro, int id_logro, int id_usuario, Timestamp fechaObtenido) {
        this.id_usuario_logro = id_usuario_logro;
        this.id_logro = id_logro;
        this.id_usuario = id_usuario;
        this.fechaObtenido = fechaObtenido;
    }

    public Logro(int id_logro, String nombre, String descripcion, int requisitosPuntos, String recompensa) {
        this.id_logro = id_logro;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.requisitosPuntos = requisitosPuntos;
        this.recompensa = recompensa;
    }

    public int getId_usuario_logro() {
        return id_usuario_logro;
    }

    public void setId_usuario_logro(int id_usuario_logro) {
        this.id_usuario_logro = id_usuario_logro;
    }

    public int getId_logro() {
        return id_logro;
    }

    public void setId_logro(int id_logro) {
        this.id_logro = id_logro;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
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

    public int getRequisitosPuntos() {
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

    public int getCantidadPuntos() {
        return cantidadPuntos;
    }

    public void setCantidadPuntos(int cantidadPuntos) {
        this.cantidadPuntos = cantidadPuntos;
    }

    public Timestamp getFechaObtenido() {
        return fechaObtenido;
    }

    public void setFechaObtenido(Timestamp fechaObtenido) {
        this.fechaObtenido = fechaObtenido;
    }
}

