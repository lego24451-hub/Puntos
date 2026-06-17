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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.Arrays;
import java.util.Comparator;

public class RankingScreen implements Screen {

    private final Main juego;
    private Stage stage;
    private Skin skin;

    public RankingScreen(Main juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table tabla = new Table();
        tabla.setFillParent(true);
        tabla.center();
        stage.addActor(tabla);

        Label.LabelStyle estiloTitulo = new Label.LabelStyle();
        estiloTitulo.font = skin.getFont("default-font");
        estiloTitulo.fontColor = new Color(0.32f, 0.29f, 0.72f, 1f);

        Label titulo = new Label(Textos.RANKING_BTN(), estiloTitulo);
        titulo.setFontScale(1.6f);
        tabla.add(titulo).padBottom(20).row();

        Usuarios[] usuarios = GestorArchivos.cargarTodosLosUsuarios();
        if (usuarios == null || usuarios.length == 0) {
            tabla.add(new Label(t("No hay jugadores registrados", "No players registered"), skin)).padBottom(20).row();
        } else {
            Arrays.sort(usuarios, new Comparator<Usuarios>() {
                @Override
                public int compare(Usuarios a, Usuarios b) {
                    if (a == null && b == null) return 0;
                    if (a == null) return 1;
                    if (b == null) return -1;
                    return Integer.compare(b.getRanking(), a.getRanking());
                }
            });
        }

        Table encabezado = new Table();
        Label lblNum = new Label("#", skin);
        Label lblUser = new Label(t("Usuario", "User"), skin);
        Label lblPts = new Label(t("Puntaje Total", "Total Score"), skin);
        lblNum.setColor(Color.LIGHT_GRAY);
        lblUser.setColor(Color.LIGHT_GRAY);
        lblPts.setColor(Color.LIGHT_GRAY);
        encabezado.add(lblNum).width(30).padRight(20);
        encabezado.add(lblUser).width(150).padRight(20);
        encabezado.add(lblPts).width(120);
        tabla.add(encabezado).padBottom(8).row();

        if (usuarios == null || usuarios.length == 0) {
            tabla.add(new Label(t("No hay jugadores registrados", "No players registered"), skin)).padBottom(20).row();
        } else {
            for (int i = 0; i < usuarios.length; i++) {
                Usuarios u = usuarios[i];
                if (u == null) continue;
                String medalla;
                if (i == 0) medalla = "#1";
                else if (i == 1) medalla = "#2";
                else if (i == 2) medalla = "#3";
                else medalla = "#" + (i + 1);

                Table fila = new Table();
                fila.add(new Label(medalla, skin)).width(30).padRight(20);
                fila.add(new Label(u.getUsername(), skin)).width(150).padRight(20);
                fila.add(new Label(u.getRanking() + " " + Textos.PTS(), skin)).width(120);

                if (juego.getUsuarioActual() != null && u.getUsername().equals(juego.getUsuarioActual().getUsername())) {
                    fila.setBackground(skin.newDrawable("white", new Color(0.3f, 0.3f, 0.7f, 0.3f)));
                }
                tabla.add(fila).padBottom(6).row();
            }
        }

        tabla.add().padBottom(15).row();
        TextButton btnVolver = new TextButton(Textos.VOLVER_MENU(), skin);
        tabla.add(btnVolver).width(220).row();

        btnVolver.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                juego.setScreen(new MenuScreen(juego));
            }
        });
    }

    private String t(String es, String en) {
        return Textos.isIngles() ? en : es;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h){ 
        stage.getViewport().update(w, h, true); 
    }
    @Override public void pause(){
    }
    @Override public void resume(){
    }
    @Override public void hide(){ 
        dispose(); 
    }
    @Override public void dispose(){
        stage.dispose(); skin.dispose(); 
    }
}
