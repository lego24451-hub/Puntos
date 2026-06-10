package com.badlogic.drop;

public class Celda {

    
    public static final int VACIA = 0;
    public static final int ROJO = 1;
    public static final int AZUL = 2;
    public static final int VERDE = 3;
    public static final int AMARILLO = 4;
    public static final int NARANJA = 5;
    public static final int MORADO  = 6;

    private final int fila;
    private final int columna;
    private int color;           // color actual de la celda
    private boolean esPunto;     // true si es un punto de inicio o fin (no se puede borrar)
    private boolean ocupada;     // true si ya tiene un color asignado 

    public Celda(int fila, int columna) {
        this.fila     = fila;
        this.columna  = columna;
        this.color    = VACIA;
        this.esPunto  = false;
        this.ocupada  = false;
    }

    public void setColor(int color) {
        this.color   = color;
        this.ocupada = (color != VACIA);
    }

    public void limpiar() {
        if (!esPunto) {
            this.color   = VACIA;
            this.ocupada = false;
        }
    }

    public void marcarComoPunto(int color) {
        this.color   = color;
        this.esPunto = true;
        this.ocupada = true;
    }

    public int  getFila(){ 
        return fila; 
    }
    public int  getColumna(){ 
        return columna; 
    }
    public int  getColor(){ 
        return color; 
    }
    public boolean isOcupada(){ 
        return ocupada; 
    }
    public boolean isEsPunto(){ 
        return esPunto; 
    }

    @Override
    public String toString() {
        return "[" + fila + "," + columna + " c=" + color + "]";
    }
}
