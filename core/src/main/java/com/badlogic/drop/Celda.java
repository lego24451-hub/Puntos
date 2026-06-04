package com.badlogic.drop;

/**
 * Representa una celda individual dentro del tablero del Flow Free.
 * Cada celda tiene una posición (fila, columna), un color asignado
 * y puede ser un punto de inicio/fin o parte de un camino.
 */
public class Celda {

    // ---------------------------------------------------------
    // Constantes de color
    // Se usan enteros para representar cada color del juego.
    // 0 = vacía, 1-6 = colores distintos
    // ---------------------------------------------------------
    public static final int VACIA   = 0;
    public static final int ROJO    = 1;
    public static final int AZUL    = 2;
    public static final int VERDE   = 3;
    public static final int AMARILLO = 4;
    public static final int NARANJA = 5;
    public static final int MORADO  = 6;

    // ---------------------------------------------------------
    // Atributos
    // ---------------------------------------------------------
    private final int fila;
    private final int columna;
    private int color;           // color actual de la celda
    private boolean esPunto;     // true si es un punto de inicio o fin (no se puede borrar)
    private boolean ocupada;     // true si ya tiene un color asignado

    // ---------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------
    public Celda(int fila, int columna) {
        this.fila     = fila;
        this.columna  = columna;
        this.color    = VACIA;
        this.esPunto  = false;
        this.ocupada  = false;
    }

    // ---------------------------------------------------------
    // Métodos
    // ---------------------------------------------------------

    /** Asigna un color a la celda y la marca como ocupada. */
    public void setColor(int color) {
        this.color   = color;
        this.ocupada = (color != VACIA);
    }

    /** Limpia la celda si NO es un punto fijo. */
    public void limpiar() {
        if (!esPunto) {
            this.color   = VACIA;
            this.ocupada = false;
        }
    }

    /** Marca esta celda como punto fijo de inicio/fin. */
    public void marcarComoPunto(int color) {
        this.color   = color;
        this.esPunto = true;
        this.ocupada = true;
    }

    // ---------------------------------------------------------
    // Getters
    // ---------------------------------------------------------
    public int  getFila()    { return fila; }
    public int  getColumna() { return columna; }
    public int  getColor()   { return color; }
    public boolean isOcupada() { return ocupada; }
    public boolean isEsPunto() { return esPunto; }

    @Override
    public String toString() {
        return "[" + fila + "," + columna + " c=" + color + "]";
    }
}
