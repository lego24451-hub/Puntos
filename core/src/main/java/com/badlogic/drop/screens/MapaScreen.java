package com.badlogic.drop.screens;

import com.badlogic.drop.main.Main;
import com.badlogic.drop.config.Usuarios;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MapaScreen implements Screen {

    private final Main juego;
    private Stage stage;
    private Skin skin;

    // Datos de cada nivel: nombre, dificultad
    private static final String[][] NIVELES = {
        {"Nivel 1", "FÁCIL"},
        {"Nivel 2", "FÁCIL"},
        {"Nivel 3", "MEDIO"},
        {"Nivel 4", "MEDIO"},
        {"Nivel 5", "DIFÍCIL"},
        {"Nivel 6", "EXTREMO"},
    };

    public MapaScreen(Main juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Usuarios usuario = juego.getUsuarioActual();
        int nivelesCompletados = usuario.getEstadisticas().getNivelesCompletados();

        Table tabla = new Table();
        tabla.setFillParent(true);
        tabla.center();
        stage.addActor(tabla);

        // Título
        Label.LabelStyle estiloTitulo = new Label.LabelStyle();
        estiloTitulo.font = skin.getFont("default-font");
        estiloTitulo.fontColor = new Color(0.32f, 0.29f, 0.72f, 1f);

        Label titulo = new Label("Mapa de Niveles", estiloTitulo);
        titulo.setFontScale(1.6f);

        Label subtitulo = new Label("Completa los niveles en orden para desbloquear los siguientes", skin);
        subtitulo.setColor(Color.GRAY);

        tabla.add(titulo).padBottom(5).row();
        tabla.add(subtitulo).padBottom(25).row();

        // Grid de botones de nivel (3 columnas)
        Table gridNiveles = new Table();
        for (int i = 0; i < NIVELES.length; i++) {
            final int numeroNivel = i + 1;
            boolean completado = i < nivelesCompletados;
            boolean disponible = i <= nivelesCompletados;

            String textoBtn = (completado ? "✓ " : "") + NIVELES[i][0] + "\n" + NIVELES[i][1];
            TextButton btnNivel = new TextButton(textoBtn, skin);

            if (completado) {
                btnNivel.setColor(new Color(0.12f, 0.43f, 0.34f, 1f)); // verde
            } else if (!disponible) {
                btnNivel.setColor(Color.DARK_GRAY);
                btnNivel.setDisabled(true);
            }

            if (disponible) {
                btnNivel.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        iniciarNivel(numeroNivel);
                    }
                });
            }

            gridNiveles.add(btnNivel).width(130).height(80).pad(8);
            if (numeroNivel % 3 == 0) gridNiveles.row();
        }

        tabla.add(gridNiveles).padBottom(25).row();

        // Botón volver
        TextButton btnVolver = new TextButton("← Volver al menú", skin);
        tabla.add(btnVolver).width(200).row();

        btnVolver.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                juego.setScreen(new MenuScreen(juego));
            }
        });
    }

    private void iniciarNivel(int numeroNivel) {
        // Solo niveles 1-6 están definidos
        if (numeroNivel < 1 || numeroNivel > 6) {
            Gdx.app.log("MapaScreen", "Nivel " + numeroNivel + " no disponible");
            return;
        }
        // FirstScreen crea su propio FlowFreeJuego en show()
        juego.setScreen(new FirstScreen(juego, numeroNivel));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.95f, 0.95f, 0.97f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { dispose(); }
    @Override public void dispose() { stage.dispose(); skin.dispose(); }
}
