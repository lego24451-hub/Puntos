package com.badlogic.drop;
          
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FirstScreen implements Screen {

    // --- Juego y lógica ---
    private FlowFreeJuego juego;

    // --- Renderizado ---
    private ShapeRenderer shapeRenderer;
    private SpriteBatch   batch;
    private BitmapFont    font;

    // --- Tamaño de cada celda y offset del tablero en pantalla ---
    private float tamCelda;
    private float offsetX;
    private float offsetY;

    // --- Colores de cada índice ---
    private static final Color[] COLORES = {
        Color.BLACK,        // 0 = VACIA
        Color.RED,          // 1 = ROJO
        Color.BLUE,         // 2 = AZUL
        Color.GREEN,        // 3 = VERDE
        Color.YELLOW,       // 4 = AMARILLO
        Color.ORANGE,       // 5 = NARANJA
        Color.PURPLE        // 6 = MORADO
    };

    @Override
    public void show() {
        juego         = new FlowFreeJuego(1); // empieza en nivel 1
        shapeRenderer = new ShapeRenderer();
        batch         = new SpriteBatch();
        font          = new BitmapFont();
        font.setColor(Color.WHITE);

        calcularLayout();
        configurarInput();
    }

    // ---------------------------------------------------------
    // Calcular tamaño y posición del tablero en pantalla
    // ---------------------------------------------------------
    private void calcularLayout() {
        int    tamano     = juego.getNivel().getTamano();
        float  areaJuego  = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) * 0.85f;
        tamCelda = areaJuego / tamano;
        offsetX  = (Gdx.graphics.getWidth()  - areaJuego) / 2f;
        offsetY  = (Gdx.graphics.getHeight() - areaJuego) / 2f;
    }

    // ---------------------------------------------------------
    // Input del mouse
    // ---------------------------------------------------------
    private void configurarInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {

            private int[] ultimaCelda = null;

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                int[] celda = pixelACelda(screenX, screenY);
                if (celda != null) {
                    juego.iniciarPath(celda[0], celda[1]);
                    ultimaCelda = celda;
                }
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                int[] celda = pixelACelda(screenX, screenY);
                if (celda != null && !igualACelda(celda, ultimaCelda)) {
                    juego.extenderPath(celda[0], celda[1]);
                    ultimaCelda = celda;
                }
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                juego.terminarPath();
                ultimaCelda = null;
                return true;
            }

            private boolean igualACelda(int[] a, int[] b) {
                return b != null && a[0] == b[0] && a[1] == b[1];
            }
        });
    }

    // Convierte coordenadas de mouse a celda del grid
    // LibGDX: Y=0 está abajo, pero screenY viene desde arriba → invertir
    private int[] pixelACelda(int screenX, int screenY) {
        float worldY = Gdx.graphics.getHeight() - screenY; // invertir Y
        int col  = (int) ((screenX - offsetX) / tamCelda);
        int fila = (int) ((worldY  - offsetY) / tamCelda);
        int tam  = juego.getNivel().getTamano();
        if (col < 0 || col >= tam || fila < 0 || fila >= tam) return null;
        return new int[]{ fila, col };
    }

    // ---------------------------------------------------------
    // Render
    // ---------------------------------------------------------
    @Override
    public void render(float delta) {
        juego.actualizar();

        // Limpiar pantalla
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        dibujarTablero();
        dibujarHUD();

        // Si ganó, mostrar mensaje
        if (juego.isTerminado() && juego.isVictoria()) {
            if (!juego.esUltimoNivel()) {
                juego.avanzarNivel();
                calcularLayout();
                configurarInput();
            }
        }
    }

    // ---------------------------------------------------------
    // Dibujar el tablero
    // ---------------------------------------------------------
    private void dibujarTablero() {
        Tablero tablero = juego.getTablero();
        int     tamano  = juego.getNivel().getTamano();
        float   margen  = 3f;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int f = 0; f < tamano; f++) {
            for (int c = 0; c < tamano; c++) {
                Celda celda = tablero.getCelda(f, c);
                float x = offsetX + c * tamCelda;
                float y = offsetY + f * tamCelda;

                // Fondo de celda (gris oscuro)
                shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1f);
                shapeRenderer.rect(x + margen, y + margen,
                                   tamCelda - margen * 2, tamCelda - margen * 2);

                // Color del camino o punto
                if (celda.getColor() != Celda.VACIA) {
                    Color color = COLORES[celda.getColor()];
                    shapeRenderer.setColor(color);

                    if (celda.isEsPunto()) {
                        // Punto fijo: círculo grande
                        float radio = tamCelda * 0.35f;
                        float cx = x + tamCelda / 2f;
                        float cy = y + tamCelda / 2f;
                        shapeRenderer.circle(cx, cy, radio, 32);
                    } else {
                        // Camino: rectángulo más pequeño
                        float pad = tamCelda * 0.25f;
                        shapeRenderer.rect(x + pad, y + pad,
                                           tamCelda - pad * 2, tamCelda - pad * 2);
                    }
                }
            }
        }

        shapeRenderer.end();

        // Líneas del grid (encima del contenido)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.4f, 0.4f, 0.4f, 1f);
        for (int f = 0; f <= tamano; f++) {
            float y = offsetY + f * tamCelda;
            shapeRenderer.line(offsetX, y, offsetX + tamano * tamCelda, y);
        }
        for (int c = 0; c <= tamano; c++) {
            float x = offsetX + c * tamCelda;
            shapeRenderer.line(x, offsetY, x, offsetY + tamano * tamCelda);
        }
        shapeRenderer.end();
    }

    // ---------------------------------------------------------
    // HUD: nivel, tiempo, intentos
    // ---------------------------------------------------------
    private void dibujarHUD() {
        batch.begin();
        font.draw(batch, "Nivel: "    + juego.getNivelNumero()
                       + "  Tiempo: " + juego.getTiempoRestante() + "s"
                       + "  Intentos: " + juego.getIntentos(),
                  10, Gdx.graphics.getHeight() - 10);

        if (juego.isTerminado()) {
            String msg = juego.isVictoria() ? "¡NIVEL COMPLETADO!" : "TIEMPO AGOTADO - R para reiniciar";
            font.draw(batch, msg, offsetX, offsetY - 10);
        }
        batch.end();
    }

    // ---------------------------------------------------------
    // Ciclo de vida
    // ---------------------------------------------------------
    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        calcularLayout();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
    }

    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}
}
