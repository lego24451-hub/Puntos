package com.badlogic.drop.config;

import java.io.Serializable;
import java.time.LocalDateTime;


 // Flujo:
 // 1. Retador crea el reto → estado PENDIENTE
 // 2. Retado lo ve en su inbox y acepta o rechaza
 // 3. Si acepta → juega el nivel → se registra puntajeRetado
 // 4. Se comparan puntajes y se determina ganador → estado FINALIZADO
 // 5. Retador ve el resultado en su inbox
 
public class Reto implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Estado {
        PENDIENTE,    
        ACEPTADO,     
        RECHAZADO,    
        FINALIZADO    
    }

    public enum Resultado {
        RETADOR_GANA,
        RETADO_GANA,
        EMPATE
    }

    
    private final String idReto;        // único: retador_retado_timestamp
    private final String usernameRetador;
    private final String usernameRetado;
    private final int    nivelNumero;

    // ── Puntajes 
    private int puntajeRetador;         // puntaje que obtuvo el retador
    private int puntajeRetado;          // puntaje que obtiene el retado al aceptar

    // ── Estado 
    private Estado  estado;
    private Resultado resultado;        // sólo válido cuando estado == FINALIZADO

    
    private final LocalDateTime fechaCreacion;
    private LocalDateTime       fechaRespuesta;

    
    public Reto(String usernameRetador, String usernameRetado,
                int nivelNumero, int puntajeRetador) {
        this.usernameRetador = usernameRetador;
        this.usernameRetado  = usernameRetado;
        this.nivelNumero = nivelNumero;
        this.puntajeRetador  = puntajeRetador;
        this.puntajeRetado = -1;  // todavía no jugó
        this.estado = Estado.PENDIENTE;
        this.resultado = null;
        this.fechaCreacion   = LocalDateTime.now();
        this.idReto = usernameRetador + "_" + usernameRetado
                             + "_" + System.currentTimeMillis();
    }

    
    public String   getIdReto(){ 
        return idReto; 
    }
    public String getUsernameRetador(){ 
        return usernameRetador; 
    }
    public String getUsernameRetado(){ 
        return usernameRetado; 
    }
    public int getNivelNumero(){ 
        return nivelNumero; 
    }
    public int getPuntajeRetador(){ 
        return puntajeRetador; 
    }
    public int getPuntajeRetado(){ 
        return puntajeRetado;
    }
    public Estado getEstado(){
        return estado; 
    }
    public Resultado getResultado() { 
        return resultado; 
    }
    public LocalDateTime getFechaCreacion(){ 
        return fechaCreacion; 
    }
    public LocalDateTime getFechaRespuesta(){ 
        return fechaRespuesta; 
    }

    // ── Setters / acciones

    // Llamar cuando el retado rechaza el reto. 
    public void rechazar() {
        this.estado          = Estado.RECHAZADO;
        this.fechaRespuesta  = LocalDateTime.now();
    }

    // Llamar cuando el retado acepta (pero todavía no juega). 
    public void aceptar() {
        this.estado         = Estado.ACEPTADO;
        this.fechaRespuesta = LocalDateTime.now();
    }

    
     // Llamar después de que el retado terminó de jugar.
     // Registra su puntaje y determina automáticamente el ganador.
     
    public void registrarPuntajeRetado(int puntaje) {
        this.puntajeRetado = puntaje;
        this.estado = Estado.FINALIZADO;
        this.fechaRespuesta = LocalDateTime.now();

        if (puntajeRetador > puntajeRetado) resultado = Resultado.RETADOR_GANA;
        else if (puntajeRetado > puntajeRetador)  resultado = Resultado.RETADO_GANA;
        else resultado = Resultado.EMPATE;
    }

    // Helpers de estado
    public boolean isPendiente(){ 
        return estado == Estado.PENDIENTE; 
    }
    public boolean isAceptado(){ 
        return estado == Estado.ACEPTADO; 
    }
    public boolean isRechazado(){ 
        return estado == Estado.RECHAZADO; 
    }
    public boolean isFinalizado() { 
        return estado == Estado.FINALIZADO; 
    }

    
     // Devuelve el username del ganador, o null si hay empate / no terminó.
     
    public String getGanador() {
        if (resultado == null) return null;
        switch (resultado) {
            case RETADOR_GANA: return usernameRetador;
            case RETADO_GANA:  return usernameRetado;
            default:           return null; // EMPATE
        }
    }

    @Override
    public String toString() {
        return "Reto{" + idReto + ", nivel=" + nivelNumero
             + ", estado=" + estado + "}";
    }
}