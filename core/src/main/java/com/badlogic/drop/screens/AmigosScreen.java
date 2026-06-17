package com.badlogic.drop.screens;

import com.badlogic.drop.archivos.GestorArchivos;
import com.badlogic.drop.config.Textos;
import com.badlogic.drop.config.Usuarios;
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

import java.util.ArrayList;

public class AmigosScreen implements Screen {

    private final Main juego;
    private Stage stage;
    private Skin  skin;

    public AmigosScreen(Main juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Usuarios usuario = juego.getUsuarioActual();
        ArrayList<String> amigos = usuario.getAmigos();

        // ── Layout principal con scroll 
        Table contenido = new Table();
        contenido.top().padTop(15).padLeft(15).padRight(15);

        ScrollPane scroll = new ScrollPane(contenido, skin);
        scroll.setFillParent(true);
        scroll.setScrollingDisabled(true, false);
        stage.addActor(scroll);

        // ── Título
        Label.LabelStyle estiloTitulo = new Label.LabelStyle();
        estiloTitulo.font = skin.getFont("default-font");
        estiloTitulo.fontColor = new Color(0.32f, 0.29f, 0.72f, 1f);

        Label titulo = new Label(Textos.AMIGOS_TITULO(), estiloTitulo);
        titulo.setFontScale(1.6f);
        contenido.add(titulo).padBottom(20).row();

        // ── Lista de amigos 
        if (amigos == null || amigos.isEmpty()) {
            contenido.add(new Label(Textos.SIN_AMIGOS(), skin))
                     .left().padBottom(20).row();
        } else {
            for (final String amigo : amigos) {
                Table fila = new Table();
                fila.left();

                // Cargar datos del amigo para mostrar info
                Usuarios data = GestorArchivos.cargarUsuario(amigo);
                String info = amigo;
                if (data != null) {
                    info = amigo + "  |  " + data.getNombreCompleto()
                         + "  (#" + data.getRanking() + ")";
                }

                Label lblAmigo = new Label(info, skin);
                TextButton btnRetar = new TextButton(Textos.RETAR(), skin);

                btnRetar.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                        juego.setScreen(new SeleccionNivelRetoScreen(juego, amigo));
                    }
                });

                fila.add(lblAmigo).expandX().left().padRight(10);
                fila.add(btnRetar);

                contenido.add(fila).fillX().padBottom(8).row();
            }
        }

        // ── Botón volver 
        contenido.add(new Label("", skin)).padBottom(15).row(); // separador
        TextButton btnVolver = new TextButton(Textos.VOLVER_MENU(), skin);
        btnVolver.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                juego.setScreen(new MenuScreen(juego));
            }
        });
        contenido.add(btnVolver).width(200).padTop(10).row();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause(){}
    @Override public void resume(){}
    @Override public void hide(){ dispose(); }
    @Override public void dispose(){ stage.dispose(); skin.dispose(); }
}
