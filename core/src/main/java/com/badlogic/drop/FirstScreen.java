package com.badlogic.drop;
          
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FirstScreen implements Screen {

    private FlowFreeJuego juego;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;

    private float tamCelda;
    private float offsetX;
    private float offsetY;

    private float tiempoVictoria  = 0f;
    private boolean esperandoAvance = false;
    private static final float DELAY_VICTORIA = 3.0f; //*****************************

    private static final Color[] COLORES = {
        Color.BLACK,
        Color.RED,
        Color.BLUE,
        Color.GREEN,
        Color.YELLOW,
        Color.ORANGE,
        Color.PURPLE
    };

    @Override
    public void show() {
        juego = new FlowFreeJuego(1);
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        calcularLayout();
        configurarInput();
    }

    private void calcularLayout() {
        int tamano = juego.getNivel().getTamano();
        float areaJuego = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) * 0.85f;
        tamCelda = areaJuego / tamano;
        offsetX  = (Gdx.graphics.getWidth()  - areaJuego) / 2f;
        offsetY  = (Gdx.graphics.getHeight() - areaJuego) / 2f;
    }

    private void configurarInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            private int[] ultimaCelda = null;

            //TECLADO
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.R) {
                    juego.reiniciar();
                    // Resetear también el estado de transición de nivel
                    esperandoAvance = false;
                    tiempoVictoria  = 0f;
                }
                return true;
            }

            // MOUSE Y TOUCH 
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

    private int[] pixelACelda(int screenX, int screenY) {
        float worldY = Gdx.graphics.getHeight() - screenY;
        int col  = (int)((screenX - offsetX) / tamCelda);
        int fila = (int)((worldY  - offsetY) / tamCelda);
        int tam  = juego.getNivel().getTamano();
        if (col < 0 || col >= tam || fila < 0 || fila >= tam) return null;
        return new int[]{ fila, col };
    }

    @Override
    public void render(float delta) {
        juego.actualizar();

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        dibujarTablero();
        dibujarHUD();

        if (juego.isTerminado() && juego.isVictoria() && !esperandoAvance) {
            esperandoAvance = true;
            tiempoVictoria  = 0f;
        }

        if (esperandoAvance) {
            tiempoVictoria += delta;
            if (tiempoVictoria >= DELAY_VICTORIA) {
                esperandoAvance = false;
                tiempoVictoria  = 0f;
                if (!juego.esUltimoNivel()) {
                    juego.avanzarNivel();
                    calcularLayout();
                    configurarInput();
                }
            }
        }
    }

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

                shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 1f);
                shapeRenderer.rect(x + margen, y + margen, tamCelda - margen*2, tamCelda - margen*2);

                if (celda.getColor() != Celda.VACIA) {
                    shapeRenderer.setColor(COLORES[celda.getColor()]);
                    if (celda.isEsPunto()) {
                        shapeRenderer.circle(x + tamCelda/2f, y + tamCelda/2f, tamCelda*0.35f, 32);
                    } else {
                        float pad = tamCelda * 0.25f;
                        shapeRenderer.rect(x + pad, y + pad, tamCelda - pad*2, tamCelda - pad*2);
                    }
                }
            }
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.4f, 0.4f, 0.4f, 1f);
        for (int f = 0; f <= tamano; f++) {
            float y = offsetY + f * tamCelda;
            shapeRenderer.line(offsetX, y, offsetX + tamano*tamCelda, y);
        }
        for (int c = 0; c <= tamano; c++) {
            float x = offsetX + c * tamCelda;
            shapeRenderer.line(x, offsetY, x, offsetY + tamano*tamCelda);
        }
        shapeRenderer.end();
    }

    private void dibujarHUD() {
        batch.begin();
        font.draw(batch,
            "Nivel: " + juego.getNivelNumero()
            + "  Tiempo: " + juego.getTiempoRestante() + "s"
            + "  Intentos: " + juego.getIntentos()
            + "  [R] Reiniciar",
            10, Gdx.graphics.getHeight() - 10);

        if (juego.isTerminado()) {
            String msg = juego.isVictoria()
                ? (juego.esUltimoNivel() ? "¡JUEGO COMPLETADO!" : "¡NIVEL COMPLETADO!")
                : "TIEMPO AGOTADO  -  R para reiniciar";
            font.draw(batch, msg, offsetX, offsetY - 10);
        }
        batch.end();
    }

    @Override public void resize(int w, int h) { if (w > 0 && h > 0) calcularLayout(); }
    @Override public void dispose(){ 
        shapeRenderer.dispose(); batch.dispose(); font.dispose(); 
    }
    @Override public void pause(){
    }
    @Override public void resume(){
    }
    @Override public void hide(){
    }
}