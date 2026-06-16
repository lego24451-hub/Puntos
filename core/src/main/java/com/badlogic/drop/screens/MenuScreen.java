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

public class MenuScreen implements Screen {

    private final Main juego;
    private Stage stage;
    private Skin skin;

    public MenuScreen(Main juego) {
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
        tabla.center();
        ScrollPane scroll = new ScrollPane (tabla,skin);
        scroll.setFillParent(true);
        stage.addActor(scroll);

        // Estilo de título
        Label.LabelStyle estiloTitulo = new Label.LabelStyle();
        estiloTitulo.font = skin.getFont("default-font");
        estiloTitulo.fontColor = new Color(0.32f, 0.29f, 0.72f, 1f);

        // Encabezado
        Label titulo = new Label("Flow Free Game", estiloTitulo);
        titulo.setFontScale(1.8f);

        Label bienvenida = new Label("Bienvenido, " + usuario.getUsername(), skin);
        Label infoNombre = new Label(usuario.getNombreCompleto(), skin);
        infoNombre.setColor(Color.GRAY);

        // Stats rápidas
        Label lblPartidas = new Label("Partidas jugadas: " + stats.getPartidasJugadas(), skin);
        Label lblNiveles  = new Label("Niveles completados: " + stats.getNivelesCompletados(), skin);
        Label lblRanking  = new Label("Ranking: #" + usuario.getRanking(), skin);

        // Separador
        Label separador = new Label("─────────────────", skin);
        separador.setColor(Color.LIGHT_GRAY);

        // Botones del menú
        TextButton btnJugar      = new TextButton("Jugar", skin);
        TextButton btnEstadisticas = new TextButton("Estadísticas", skin);
        TextButton btnRankings = new TextButton("Rankings", skin);
        TextButton btnCerrarSesion = new TextButton("Cerrar sesión", skin);

        float ancho = 280f;

        // Layout
        tabla.add(titulo).padBottom(5).row();
        tabla.add(bienvenida).padBottom(2).row();
        tabla.add(infoNombre).padBottom(20).row();
        tabla.add(separador).padBottom(10).row();
        tabla.add(lblPartidas).left().padBottom(4).row();
        tabla.add(lblNiveles).left().padBottom(4).row();
        tabla.add(lblRanking).left().padBottom(20).row();
        tabla.add(separador).padBottom(15).row();
        tabla.add(btnRankings).width(ancho).padBottom(10).row();
        tabla.add(btnJugar).width(ancho).padBottom(10).row();
        tabla.add(btnEstadisticas).width(ancho).padBottom(10).row();
        tabla.add(btnCerrarSesion).width(ancho).row();

        // Acciones
        btnJugar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                juego.setScreen(new MapaScreen(juego));
            }
        });

        btnEstadisticas.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                juego.setScreen(new EstadisticasScreen(juego));
            }
        });
        
        btnRankings.addListener(new ChangeListener(){
            public void changed (ChangeListener.ChangeEvent event, Actor actor){
                juego.setScreen(new RankingScreen(juego));
            }
        });
        
        
        
        btnCerrarSesion.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                juego.setUsuarioActual(null);
                juego.setScreen(new LoginScreen(juego));
            }
        });
        
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
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
