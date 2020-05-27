package net.jaumebalmes.grincon17.futchamp.models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

public class Partido implements Serializable {

    private Long id;
    private LocalDate fecha; // Fecha del partido
    private LocalTime hora; // Hora de inicio
    private int jornada; // sera el numero de la jornada en donde se encuentra el partido
    private Equipo local;  // id de equipo y otros datos
    private Equipo visitante; // id de equipo y otros datos
    private Calendario calendario; // id calendario que genero los partidos

    //Constructores
    public Partido() {
    }


    // Setter y Getter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public int getJornada() {
        return jornada;
    }

    public void setJornada(int jornada) {
        this.jornada = jornada;
    }
}
