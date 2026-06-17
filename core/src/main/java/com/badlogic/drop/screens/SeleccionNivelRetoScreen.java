package com.badlogic.drop.screens;

import com.badlogic.drop.archivos.GestorSocial;
import com.badlogic.drop.config.Nivel;
import com.badlogic.drop.config.Reto;
import com.badlogic.drop.main.Main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


 // Pantalla donde el retador elige el nivel que quiere usar para el reto.
 // Luego se lanza FirstScreen en "modo reto" para que el retador juegue primero

public class SeleccionNivelRetoScreen implements Screen {

    private final Main   juego;
    private final String usernameRetado;
    private Stage  stage;
    private Skin   skin;

    
    private static final int TOTAL_NIVELES = 6;

    public SeleccionNivelRetoScreen(Main juego, String usernameRetado) {
        this.juego = juego;
        this.usernameRetado  = usernameRetado;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin  = new Skin(Gdx.files.internal("uiskin.json"));

        Table tabla = new Table();
        tabla.setFillParent(true);
        tabla.top().padTop(40);
        stage.addActor(tabla);

        // ── Título
        Label.LabelStyle estiloTitulo = new Label.LabelStyle();
        estiloTitulo.font      = skin.getFont("default-font");
        estiloTitulo.fontColor = new Color(0.32f, 0.29f, 0.72f, 1f);

        Label titulo = new Label("Retar a: " + usernameRetado, estiloTitulo);
        titulo.setFontScale(1.5f);

        Label subtitulo = new Label("Selecciona el nivel del reto:", skin);

        tabla.add(titulo).padBottom(10).row();
        tabla.add(subtitulo).padBottom(25).row();

        // ── Botones de nivel
        for (int i = 1; i <= TOTAL_NIVELES; i++) {
            final int numNivel = i;
            Nivel nivel = Nivel.getNivel(i);

            String textoBtn = "Nivel " + i + " — " + nivel.getDificultad()
                            + " (" + nivel.getTamano() + "x" + nivel.getTamano()
                            + ", " + nivel.getTiempoLimite() + "s)";

            TextButton btn = new TextButton(textoBtn, skin);
            btn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    iniciarRetoEnNivel(numNivel);
                }
            });
            tabla.add(btn).width(400).padBottom(8).row();
        }

        // ── Botón volver 
        TextButton btnVolver = new TextButton("Volver", skin);
        btnVolver.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                juego.setScreen(new BuscarJugadorScreen(juego));
            }
        });
        tabla.add(btnVolver).width(200).padTop(20).row();
    }

    
     // Lanza el juego en modo reto para que el retador juegue primero.
     // Al terminar, FirstScreen (modo reto) creará el Reto y lo enviará.
     
    private void iniciarRetoEnNivel(int nivel) {
        juego.setScreen(new FirstScreen(juego, nivel, usernameRetado));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   { dispose(); }
    @Override public void dispose(){ stage.dispose(); skin.dispose(); }
}