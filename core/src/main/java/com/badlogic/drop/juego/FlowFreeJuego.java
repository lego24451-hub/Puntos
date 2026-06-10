package com.badlogic.drop.juego;

import com.badlogic.drop.config.Celda;
import com.badlogic.drop.config.Nivel;
import com.badlogic.drop.config.Tablero;

public class FlowFreeJuego {

    private int nivelActual;   
    private Nivel nivel;
    private Tablero  tablero;

    private int  intentos;      
    private boolean pausado;
    private boolean terminado;  

    private long tiempoInicio;
    private long tiempoTranscurrido; 
    private long tiempoPausa;        

    private boolean  victoria;

    public FlowFreeJuego(int nivelInicial) {
        this.nivelActual = nivelInicial;
        cargarNivel(nivelInicial);
    }
    private void cargarNivel(int numero) {
        this.nivel = Nivel.getNivel(numero);
        this.tablero = new Tablero(nivel);
        this.intentos = 0;
        this.pausado = false;
        this.terminado = false;
        this.victoria= false;
        this.tiempoTranscurrido = 0;
        this.tiempoInicio = System.currentTimeMillis();
    }

    public void actualizar() {
        if (pausado || terminado) return;

        tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;

        if (getTiempoRestante() <= 0) {
            terminado = true;
            victoria  = false;
        }
        if (tablero.estaResuelto()) {
            terminado = true;
            victoria  = true;
            nivel.setCompletado(true);
        }
    }
    public void reiniciar() {
        tablero.resetear(nivel);
        intentos++;
        tiempoTranscurrido = 0;
        tiempoInicio = System.currentTimeMillis();
        terminado = false;
        victoria = false;
    }

    public boolean avanzarNivel() {
        if (nivelActual >= 6) return false;
        nivelActual++;
        cargarNivel(nivelActual);
        return true;
    }

    public void pausar() {
        if (!pausado && !terminado) {
            pausado = true;
            tiempoPausa = System.currentTimeMillis();
        }
    }

    public void reanudar() {
        if (pausado) {
           
            tiempoInicio += (System.currentTimeMillis() - tiempoPausa);
            pausado = false;
        }
    }

    public int[] pixelACelda(float pixelX, float pixelY,
                              float offsetX, float offsetY, float tamCelda) {
        int col = (int) ((pixelX - offsetX) / tamCelda);
        int fila = (int) ((pixelY - offsetY) / tamCelda);
        if (col < 0 || col >= nivel.getTamano()) 
            return null;
        if (fila < 0 || fila >= nivel.getTamano()) 
            return null;
        return new int[]{ fila, col };
    }

    public void iniciarPath(int fila, int columna) {
        if (!terminado && !pausado) tablero.iniciarPath(fila, columna);
    }

    public void extenderPath(int fila, int columna) {
        if (!terminado && !pausado) tablero.extenderPath(fila, columna);
    }

    public void terminarPath() {
        if (!terminado && !pausado) tablero.terminarPath();
    }
    
    public int getTiempoRestante() {
        int transcurrido = (int)(tiempoTranscurrido / 1000);
        return nivel.getTiempoLimite() - transcurrido;
    }

    public int getTiempoTranscurridoSeg() {
        return (int)(tiempoTranscurrido / 1000);
    }

    public Tablero  getTablero(){ 
        return tablero; 
    }
    public Nivel getNivel(){ 
        return nivel; 
    }
    public int getNivelNumero(){ 
        return nivelActual; 
    }
    public int getIntentos() { 
        return intentos; 
    }
    public boolean  isPausado() { 
        return pausado; 
    }
    public boolean  isTerminado(){ 
        return terminado; 
    }
    public boolean  isVictoria(){ 
        return victoria; 
    }
    public boolean  esUltimoNivel(){ 
        return nivelActual == 6; 
    }
    
}
