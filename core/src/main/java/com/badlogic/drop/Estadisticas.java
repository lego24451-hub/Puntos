/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.badlogic.drop;
import java.io.Serializable;
import java.util.HashMap;
/**
 *
 * @author user
 */
public class Estadisticas implements Serializable {
    private static final long serialVersionUID = 1L;
    private int partidasJugadas;
    private int nivelesCompletados;
    private HashMap <Integer, Long> tiempoPorNivel;
    private long tiempoTotalJugado;
    private int mejorRanking;
    
    public Estadisticas(){
        this.partidasJugadas = 0;
        this.nivelesCompletados = 0;
        this.tiempoPorNivel = new HashMap<>();
        this.tiempoTotalJugado = 0;
        this.mejorRanking = 0;
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
    
    
    
    
    
}
