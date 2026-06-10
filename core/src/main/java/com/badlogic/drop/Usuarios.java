/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.badlogic.drop;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
/**
 *
 * @author user
 */
public class Usuarios implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private String username, contrasena, nombreCompleto, avatar;
    private LocalDateTime fechaRegistro,ultimaSesion;
    private int ranking;
    private ArrayList <String> amigos;
    private Estadisticas estadisticas;
    
    public Usuarios (String username, String contrasena, String nombreCompleto){
        this.username = username;
        this.contrasena = contrasena;
        this.nombreCompleto = nombreCompleto;
        this.fechaRegistro = LocalDateTime.now();
        this.ultimaSesion = LocalDateTime.now();
        this.avatar = "default";
        this.ranking = 0;
        this.amigos = new ArrayList<>();
        this.estadisticas = new Estadisticas();
        
        
    }

    public Estadisticas getEstadisticas() {
        return estadisticas;
    }

    public String getUsername() {
        return username;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getAvatar() {
        return avatar;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public LocalDateTime getUltimaSesion() {
        return ultimaSesion;
    }

    public int getRanking() {
        return ranking;
    }

    public ArrayList<String> getAmigos() {
        return amigos;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setUltimaSesion(LocalDateTime ultimaSesion) {
        this.ultimaSesion = ultimaSesion;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }


public void agregarAmigo (String username){
    if (!amigos.contains(username)){
        amigos.add(username);
    }
}
    

public String toString(){
    return "Usuario: " +username + " | Nombre: " +nombreCompleto + "| Ranking: " +ranking;
}
}
