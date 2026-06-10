package com.badlogic.drop;

public class Tablero {

    private final int tamano;
    private final Celda[][] grid;

    private int colorActivo;
    private int[][]  pathActivo;
    private int pathLongitud;
    private boolean pathCerrado;

    private int[][][] puntosPorColor;
   
    private boolean[] colorConectado;
    
    private int totalColores;

    public Tablero(Nivel nivel) {
        this.tamano = nivel.getTamano();
        this.grid = new Celda[tamano][tamano];
        this.colorActivo = Celda.VACIA;
        this.pathActivo = new int[tamano * tamano][2];
        this.pathLongitud = 0;
        this.pathCerrado  = false;
        this.puntosPorColor = new int[7][2][2];
        this.colorConectado = new boolean[7];
        this.totalColores = nivel.getPuntos().length;

        inicializarGrid(nivel);
    }

    private void inicializarGrid(Nivel nivel) {
        for (int f = 0; f < tamano; f++)
            for (int c = 0; c < tamano; c++)
                grid[f][c] = new Celda(f, c);

        for (int[] punto : nivel.getPuntos()) {
            int color = punto[0];
            grid[punto[1]][punto[2]].marcarComoPunto(color);
            grid[punto[3]][punto[4]].marcarComoPunto(color);
            puntosPorColor[color][0][0] = punto[1];
            puntosPorColor[color][0][1] = punto[2];
            puntosPorColor[color][1][0] = punto[3];
            puntosPorColor[color][1][1] = punto[4];
        }
    }
    public void iniciarPath(int fila, int columna) {
        Celda celda = getCelda(fila, columna);
        if (celda == null || celda.getColor() == Celda.VACIA) return;

        colorActivo  = celda.getColor();
        pathLongitud = 0;
        pathCerrado  = false;
        colorConectado[colorActivo] = false;

        limpiarCaminoDeColor(colorActivo);
        agregarAlPath(fila, columna);
        celda.setColor(colorActivo);
    }

    public void extenderPath(int fila, int columna) {
        if (colorActivo == Celda.VACIA || pathCerrado) return;
        Celda celda = getCelda(fila, columna);
        if (celda == null) return;

        if (esPenultimaCelda(fila, columna)) {
            retrocederPath();
            return;
        }

        if (estaEnPathActivo(fila, columna)) return;

        if (celda.getColor() != Celda.VACIA
                && celda.getColor() != colorActivo
                && !celda.isEsPunto()) {
            colorConectado[celda.getColor()] = false;
            limpiarCaminoDeColor(celda.getColor());
        }

        if (celda.isEsPunto() && celda.getColor() != colorActivo) return;

        agregarAlPath(fila, columna);
        celda.setColor(colorActivo);

        if (esSegundoPunto(fila, columna)) {
            colorConectado[colorActivo] = true;
            pathCerrado = true;
        }
    }

    public void terminarPath() {
        colorActivo  = Celda.VACIA;
        pathLongitud = 0;
        pathCerrado  = false;
    }
    private void agregarAlPath(int fila, int columna) {
        pathActivo[pathLongitud][0] = fila;
        pathActivo[pathLongitud][1] = columna;
        pathLongitud++;
    }

    private void retrocederPath() {
        if (pathLongitud <= 1) return;
        int f = pathActivo[pathLongitud - 1][0];
        int c = pathActivo[pathLongitud - 1][1];
        Celda celda = grid[f][c];
        if (!celda.isEsPunto()) celda.limpiar();
        pathLongitud--;
        colorConectado[colorActivo] = false;
        pathCerrado = false;
    }

    private boolean esPenultimaCelda(int fila, int columna) {
        if (pathLongitud < 2) return false;
        return pathActivo[pathLongitud - 2][0] == fila
            && pathActivo[pathLongitud - 2][1] == columna;
    }

    private boolean estaEnPathActivo(int fila, int columna) {
        for (int i = 0; i < pathLongitud; i++)
            if (pathActivo[i][0] == fila && pathActivo[i][1] == columna) return true;
        return false;
    }
    private boolean esSegundoPunto(int fila, int columna) {
        if (colorActivo <= 0 || colorActivo >= 7) return false;
        Celda celda = grid[fila][columna];
        if (!celda.isEsPunto()) return false;

        int inicioF = pathActivo[0][0];
        int inicioC = pathActivo[0][1];
        boolean esInicio = (fila == inicioF && columna == inicioC);
        if (esInicio) return false;

        int pA0 = puntosPorColor[colorActivo][0][0], pA1 = puntosPorColor[colorActivo][0][1];
        int pB0 = puntosPorColor[colorActivo][1][0], pB1 = puntosPorColor[colorActivo][1][1];
        return (fila == pA0 && columna == pA1) || (fila == pB0 && columna == pB1);
    }

    private void limpiarCaminoDeColor(int color) {
        for (int f = 0; f < tamano; f++)
            for (int c = 0; c < tamano; c++)
                if (grid[f][c].getColor() == color && !grid[f][c].isEsPunto())
                    grid[f][c].limpiar();
    }
    public boolean estaResuelto() {
        if (!todasCeldasOcupadas()) return false;
        return todosColoresConectados();
    }

    private boolean todasCeldasOcupadas() {
        for (int f = 0; f < tamano; f++)
            for (int c = 0; c < tamano; c++)
                if (!grid[f][c].isOcupada()) return false;
        return true;
    }

    private boolean todosColoresConectados() {
        for (int color = 1; color < 7; color++) {
            if (colorTienePuntos(color) && !colorConectado[color]) return false;
        }
        return true;
    }

    private boolean colorTienePuntos(int color) {
        for (int f = 0; f < tamano; f++)
            for (int c = 0; c < tamano; c++)
                if (grid[f][c].isEsPunto() && grid[f][c].getColor() == color) return true;
        return false;
    }
    public void resetear(Nivel nivel) {
        for (int f = 0; f < tamano; f++)
            for (int c = 0; c < tamano; c++)
                grid[f][c].limpiar();

        for (int[] punto : nivel.getPuntos()) {
            int color = punto[0];
            grid[punto[1]][punto[2]].marcarComoPunto(color);
            grid[punto[3]][punto[4]].marcarComoPunto(color);
            puntosPorColor[color][0][0] = punto[1];
            puntosPorColor[color][0][1] = punto[2];
            puntosPorColor[color][1][0] = punto[3];
            puntosPorColor[color][1][1] = punto[4];
        }
        colorActivo = Celda.VACIA;
        pathLongitud = 0;
        pathCerrado = false;
        colorConectado = new boolean[7];
    }
    public int getTamano(){ 
        return tamano; }
    public Celda    getCelda(int f, int c) {
        if (f < 0 || f >= tamano || c < 0 || c >= tamano) return null;
        return grid[f][c];
    }
    public int getColorActivo(){ 
        return colorActivo; 
    }
    public Celda[][] getGrid(){ 
        return grid; 
    }
}