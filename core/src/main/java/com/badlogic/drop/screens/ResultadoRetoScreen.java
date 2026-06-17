package com.badlogic.drop.screens;

import com.badlogic.drop.archivos.GestorSocial;
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


 //Pantalla que muestra el resultado final después de que el RETADO terminó de jugar.
 
 //Muestra:
 //- Puntaje del retado (jugador actual)
 // - Puntaje del retador
 // - Quién ganó
 
 // Luego guarda el reto como FINALIZADO (o RECHAZADO si lo rechazó).
 
public class ResultadoRetoScreen implements Screen {

    private final Main juego;
    private final Reto reto;
    private Stage  stage;
    private Skin   skin;

    public ResultadoRetoScreen(Main juego, Reto reto) {
        this.juego = juego;
        this.reto  = reto;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin  = new Skin(Gdx.files.internal("uiskin.json"));

        Table tabla = new Table();
        tabla.setFillParent(true);
        tabla.center();
        stage.addActor(tabla);

        // ── Título 
        Label.LabelStyle estiloTitulo = new Label.LabelStyle();
        estiloTitulo.font      = skin.getFont("default-font");
        estiloTitulo.fontColor = new Color(0.32f, 0.29f, 0.72f, 1f);

        Label titulo = new Label("Resultado del Reto", estiloTitulo);
        titulo.setFontScale(2f);
        tabla.add(titulo).padBottom(30).row();

        // ── Nivel jugado 
        Label lblNivel = new Label("Nivel " + reto.getNivelNumero(), skin);
        lblNivel.setFontScale(1.2f);
        tabla.add(lblNivel).padBottom(20).row();

        // ── Puntajes 
        tabla.add(new Label("─── Puntajes ───", skin)).padBottom(10).row();

        Label lblRetador = new Label(
            reto.getUsernameRetador() + " (retador): " + reto.getPuntajeRetador() + " pts", skin);
        Label lblRetado  = new Label(
            reto.getUsernameRetado()  + " (tú):       " + reto.getPuntajeRetado()  + " pts", skin);

        tabla.add(lblRetador).padBottom(5).row();
        tabla.add(lblRetado).padBottom(20).row();

        // ── Ganador
        Label lblGanador;
        String ganador = reto.getGanador();
        String usernameActual = juego.getUsuarioActual().getUsername();

        if (ganador == null) {
            lblGanador = new Label("¡Empate!", skin);
            lblGanador.setColor(Color.YELLOW);
        } else if (ganador.equals(usernameActual)) {
            lblGanador = new Label("¡Ganaste! 🏆", skin);
            lblGanador.setColor(Color.GREEN);
        } else {
            lblGanador = new Label("Perdiste  😞", skin);
            lblGanador.setColor(Color.RED);
        }
        lblGanador.setFontScale(1.8f);
        tabla.add(lblGanador).padBottom(30).row();

        // ── Botón volver al menú 
        TextButton btnMenu = new TextButton("Volver al Menú", skin);
        btnMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                juego.setScreen(new MenuScreen(juego));
            }
        });

        TextButton btnInbox = new TextButton("Ver Inbox", skin);
        btnInbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                juego.setScreen(new InboxScreen(juego));
            }
        });

        tabla.add(btnInbox).width(200).padBottom(8).row();
        tabla.add(btnMenu).width(200).row();
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