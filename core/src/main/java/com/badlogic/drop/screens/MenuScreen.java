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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import java.io.File;

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

        
        Label.LabelStyle estiloTitulo = new Label.LabelStyle();
        estiloTitulo.font = skin.getFont("default-font");
        estiloTitulo.fontColor = new Color(0.32f, 0.29f, 0.72f, 1f);

        
        Label titulo = new Label("Flow Free", estiloTitulo);
        titulo.setFontScale(1.8f);

        Label bienvenida = new Label("Bienvenido, " + usuario.getUsername(), skin);
        Label infoNombre = new Label(usuario.getNombreCompleto(), skin);
        infoNombre.setColor(Color.GRAY);

        String ultimaSesion = usuario.getUltimaSesion() != null
      ? "Última sesión: " + usuario.getUltimaSesion()
           .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
     : "Primera sesión";
     Label lblUltimaSesion = new Label(ultimaSesion, skin);
        lblUltimaSesion.setColor(Color.GRAY);
        
        
        Image imgAvatar;
        try{
            String rutaAvatar = usuario.getAvatar();
            if (rutaAvatar != null && !rutaAvatar.isEmpty() && !rutaAvatar.equals("avatar_default.png")){
                File f = new File (rutaAvatar);
                if (f.exists()){
                    Texture texAvatar = new Texture (Gdx.files.absolute(rutaAvatar));
                    imgAvatar = new Image (new TextureRegionDrawable(new TextureRegion(texAvatar)));
                    
                } else{
                    imgAvatar = new Image();
                } 
                
            } 
            else {
                imgAvatar = new Image();
            }
        }catch (Exception e){
            imgAvatar = new Image();
        }
        TextButton btnCambiarAvatar = new TextButton ("Cambiar avatar", skin);
        
        Label lblPartidas = new Label("Partidas jugadas: " + stats.getPartidasJugadas(), skin);
        Label lblNiveles  = new Label("Niveles completados: " + stats.getNivelesCompletados(), skin);
        Label lblRanking  = new Label("Ranking: #" + usuario.getRanking(), skin);

       
        Label separador = new Label("─────────────────", skin);
        separador.setColor(Color.LIGHT_GRAY);

        
        TextButton btnJugar      = new TextButton("Jugar", skin);
        TextButton btnEstadisticas = new TextButton("Estadísticas", skin);
        TextButton btnRankings = new TextButton("Rankings", skin);
        TextButton btnCerrarSesion = new TextButton("Cerrar sesión", skin);

        float ancho = 280f;

        
        tabla.add(titulo).padBottom(5).row();
        tabla.add(imgAvatar).size(80,80).padBottom(4).row();
        tabla.add(btnCambiarAvatar).width(180).padBottom(10).row();
        tabla.add(bienvenida).padBottom(2).row();
        tabla.add(infoNombre).padBottom(20).row();
        tabla.add(lblUltimaSesion).padBottom(20).row();
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
        
        btnCambiarAvatar.addListener(new ChangeListener() {
    @Override
    public void changed(ChangeEvent event, Actor actor) {
        juego.setScreen(new AvatarScreen(juego));
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
