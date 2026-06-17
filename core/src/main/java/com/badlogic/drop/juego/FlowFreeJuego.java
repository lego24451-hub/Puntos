package com.badlogic.drop.juego;

import com.badlogic.drop.config.Celda;
import com.badlogic.drop.config.Juego;
import com.badlogic.drop.config.Nivel;
import com.badlogic.drop.config.Tablero;

public class FlowFreeJuego extends Juego {

    public FlowFreeJuego(int nivelInicial) {
        this.nivelActual = nivelInicial;
        cargarNivel(nivelInicial);
    }

    private void cargarNivel(int numero) {
        this.nivel = Nivel.getNivel(numero);
        this.tablero = new Tablero(nivel);
        this.intentos = 0;
        this.vidas = 3;
        this.pausado = false;
        this.terminado = false;
        this.victoria = false;
        this.tiempoTranscurrido = 0;
        this.tiempoInicio = System.currentTimeMillis();
    }

    @Override
    public synchronized void actualizar() { //para que no se creen conflictos
        if (pausado || terminado) return;

        tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;

        
        if (tablero.estaResuelto()) {
            terminado = true;
            victoria = true;
            nivel.setCompletado(true);
            calcularPuntaje();
        }
    }

    @Override
    public void reiniciar() {
        
        if (terminado && vidas == 0){
            vidas = 3;
        }
        
        if (vidas > 0){
            vidas--;
            tablero.resetear(nivel);
         intentos++;
//        if(intentos == 3) { //cuando pasa de 3 el contador se reinicia a 0 
//          intentos = 0; 
//        }
            tiempoTranscurrido = 0;
            tiempoInicio = System.currentTimeMillis();
            terminado = false;
            victoria = false;
            
        }
        
        else if (intentos ==3){
            intentos = 0; 
            vidas = 0;
            terminado = true;
            victoria = false;
        }
    }

    @Override
    public boolean avanzarNivel() {
        if (nivelActual >= 6) return false;
        nivelActual++;
        cargarNivel(nivelActual);
        return true;
    }

    @Override
    public boolean esUltimoNivel() {
        return nivelActual == 6;
    }

    @Override
    protected void calcularPuntaje() {
        int tiempoSeg = getTiempoTranscurridoSeg();
        if (tiempoSeg < 1) tiempoSeg = 1;

        int tiempoLimite = nivel.getTiempoLimite();
        int nivelNum = nivel.getNumero();

       
        puntaje = (tiempoLimite * 100) / tiempoSeg + (nivelNum * 10);
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
        return getTiempoTranscurridoSeg();
    }

    
    
    
    
}
