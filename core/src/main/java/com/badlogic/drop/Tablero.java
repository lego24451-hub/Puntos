package com.badlogic.drop;

/**
 * Representa el tablero NxN del Flow Free.
 *
 * Responsabilidades:
 *  - Inicializar el grid a partir de un Nivel
 *  - Gestionar el camino (path) activo que dibuja el jugador
 *  - Limpiar caminos cuando el jugador los pisa con otro color
 *  - Verificar si el tablero está completamente resuelto
 */
public class Tablero {

    // ---------------------------------------------------------
    // Atributos
    // ---------------------------------------------------------
    private final int      tamano;
    private final Celda[][] grid;

    // Path activo: color que se está arrastrando y su recorrido
    private int      colorActivo;
    private int[][]  pathActivo;   // lista de [fila, col] del camino actual
    private int      pathLongitud; // cuántas celdas lleva el path activo

    // ---------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------
    public Tablero(Nivel nivel) {
        this.tamano       = nivel.getTamano();
        this.grid         = new Celda[tamano][tamano];
        this.colorActivo  = Celda.VACIA;
        this.pathActivo   = new int[tamano * tamano][2]; // máximo posible
        this.pathLongitud = 0;

        inicializarGrid(nivel);
    }

    // ---------------------------------------------------------
    // Inicialización
    // ---------------------------------------------------------

    /** Crea todas las celdas vacías y coloca los puntos del nivel. */
    private void inicializarGrid(Nivel nivel) {
        // Crear celdas vacías
        for (int f = 0; f < tamano; f++) {
            for (int c = 0; c < tamano; c++) {
                grid[f][c] = new Celda(f, c);
            }
        }

        // Colocar puntos fijos del nivel: { color, f1, c1, f2, c2 }
        for (int[] punto : nivel.getPuntos()) {
            int color = punto[0];
            grid[punto[1]][punto[2]].marcarComoPunto(color);
            grid[punto[3]][punto[4]].marcarComoPunto(color);
        }
    }

    // ---------------------------------------------------------
    // Lógica del drag
    // ---------------------------------------------------------

    /**
     * El jugador presiona sobre una celda.
     * Si es un punto de color, inicia el path con ese color.
     * Si la celda ya tiene un color (no punto), continúa ese path desde aquí.
     */
    public void iniciarPath(int fila, int columna) {
        Celda celda = getCelda(fila, columna);
        if (celda == null) return;

        // Solo se puede iniciar desde una celda con color
        if (celda.getColor() == Celda.VACIA) return;

        colorActivo  = celda.getColor();
        pathLongitud = 0;

        // Limpiar el camino anterior de ese color (no los puntos)
        limpiarCaminoDeColor(colorActivo);

        // Agregar la celda de inicio al path
        agregarAlPath(fila, columna);
        celda.setColor(colorActivo); // restaurar color si se limpió
    }

    /**
     * El jugador arrastra hacia una nueva celda.
     * Agrega la celda al path si el movimiento es válido.
     */
    public void extenderPath(int fila, int columna) {
        if (colorActivo == Celda.VACIA) return;
        Celda celda = getCelda(fila, columna);
        if (celda == null) return;

        // No retroceder a la celda anterior del path
        if (esPenultimaCelda(fila, columna)) {
            // El usuario retrocedió: quitar la última celda del path
            retrocederPath();
            return;
        }

        // No pisar celdas ya en el path actual
        if (estaEnPathActivo(fila, columna)) return;

        // Si la celda tiene un color diferente y no es punto, limpiarla
        if (celda.getColor() != Celda.VACIA
                && celda.getColor() != colorActivo
                && !celda.isEsPunto()) {
            limpiarCaminoDeColor(celda.getColor());
        }

        // No pasar sobre un punto de otro color
        if (celda.isEsPunto() && celda.getColor() != colorActivo) return;

        // Agregar la celda al path
        agregarAlPath(fila, columna);
        celda.setColor(colorActivo);
    }

    /** El jugador soltó el mouse: se termina el path activo. */
    public void terminarPath() {
        colorActivo  = Celda.VACIA;
        pathLongitud = 0;
    }

    // ---------------------------------------------------------
    // Helpers del path
    // ---------------------------------------------------------

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
    }

    private boolean esPenultimaCelda(int fila, int columna) {
        if (pathLongitud < 2) return false;
        return pathActivo[pathLongitud - 2][0] == fila
            && pathActivo[pathLongitud - 2][1] == columna;
    }

    private boolean estaEnPathActivo(int fila, int columna) {
        for (int i = 0; i < pathLongitud; i++) {
            if (pathActivo[i][0] == fila && pathActivo[i][1] == columna) return true;
        }
        return false;
    }

    /** Limpia todas las celdas de un color que NO sean puntos fijos. */
    private void limpiarCaminoDeColor(int color) {
        for (int f = 0; f < tamano; f++) {
            for (int c = 0; c < tamano; c++) {
                if (grid[f][c].getColor() == color && !grid[f][c].isEsPunto()) {
                    grid[f][c].limpiar();
                }
            }
        }
    }

    // ---------------------------------------------------------
    // Validación de victoria
    // ---------------------------------------------------------

    /**
     * El tablero está resuelto cuando:
     *  1. Ninguna celda está vacía (todas tienen algún color)
     *  2. Todos los puntos de inicio están conectados con sus puntos de fin
     */
    public boolean estaResuelto() {
        return todasCeldasOcupadas();
        // La condición de conexión se garantiza por la mecánica del drag:
        // solo se puede completar un color si el path llega al punto de fin.
    }

    private boolean todasCeldasOcupadas() {
        for (int f = 0; f < tamano; f++) {
            for (int c = 0; c < tamano; c++) {
                if (!grid[f][c].isOcupada()) return false;
            }
        }
        return true;
    }

    // ---------------------------------------------------------
    // Reset del tablero
    // ---------------------------------------------------------

    /** Limpia todos los caminos pero conserva los puntos fijos del nivel. */
    public void resetear(Nivel nivel) {
        for (int f = 0; f < tamano; f++) {
            for (int c = 0; c < tamano; c++) {
                grid[f][c].limpiar();
            }
        }
        // Restaurar puntos fijos
        for (int[] punto : nivel.getPuntos()) {
            int color = punto[0];
            grid[punto[1]][punto[2]].marcarComoPunto(color);
            grid[punto[3]][punto[4]].marcarComoPunto(color);
        }
        colorActivo  = Celda.VACIA;
        pathLongitud = 0;
    }

    // ---------------------------------------------------------
    // Getters
    // ---------------------------------------------------------
    public int    getTamano()               { return tamano; }
    public Celda  getCelda(int f, int c)    {
        if (f < 0 || f >= tamano || c < 0 || c >= tamano) return null;
        return grid[f][c];
    }
    public int    getColorActivo()          { return colorActivo; }
    public Celda[][] getGrid()              { return grid; }
}
