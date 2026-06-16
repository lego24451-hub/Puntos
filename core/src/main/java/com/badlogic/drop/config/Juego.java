/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.badlogic.drop.config;

/**
 *
 * @author user
 */
public abstract class Juego {
    protected int nivelActual;
    protected Nivel nivel;
    protected Tablero tablero;
    
    protected int intentos, puntaje;
    protected boolean pausado, terminado, victoria;
    
    protected long tiempoInicio, tiempoTranscurrido, tiempoPausa;
    
   protected int vidas = 3;
   
    public abstract void actualizar();
    public abstract void reiniciar();
    protected abstract void calcularPuntaje();
    public abstract boolean esUltimoNivel();
    
    public boolean avanzarNivel(){
        if (nivelActual >= 6) return false;
        nivelActual++;
        return true;
    }
    
    public void pausar(){
        if (!pausado && !terminado){
            pausado = true;
            tiempoPausa = System.currentTimeMillis();
        }
    }
    
    public void reanudar(){
        if (pausado){
            tiempoInicio += (System.currentTimeMillis() - tiempoPausa);
            pausado = false;
        }
    }
   
    public int getTiempoTranscurridoSeg(){
        return (int)(tiempoTranscurrido / 1000);
    }

    
    public Tablero getTablero() { return tablero; }
    public Nivel getNivel() { return nivel; }
    public int getNivelNumero() { return nivelActual; }
    public int getIntentos() { return intentos; }
    public boolean isPausado() { return pausado; }
    public boolean isTerminado() { return terminado; }
    public boolean isVictoria() { return victoria; }
    public int getPuntaje() { return puntaje; }

    public int getVidas() {return vidas;}
    
}
