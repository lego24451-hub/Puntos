package com.badlogic.drop;

/**
 * Lógica principal del juego Flow Free.
 *
 * Esta clase orquesta:
 *  - El nivel activo y el tablero
 *  - El conteo de intentos (reinicios)
 *  - El tiempo transcurrido (para mostrar en HUD)
 *  - La detección de victoria y derrota por tiempo
 *  - El avance al siguiente nivel
 *
 * NO contiene código de renderizado — eso va en PantallaJuego.java (LibGDX).
 * Esta clase es Java puro y puede probarse sin LibGDX.
 */
public class FlowFreeJuego {

    // ---------------------------------------------------------
    // Atributos
    // ---------------------------------------------------------
    private int      nivelActual;   // número del nivel (1-5)
    private Nivel    nivel;
    private Tablero  tablero;

    private int      intentos;      // cuántas veces reinició en este nivel
    private boolean  pausado;
    private boolean  terminado;     // true cuando ganó o perdió

    // Control de tiempo (en milisegundos)
    private long     tiempoInicio;
    private long     tiempoTranscurrido; // ms acumulados mientras no está pausado
    private long     tiempoPausa;        // momento en que se pausó

    // Estado del resultado
    private boolean  victoria;

    // ---------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------
    public FlowFreeJuego(int nivelInicial) {
        this.nivelActual = nivelInicial;
        cargarNivel(nivelInicial);
    }

    // ---------------------------------------------------------
    // Ciclo de vida
    // ---------------------------------------------------------

    /** Carga un nivel nuevo y reinicia el estado del juego. */
    private void cargarNivel(int numero) {
        this.nivel              = Nivel.getNivel(numero);
        this.tablero            = new Tablero(nivel);
        this.intentos           = 0;
        this.pausado            = false;
        this.terminado          = false;
        this.victoria           = false;
        this.tiempoTranscurrido = 0;
        this.tiempoInicio       = System.currentTimeMillis();
    }

    /**
     * Debe llamarse en cada frame del render() de LibGDX.
     * Actualiza el tiempo y verifica si se agotó.
     */
    public void actualizar() {
        if (pausado || terminado) return;

        tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;

        // Verificar derrota por tiempo agotado
        if (getTiempoRestante() <= 0) {
            terminado = true;
            victoria  = false;
        }

        // Verificar victoria
        if (tablero.estaResuelto()) {
            terminado = true;
            victoria  = true;
            nivel.setCompletado(true);
        }
    }

    /** Reinicia el tablero del nivel actual sin cambiar el nivel. */
    public void reiniciar() {
        tablero.resetear(nivel);
        intentos++;
        tiempoTranscurrido = 0;
        tiempoInicio       = System.currentTimeMillis();
        terminado          = false;
        victoria           = false;
    }

    /** Avanza al siguiente nivel. Retorna false si ya es el último. */
    public boolean avanzarNivel() {
        if (nivelActual >= 5) return false;
        nivelActual++;
        cargarNivel(nivelActual);
        return true;
    }

    public void pausar() {
        if (!pausado && !terminado) {
            pausado     = true;
            tiempoPausa = System.currentTimeMillis();
        }
    }

    public void reanudar() {
        if (pausado) {
            // Compensar el tiempo que estuvo pausado
            tiempoInicio += (System.currentTimeMillis() - tiempoPausa);
            pausado = false;
        }
    }

    // ---------------------------------------------------------
    // Delegación al Tablero (input del jugador)
    // ---------------------------------------------------------

    /**
     * Convierte coordenadas de pantalla (píxeles) a celda del grid.
     * Debe llamarse pasando el tamaño de la ventana y el tamaño del tablero en pantalla.
     *
     * @param pixelX      coordenada X del mouse en pantalla
     * @param pixelY      coordenada Y del mouse en pantalla (LibGDX: Y invertido)
     * @param offsetX     margen izquierdo del tablero en pantalla
     * @param offsetY     margen superior del tablero en pantalla
     * @param tamCelda    tamaño en píxeles de cada celda
     * @return int[] { fila, columna } o null si está fuera del tablero
     */
    public int[] pixelACelda(float pixelX, float pixelY,
                              float offsetX, float offsetY, float tamCelda) {
        int col = (int) ((pixelX - offsetX) / tamCelda);
        int fila = (int) ((pixelY - offsetY) / tamCelda);
        if (col < 0 || col >= nivel.getTamano()) return null;
        if (fila < 0 || fila >= nivel.getTamano()) return null;
        return new int[]{ fila, col };
    }

    public void iniciarPath(int fila, int columna) {
        if (!terminado && !pausado) tablero.iniciarPath(fila, columna);
    }

    public void extenderPath(int fila, int columna) {
        if (!terminado && !pausado) tablero.extenderPath(fila, columna);
    }

    public void terminarPath() {
        if (!terminado && !pausado) tablero.terminarPath();
    }

    // ---------------------------------------------------------
    // Getters para el HUD y la pantalla
    // ---------------------------------------------------------

    /** Tiempo restante en segundos (puede ser negativo si se agotó). */
    public int getTiempoRestante() {
        int transcurrido = (int)(tiempoTranscurrido / 1000);
        return nivel.getTiempoLimite() - transcurrido;
    }

    /** Tiempo transcurrido en segundos. */
    public int getTiempoTranscurridoSeg() {
        return (int)(tiempoTranscurrido / 1000);
    }

    public Tablero  getTablero()      { return tablero; }
    public Nivel    getNivel()        { return nivel; }
    public int      getNivelNumero()  { return nivelActual; }
    public int      getIntentos()     { return intentos; }
    public boolean  isPausado()       { return pausado; }
    public boolean  isTerminado()     { return terminado; }
    public boolean  isVictoria()      { return victoria; }
    public boolean  esUltimoNivel()   { return nivelActual == 5; }
}
