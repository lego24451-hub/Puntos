package com.badlogic.drop.screens;

import com.badlogic.drop.main.Main;
import com.badlogic.drop.config.Usuarios;
import com.badlogic.drop.config.Estadisticas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.HashMap;
import java.util.Map;

public class EstadisticasScreen implements Screen {

    private final Main juego;
    private Stage stage;
    private Skin skin;

    public EstadisticasScreen(Main juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Usuarios usuario = juego.getUsuarioActual();
        Estadisticas stats = usuario.getEstadisticas();

        Table tabla = new Table();
        tabla.setFillParent(true);
        tabla.center();
        stage.addActor(tabla);

        // Título
        Label.LabelStyle estiloTitulo = new Label.LabelStyle();
        estiloTitulo.font = skin.getFont("default-font");
        estiloTitulo.fontColor = new Color(0.32f, 0.29f, 0.72f, 1f);

        Label titulo = new Label("Estadísticas", estiloTitulo);
        titulo.setFontScale(1.6f);
        Label subUsuario = new Label(usuario.getUsername(), skin);
        subUsuario.setColor(Color.GRAY);

        Label separador = new Label("─────────────────", skin);
        separador.setColor(Color.LIGHT_GRAY);

        // Datos generales
        Label lblPartidas  = new Label("Partidas jugadas:       " + stats.getPartidasJugadas(), skin);
        Label lblCompletados = new Label("Niveles completados:    " + stats.getNivelesCompletados(), skin);
        Label lblTiempo    = new Label("Tiempo total jugado:    " + formatearTiempo(stats.getTiempoTotalJugado()), skin);
        Label lblRanking   = new Label("Ranking:                #" + usuario.getRanking(), skin);

        // Mejor tiempo por nivel
        Label lblMejoresTiempos = new Label("Mejor tiempo por nivel:", skin);
        lblMejoresTiempos.setColor(new Color(0.32f, 0.29f, 0.72f, 1f));

        Table tablaTiempos = new Table();
        HashMap<Integer, Long> tiempos = stats.getTiempoPorNivel();
        if (tiempos == null || tiempos.isEmpty()) {
            tablaTiempos.add(new Label("  Sin registros aún.", skin)).left().row();
        } else {
            for (Map.Entry<Integer, Long> entrada : tiempos.entrySet()) {
                String fila = "  Nivel " + entrada.getKey() + ":  " + formatearTiempo(entrada.getValue());
                tablaTiempos.add(new Label(fila, skin)).left().padBottom(3).row();
            }
        }

        // Layout
        tabla.add(titulo).padBottom(4).row();
        tabla.add(subUsuario).padBottom(20).row();
        tabla.add(separador).padBottom(12).row();
        tabla.add(lblPartidas).left().padBottom(6).row();
        tabla.add(lblCompletados).left().padBottom(6).row();
        tabla.add(lblTiempo).left().padBottom(6).row();
        tabla.add(lblRanking).left().padBottom(20).row();
        tabla.add(separador).padBottom(12).row();
        tabla.add(lblMejoresTiempos).left().padBottom(8).row();
        tabla.add(tablaTiempos).left().padBottom(25).row();

        // Botón volver
        TextButton btnVolver = new TextButton("← Volver al menú", skin);
        tabla.add(btnVolver).width(220).row();

        btnVolver.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                juego.setScreen(new MenuScreen(juego));
            }
        });
    }

    private String formatearTiempo(long segundos) {
        long min = segundos / 60;
        long seg = segundos % 60;
        if (min > 0) return min + "m " + seg + "s";
        return seg + "s";
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
