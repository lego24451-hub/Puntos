package com.badlogic.drop.config;

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
            case 6: return nivel6();
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
        { Celda.ROJO,     0, 0, 3, 3 },
        { Celda.AZUL,     0, 6, 2, 6 }, 
        { Celda.VERDE,    0, 3, 3, 4 }, 
        { Celda.AMARILLO, 6, 0, 4, 2 },
        { Celda.NARANJA,  6, 6, 4, 4 }
    };
    return new Nivel(3, 7, 40, "MEDIO", puntos);
}
    private static Nivel nivel4() {
    int[][] puntos = {
        { Celda.ROJO,     0, 0, 0, 7 },     
        { Celda.AZUL,     7, 0, 7, 7 },     
        { Celda.VERDE,    1, 0, 6, 0 },     
        { Celda.AMARILLO, 1, 7, 6, 7 },     
        { Celda.NARANJA,  3, 3, 4, 4 }      
    };
    return new Nivel(4, 8, 45, "DIFÍCIL", puntos);
}
    private static Nivel nivel5() {
        int[][] puntos = {
            { Celda.ROJO,     0, 0, 0, 8 },      // fila superior
            { Celda.AZUL,     8, 0, 8, 8 },      // fila inferior
            { Celda.VERDE,    1, 0, 7, 0 },      // columna izquierda
            { Celda.AMARILLO, 1, 8, 7, 8 },      // columna derecha
            { Celda.NARANJA,  2, 1, 2, 7 },      // fila 2 horizontal
            { Celda.MORADO,   6, 1, 6, 7 }       // fila 6 horizontal
        };
        return new Nivel(5, 9, 35, "MUY DIFÍCIL", puntos);
    }
    private static Nivel nivel6() {
        int[][] puntos = {
            { Celda.ROJO,     0, 0, 0, 9 },      // fila superior
            { Celda.AZUL,     9, 0, 9, 9 },      // fila inferior
            { Celda.VERDE,    1, 0, 8, 0 },      // columna izquierda
            { Celda.AMARILLO, 1, 9, 8, 9 },      // columna derecha
            { Celda.NARANJA,  2, 2, 2, 7 },      // fila 2 horizontal
            { Celda.MORADO,   7, 2, 7, 7 },      // fila 7 horizontal
            { Celda.ROSA,     4, 1, 4, 8 }       // fila 4 horizontal (centro)
        };
        return new Nivel(6, 10, 30, "EXTREMO", puntos);
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
