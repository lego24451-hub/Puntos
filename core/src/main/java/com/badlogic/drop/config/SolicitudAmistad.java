package com.badlogic.drop.config;

import java.io.Serializable;
import java.time.LocalDateTime;


public class SolicitudAmistad implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Estado { PENDIENTE, ACEPTADA, RECHAZADA }

    private final String deMiParte;   // username del que envía
    private final String paraUsuario; // username del destinatario
    private Estado estado;
    private final LocalDateTime fechaEnvio;

    public SolicitudAmistad(String deMiParte, String paraUsuario) {
        this.deMiParte = deMiParte;
        this.paraUsuario = paraUsuario;
        this.estado = Estado.PENDIENTE;
        this.fechaEnvio = LocalDateTime.now();
    }

   
    public String getDeMiParte(){ 
        return deMiParte; 
    }
    public String getParaUsuario() { 
        return paraUsuario; 
    }
    public Estado getEstado(){ 
        return estado; 
    }
    public LocalDateTime getFechaEnvio(){
        return fechaEnvio; 
    }

  
    public void setEstado(Estado estado){ 
        this.estado = estado;
    }

    
    public boolean isPendiente(){ 
        return estado == Estado.PENDIENTE; 
    }
    public boolean isAceptada(){ 
        return estado == Estado.ACEPTADA; 
    }
    public boolean isRechazada(){ 
        return estado == Estado.RECHAZADA; 
    }

    @Override
    public String toString() {
        return "SolicitudAmistad{de=" + deMiParte
             + ", para=" + paraUsuario
             + ", estado=" + estado + "}";
    }
}