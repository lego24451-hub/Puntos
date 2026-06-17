package com.badlogic.drop.config;

import java.io.Serializable;
import java.util.HashMap;

public class Estadisticas implements Serializable {
    private static final long serialVersionUID = 3L; 
    private int partidasJugadas;
    private int nivelesCompletados;
    private HashMap <Integer, Long> tiempoPorNivel;
    private long tiempoTotalJugado;
    private int mejorRanking;
    private HashMap <Integer, Integer> puntajeMaximoPorNivel;
    
    public Estadisticas(){
        this.partidasJugadas = 0;
        this.nivelesCompletados = 0;
        this.tiempoPorNivel = new HashMap<>();
        this.tiempoTotalJugado = 0;
        this.mejorRanking = 0;
        this.puntajeMaximoPorNivel = new HashMap<>();
    }
    
    public void registrarPartida (int nivel, long tiempoSegundos){
        partidasJugadas++;
        tiempoTotalJugado += tiempoSegundos;
        
        if (!tiempoPorNivel.containsKey(nivel) || tiempoSegundos < tiempoPorNivel.get(nivel)){
            tiempoPorNivel.put(nivel, tiempoSegundos);
        }
    }
    
    public void registrarNivelCompletado (int nivel){
        nivelesCompletados++;
    }
    
    
    public long getTiempoPromedio (int nivel){
        if (tiempoPorNivel.containsKey(nivel)){
            return tiempoPorNivel.get(nivel);
        }
        return 0;
    }

    public int getPartidasJugadas() {
        return partidasJugadas;
    }

    public int getNivelesCompletados() {
        return nivelesCompletados;
    }

    public HashMap<Integer, Long> getTiempoPorNivel() {
        return tiempoPorNivel;
    }

    public long getTiempoTotalJugado() {
        return tiempoTotalJugado;
    }

    public int getMejorRanking() {
        return mejorRanking;
    }

    public void setMejorRanking(int mejorRanking) {
        this.mejorRanking = mejorRanking;
    }

    // Actualiza el puntaje máximo para un nivel si es mayor al anterior 
    public void actualizarPuntajeMaximo(int nivel, int puntaje) {
        Integer anterior = puntajeMaximoPorNivel.get(nivel);
        if (anterior == null || puntaje > anterior) {
            puntajeMaximoPorNivel.put(nivel, puntaje);
        }
    }

    // Obtiene el puntaje máximo de un nivel (0 si no hay registro) 
    public int getPuntajeMaximo(int nivel) {
        return puntajeMaximoPorNivel.getOrDefault(nivel, 0);
    }

    public HashMap<Integer, Integer> getPuntajeMaximoPorNivel() {
        return puntajeMaximoPorNivel;
    }
    
    
}
