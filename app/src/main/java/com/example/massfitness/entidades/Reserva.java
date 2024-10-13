package com.example.massfitness.entidades;

import java.sql.Timestamp;
import java.util.Date;

public class Reserva {
    private int idReserva;
    private int idUsuario;
    private int espacio_id;
    private String tipoReserva;

    private Timestamp horarioReserva;
    private String estadoReserva;

    public Reserva() {
    }

    public Reserva(int idReserva, int idUsuario, int espacio_id, String tipoReserva, Timestamp horarioReserva, String estadoReserva) {
        this.idReserva = idReserva;
        this.idUsuario = idUsuario;
        this.espacio_id = espacio_id;
        this.tipoReserva = tipoReserva;
        this.horarioReserva = horarioReserva;
        this.estadoReserva = estadoReserva;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getEspacio_id() {
        return espacio_id;
    }

    public void setEspacio_id(int espacio_id) {
        this.espacio_id = espacio_id;
    }

    public String getTipoReserva() {
        return tipoReserva;
    }

    public void setTipoReserva(String tipoReserva) {
        this.tipoReserva = tipoReserva;
    }

    public Timestamp getHorarioReserva() {
        return horarioReserva;
    }

    public void setHorarioReserva(Timestamp horarioReserva) {
        this.horarioReserva = horarioReserva;
    }

    public String getEstadoReserva() {
        return estadoReserva;
    }

    public void setEstadoReserva(String estadoReserva) {
        this.estadoReserva = estadoReserva;
    }
}
