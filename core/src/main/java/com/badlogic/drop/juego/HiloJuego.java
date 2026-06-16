/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.badlogic.drop.juego;

/**
 *
 * @author user
 */
public class HiloJuego extends Thread {
    private final FlowFreeJuego juego;
    private boolean ejecutando;
    
    public HiloJuego (FlowFreeJuego juego){
        this.juego = juego;
        this.ejecutando = false;
    }
    
    
    
    public void run (){
        ejecutando = true;
        while (ejecutando){
            juego.actualizar();
            try{
                Thread.sleep(16);
            }catch (InterruptedException e){
                ejecutando = false;
            }
        }
    }
    
    
    public void detener(){
        ejecutando = false;
        interrupt();
    }
    
}
