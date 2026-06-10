package com.badlogic.drop;

public class Nivel {

    private final int    numero;
    private final int    tamano;        // tamaño del grid NxN
    private final int    tiempoLimite;  // en segundos
    private final String dificultad;
    private final int[][] puntos;       // { color, f1, c1, f2, c2 }
    private boolean       completado;

    public Nivel(int numero, int tamano, int tiempoLimite, String dificultad, int[][] puntos) {
        this.numero       = numero;
        this.tamano       = tamano;
        this.tiempoLimite = tiempoLimite;
        this.dificultad   = dificultad;
        this.puntos       = puntos;
        this.completado   = false;
    }

    public static Nivel getNivel(int numero) {
        switch (numero) {
            case 1: return nivel1();
            case 2: return nivel2();
            case 3: return nivel3();
            case 4: return nivel4();
            case 5: return nivel5();
            default: throw new IllegalArgumentException("Nivel " + numero + " no existe.");
        }
    }

    private static Nivel nivel1() {
        int[][] puntos = {
            { Celda.ROJO,     1, 0, 4, 0 },
            { Celda.AZUL,     2, 2, 4, 4 },
            { Celda.VERDE,    0, 4, 3, 4 }   // reutilizamos VERDE como tercer color
        };
        return new Nivel(1, 5, 60, "MUY FÁCIL", puntos);
    }
    private static Nivel nivel2() {
   
        int[][] puntos = {
            { Celda.ROJO,     5, 0, 0, 0 },
            { Celda.AZUL,     5, 5, 0, 5 },
            { Celda.VERDE,    4, 2, 2, 2 },
            { Celda.AMARILLO, 1, 3, 0, 3 }
        };
        return new Nivel(2, 6, 50, "FÁCIL", puntos);
    }
    private static Nivel nivel3() {
        int[][] puntos = {
            { Celda.ROJO,     0, 0, 6, 6 },
            { Celda.AZUL,     0, 6, 6, 0 },
            { Celda.VERDE,    0, 3, 6, 3 },
            { Celda.AMARILLO, 3, 0, 3, 6 },
            { Celda.NARANJA,  1, 1, 5, 5 }
        };
        return new Nivel(3, 7, 40, "MEDIO", puntos);
    }
    private static Nivel nivel4() {
        int[][] puntos = {
            { Celda.ROJO,     0, 0, 7, 7 },
            { Celda.AZUL,     0, 7, 7, 0 },
            { Celda.VERDE,    0, 4, 7, 3 },
            { Celda.AMARILLO, 2, 0, 5, 7 },
            { Celda.NARANJA,  1, 2, 6, 5 }
        };
        return new Nivel(4, 8, 30, "DIFÍCIL", puntos);
    }
    private static Nivel nivel5() {
        int[][] puntos = {
            { Celda.ROJO,     0, 0, 8, 8 },
            { Celda.AZUL,     0, 8, 8, 0 },
            { Celda.VERDE,    0, 4, 8, 4 },
            { Celda.AMARILLO, 4, 0, 4, 8 },
            { Celda.NARANJA,  1, 1, 7, 7 },
            { Celda.MORADO,   2, 6, 6, 2 }
        };
        return new Nivel(5, 9, 20, "MUY DIFÍCIL", puntos);
    }

    public int getNumero(){ 
        return numero; 
    }
    public int getTamano(){ 
        return tamano; 
    }
    public int getTiempoLimite() { 
        return tiempoLimite; 
    }
    public String  getDificultad(){ 
        return dificultad; 
    }
    public int[][] getPuntos(){ 
        return puntos; 
    }
    public boolean isCompletado(){ 
        return completado; 
    }
    public void setCompletado(boolean completado) 
    { this.completado = completado; 
    }
}