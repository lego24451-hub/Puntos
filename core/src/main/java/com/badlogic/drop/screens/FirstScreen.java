package com.badlogic.drop.screens;

import com.badlogic.drop.archivos.GestorArchivos;
import com.badlogic.drop.archivos.GestorSocial;
import com.badlogic.drop.config.Celda;
import com.badlogic.drop.config.Estadisticas;
import com.badlogic.drop.config.Reto;
import com.badlogic.drop.config.Tablero;
import com.badlogic.drop.config.Textos;
import com.badlogic.drop.config.Usuarios;
import com.badlogic.drop.juego.FlowFreeJuego;
import com.badlogic.drop.juego.HiloJuego;
import com.badlogic.drop.main.Main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


public class FirstScreen implements Screen {

    // ── Modos de juego 
    private enum ModoJuego { NORMAL, RETADOR, RETADO }

    // ── Campos base 
    private final Main   juego;
    private final int    nivelInicial;
    private final ModoJuego modo;

    // Modo RETADOR: username del jugador al que se reta
    private final String usernameRetado;

    // Modo RETADO: el objeto Reto que fue aceptado
    private final Reto   retoPendiente;

    // ── Infraestructura del juego 
    private OrthographicCamera camara;
    private FlowFreeJuego flowJuego;
    private HiloJuego hiloJuego;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;

    private float tamCelda, offsetX, offsetY;
    private float tiempoVictoria  = 0f;
    private boolean esperandoAvance = false;
    private static final float DELAY_VICTORIA = 3.0f;

    // ── Autoguardado cada 30 segundos ──
    private float tiempoAutoguardado = 0f;
    private static final float INTERVALO_AUTOGUARDADO = 30f;

    private static final Color[] COLORES = {
        Color.BLACK, Color.RED,    Color.BLUE,   Color.GREEN,
        Color.YELLOW, Color.ORANGE, Color.PURPLE, Color.PINK
    };

    
    public FirstScreen(Main juego, int nivelInicial) {
        this.juego = juego;
        this.nivelInicial = nivelInicial;
        this.modo = ModoJuego.NORMAL;
        this.usernameRetado  = null;
        this.retoPendiente   = null;
    }

    
     // Modo RETADOR: el jugador actual reta a {@code usernameRetado}.
     //Al terminar, se crea el Reto con el puntaje obtenido y se envía.
     
    public FirstScreen(Main juego, int nivelInicial, String usernameRetado) {
        this.juego           = juego;
        this.nivelInicial    = nivelInicial;
        this.modo            = ModoJuego.RETADOR;
        this.usernameRetado  = usernameRetado;
        this.retoPendiente   = null;
    }

    
     //Modo RETADO: el jugador actual acepta un reto existente.
      //Al terminar, se registra el puntaje en el Reto y se muestra el resultado.
     
    public FirstScreen(Main juego, int nivelInicial, Reto retoPendiente) {
        this.juego           = juego;
        this.nivelInicial    = nivelInicial;
        this.modo            = ModoJuego.RETADO;
        this.usernameRetado  = null;
        this.retoPendiente   = retoPendiente;
    }

    
    @Override
    public void show() {
        flowJuego  = new FlowFreeJuego(nivelInicial);
        hiloJuego = new HiloJuego(flowJuego);
        shapeRenderer = new ShapeRenderer();
        batch  = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        camara = new OrthographicCamera();
        camara.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.setProjectionMatrix(camara.combined);
        batch.setProjectionMatrix(camara.combined);

        calcularLayout();
        configurarInput();
        hiloJuego.start();
    }

    private void calcularLayout() {
        int   tamano   = flowJuego.getNivel().getTamano();
        float areaJuego = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) * 0.85f;
        tamCelda = areaJuego / tamano;
        offsetX  = (Gdx.graphics.getWidth()  - areaJuego) / 2f;
        offsetY  = (Gdx.graphics.getHeight() - areaJuego) / 2f;
    }

    private void configurarInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            private int[] ultimaCelda = null;

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.R) {
                    flowJuego.reiniciar();
                    esperandoAvance = false;
                    tiempoVictoria  = 0f;
                }
                if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.M) {
                    volverAlMenu();
                }
                return true;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                int[] celda = pixelACelda(screenX, screenY);
                if (celda != null) {
                    flowJuego.iniciarPath(celda[0], celda[1]);
                    ultimaCelda = celda;
                }
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                int[] celda = pixelACelda(screenX, screenY);
                if (celda != null && !igualACelda(celda, ultimaCelda)) {
                    flowJuego.extenderPath(celda[0], celda[1]);
                    ultimaCelda = celda;
                }
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                flowJuego.terminarPath();
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
        int tam  = flowJuego.getNivel().getTamano();
        if (col < 0 || col >= tam || fila < 0 || fila >= tam) return null;
        return new int[]{ fila, col };
    }

   
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        dibujarTablero();
        dibujarHUD();

        if (flowJuego.isTerminado() && flowJuego.isVictoria() && !esperandoAvance) {
            esperandoAvance = true;
            tiempoVictoria  = 0f;
            registrarEstadisticas();
        }

        if (esperandoAvance) {
            tiempoVictoria += delta;
            if (tiempoVictoria >= DELAY_VICTORIA) {
                esperandoAvance = false;
                tiempoVictoria  = 0f;
                manejarFinDeNivel();
            }
        }

        // ── Autoguardado periódico cada 30 segundos ──
        if (juego.getUsuarioActual() != null && !flowJuego.isTerminado()) {
            tiempoAutoguardado += delta;
            if (tiempoAutoguardado >= INTERVALO_AUTOGUARDADO) {
                tiempoAutoguardado = 0f;
                GestorArchivos.guardarUsuario(juego.getUsuarioActual());
            }
        }
    }

    private void manejarFinDeNivel() {
        switch (modo) {
            case NORMAL:
                // Comportamiento original: avanzar nivel o volver al menú
                if (!flowJuego.esUltimoNivel()) {
                    flowJuego.avanzarNivel();
                    calcularLayout();
                    configurarInput();
                } else {
                    volverAlMenu();
                }
                break;

            case RETADOR:
                // Crear el reto con el puntaje obtenido y enviarlo al retado
                int puntajeRetador = flowJuego.getPuntaje();
                Reto nuevoReto = new Reto(
                    juego.getUsuarioActual().getUsername(),
                    usernameRetado,
                    flowJuego.getNivelNumero(),
                    puntajeRetador
                );
                GestorSocial.enviarReto(nuevoReto);

                // Mostrar confirmación y volver al menú
                // Mostrar mensaje en inbox y volver al menú
juego.setScreen(new MenuScreen(juego));
                break;

            case RETADO:
                // Registrar puntaje del retado en el reto y guardar
                int puntajeRetado = flowJuego.getPuntaje();
                retoPendiente.registrarPuntajeRetado(puntajeRetado);
                GestorSocial.actualizarReto(retoPendiente);

                // Mostrar pantalla de resultado
                juego.setScreen(new ResultadoRetoScreen(juego, retoPendiente));
                break;
        }
    }

    

    private void registrarEstadisticas() {
        if (juego.getUsuarioActual() == null) return;
        Usuarios u = juego.getUsuarioActual();
        Estadisticas stats = u.getEstadisticas();

        int  nivel = flowJuego.getNivelNumero();
        long tiempo = flowJuego.getTiempoTranscurridoSeg();
        int  puntaje = flowJuego.getPuntaje();

        stats.registrarPartida(nivel, tiempo);
        stats.registrarNivelCompletado(nivel);
        stats.actualizarPuntajeMaximo(nivel, puntaje);

        int rankingTotal = 0;
        for (int p : stats.getPuntajeMaximoPorNivel().values()) rankingTotal += p;
        u.setRanking(rankingTotal);

        GestorArchivos.guardarUsuario(u);
    }

    private void volverAlMenu() {
        if (juego.getUsuarioActual() != null)
            GestorArchivos.guardarUsuario(juego.getUsuarioActual());
        juego.setScreen(new MenuScreen(juego));
    }

    
    private void dibujarTablero() {
        Tablero tablero = flowJuego.getTablero();
        int tamano = flowJuego.getNivel().getTamano();
        float margen = 3f;

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

        // Etiqueta de modo reto en la parte superior
        if (modo == ModoJuego.RETADOR) {
            font.draw(batch, "MODO RETO — Retando a: " + usernameRetado,
                10, Gdx.graphics.getHeight() - 30);
        } else if (modo == ModoJuego.RETADO && retoPendiente != null) {
            font.draw(batch, "MODO RETO — Reto de: " + retoPendiente.getUsernameRetador()
                + " | Su puntaje: " + retoPendiente.getPuntajeRetador() + " pts",
                10, Gdx.graphics.getHeight() - 30);
        }

        String hudTexto = Textos.NIVEL() + flowJuego.getNivelNumero()
            + "  " + Textos.TIEMPO() + flowJuego.getTiempoRestante() + "s"
            + Textos.INTENTOS() + flowJuego.getIntentos() + Textos.DE() + flowJuego.getVidas()
            + Textos.REINICIAR() + Textos.MENU();
        font.draw(batch, hudTexto, 10, Gdx.graphics.getHeight() - 50);

        if (flowJuego.isTerminado() && flowJuego.isVictoria()) {
            batch.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.setColor(0, 0, 0, 0.65f);
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.gl.glDisable(GL20.GL_BLEND);
            shapeRenderer.end();

            batch.begin();

            float centroX = Gdx.graphics.getWidth()  / 2f;
            float centroY = Gdx.graphics.getHeight() / 2f;
            GlyphLayout layout = new GlyphLayout();

            font.getData().setScale(3f);
            String tituloVic = (modo == ModoJuego.NORMAL && flowJuego.esUltimoNivel())
                ? Textos.JUEGO_COMPLETADO() : Textos.GANASTE();
            layout.setText(font, tituloVic);
            font.draw(batch, tituloVic, centroX - layout.width / 2f, centroY + 80);
            font.getData().setScale(1f);

            font.getData().setScale(1.5f);
            String puntajeStr = Textos.PUNTAJE() + flowJuego.getPuntaje() + " " + Textos.PTS();
            layout.setText(font, puntajeStr);
            font.draw(batch, puntajeStr, centroX - layout.width / 2f, centroY + 20);
            font.getData().setScale(1f);

            // En modo RETADO: mostrar puntaje del retador para comparar
            if (modo == ModoJuego.RETADO && retoPendiente != null) {
                font.getData().setScale(1.2f);
                String rivalStr = "Puntaje rival: " + retoPendiente.getPuntajeRetador() + " pts";
                layout.setText(font, rivalStr);
                font.draw(batch, rivalStr, centroX - layout.width / 2f, centroY - 15);
                font.getData().setScale(1f);
            }

            if (juego.getUsuarioActual() != null && modo == ModoJuego.NORMAL) {
                int nivel = flowJuego.getNivelNumero();
                int mejorPuntaje = juego.getUsuarioActual().getEstadisticas().getPuntajeMaximo(nivel);
                if (mejorPuntaje > 0) {
                    String mejorStr = Textos.MEJOR_PUNTAJE() + mejorPuntaje + " " + Textos.PTS();
                    layout.setText(font, mejorStr);
                    font.draw(batch, mejorStr, centroX - layout.width / 2f, centroY - 20);
                }
            }

            font.getData().setScale(0.8f);
            String continuarStr;
            if (modo == ModoJuego.NORMAL && !flowJuego.esUltimoNivel()) {
                int segRest = (int)(DELAY_VICTORIA - tiempoVictoria + 1);
                continuarStr = Textos.SIGUIENTE_EN() + segRest + "...";
            } else if (modo == ModoJuego.RETADOR) {
                int segRest = (int)(DELAY_VICTORIA - tiempoVictoria + 1);
                continuarStr = "Enviando reto en " + segRest + "s...";
            } else if (modo == ModoJuego.RETADO) {
                int segRest = (int)(DELAY_VICTORIA - tiempoVictoria + 1);
                continuarStr = "Calculando resultado en " + segRest + "s...";
            } else {
                continuarStr = Textos.VOLVER_MENU_MSG();
            }
            layout.setText(font, continuarStr);
            font.draw(batch, continuarStr, centroX - layout.width / 2f, centroY - 70);
            font.getData().setScale(1f);
        }

        if (flowJuego.isTerminado() && !flowJuego.isVictoria()) {
            batch.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.setColor(0, 0, 0, 0.65f);
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.gl.glDisable(GL20.GL_BLEND);
            shapeRenderer.end();

            batch.begin();

            float centroX = Gdx.graphics.getWidth()  / 2f;
            float centroY = Gdx.graphics.getHeight() / 2f;
            GlyphLayout layout = new GlyphLayout();

            font.getData().setScale(3f);
            String perdiste = Textos.PERDISTE();
            layout.setText(font, perdiste);
            font.draw(batch, perdiste, centroX - layout.width / 2f, centroY + 40);
            font.getData().setScale(1f);

            String mensaje = Textos.REINTENTAR_MSG();
            layout.setText(font, mensaje);
            font.draw(batch, mensaje, centroX - layout.width / 2f, centroY - 20);
        }

        batch.end();
    }

    @Override
    public void resize(int w, int h) {
        if (w <= 0 || h <= 0) return;
        camara.setToOrtho(false, w, h);
        camara.update();
        shapeRenderer.setProjectionMatrix(camara.combined);
        batch.setProjectionMatrix(camara.combined);
        calcularLayout();
    }

    @Override public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
        font.dispose();
        if (hiloJuego != null) hiloJuego.detener();
    }

    @Override public void pause(){}
    @Override public void resume() {}
    @Override public void hide(){ dispose(); }
}