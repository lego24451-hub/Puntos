package com.badlogic.drop.screens;

import com.badlogic.drop.archivos.GestorArchivos;
import com.badlogic.drop.main.Main;
import com.badlogic.drop.config.Textos;
import com.badlogic.drop.config.Usuarios;
import com.badlogic.drop.config.Estadisticas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
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
        tabla.setFillParent(true);
        tabla.center();
        stage.addActor(tabla);

        Label.LabelStyle estiloTitulo = new Label.LabelStyle();
        estiloTitulo.font = skin.getFont("default-font");
        estiloTitulo.fontColor = new Color(0.32f, 0.29f, 0.72f, 1f);

        Label titulo = new Label(Textos.TITULO_JUEGO(), estiloTitulo);
        titulo.setFontScale(1.8f);

        Label bienvenida = new Label(Textos.BIENVENIDO() + usuario.getUsername(), skin);
        Label infoNombre = new Label(usuario.getNombreCompleto(), skin);
        infoNombre.setColor(Color.WHITE);

        // Avatar personalizado (si existe)
        Image imgAvatar = cargarAvatar(usuario);
        if (imgAvatar != null) {
            tabla.add(imgAvatar).size(80, 80).padBottom(10).row();
        }

        Label lblPartidas = new Label(Textos.PARTIDAS() + stats.getPartidasJugadas(), skin);
        Label lblNiveles  = new Label(Textos.NIVELES_COMP() + stats.getNivelesCompletados(), skin);
        Label lblRanking  = new Label(Textos.RANKING() + usuario.getRanking(), skin);

        Label separador = new Label("─────────────────", skin);
        separador.setColor(Color.LIGHT_GRAY);

        TextButton btnJugar   = new TextButton(Textos.JUGAR(), skin);
        TextButton btnEstadisticas  = new TextButton(Textos.ESTADISTICAS(), skin);
        TextButton btnRanking = new TextButton(Textos.RANKING_BTN(), skin);
        TextButton btnSubirAvatar = new TextButton(Textos.PERFIL_AVATAR(), skin);
        TextButton btnVerPerfil  = new TextButton(Textos.VER_PERFIL(), skin);
        TextButton btnAmigos = new TextButton(Textos.VER_AMIGOS(), skin);
        TextButton btnInbox = new TextButton(Textos.INBOX(), skin);
        TextButton btnIdioma  = new TextButton(Textos.IDIOMA(), skin);
        TextButton btnCerrarSesion = new TextButton(Textos.CERRAR_SESION(), skin);

        float ancho = 300f;

        tabla.add(titulo).padBottom(5).row();
        tabla.add(bienvenida).padBottom(2).row();
        tabla.add(infoNombre).padBottom(20).row();
        tabla.add(separador).padBottom(10).row();
        tabla.add(lblPartidas).left().padBottom(4).row();
        tabla.add(lblNiveles).left().padBottom(4).row();
        tabla.add(lblRanking).left().padBottom(20).row();
        tabla.add(separador).padBottom(15).row();
        tabla.add(btnJugar).width(ancho).padBottom(8).row();
        tabla.add(btnEstadisticas).width(ancho).padBottom(8).row();
        tabla.add(btnRanking).width(ancho).padBottom(8).row();
        tabla.add(btnSubirAvatar).width(ancho).padBottom(8).row();
        tabla.add(btnVerPerfil).width(ancho).padBottom(8).row();
        tabla.add(btnAmigos).width(ancho).padBottom(8).row();
        tabla.add(btnInbox).width(ancho).padBottom(8).row();
        tabla.add(btnIdioma).width(ancho).padBottom(8).row();
        tabla.add(btnCerrarSesion).width(ancho).row();

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

        btnRanking.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                juego.setScreen(new RankingScreen(juego));
            }
        });

        btnSubirAvatar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                juego.setScreen(new AvatarScreen(juego));
            }
        });

        btnVerPerfil.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                juego.setScreen(new BuscarJugadorScreen(juego));
            }
        });

        btnAmigos.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                juego.setScreen(new AmigosScreen(juego));
            }
        });

        btnInbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                juego.setScreen(new InboxScreen(juego));
            }
        });

        btnIdioma.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                Textos.toggle();
                btnIdioma.setText(Textos.IDIOMA());
            }
        });

        btnCerrarSesion.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                juego.setUsuarioActual(null);
                juego.setScreen(new LoginScreen(juego));
                show();
            }
        });
    }

    //Carga la imagen del avatar personalizado si existe 
    private Image cargarAvatar(Usuarios usuario) {
        //  Intentar con la ruta almacenada en el usuario
        String ruta = usuario.getAvatar();
        if (ruta != null && !ruta.isEmpty() && !ruta.equals("avatar_default.png")) {
            try {
                File f = new File(ruta);
                if (f.exists()) {
                    Texture tex = new Texture(Gdx.files.absolute(ruta));
                    return new Image(new TextureRegionDrawable(new TextureRegion(tex)));
                }
            } catch (Exception e) {
                // ignorar
            }
        }
        // Buscar por nombre de usuario en la carpeta usuarios
        try {
            ruta = GestorArchivos.getRutaAvatar(usuario.getUsername());
            if (ruta != null) {
                Texture tex = new Texture(Gdx.files.absolute(ruta));
                return new Image(new TextureRegionDrawable(new TextureRegion(tex)));
            }
        } catch (Exception e) {
            // ignorar
        }
        return null; // sin avatar personalizado
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
