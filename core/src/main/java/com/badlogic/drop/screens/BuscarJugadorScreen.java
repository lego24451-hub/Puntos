package com.badlogic.drop.screens;

import com.badlogic.drop.archivos.GestorArchivos;
import com.badlogic.drop.archivos.GestorSocial;
import com.badlogic.drop.config.Estadisticas;
import com.badlogic.drop.config.Textos;
import com.badlogic.drop.config.Usuarios;
import com.badlogic.drop.main.Main;

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

public class BuscarJugadorScreen implements Screen {

    private final Main juego;
    private Stage stage;
    private Skin  skin;

    private TextField campoBusqueda;
    private Label     labelResultado;
    private Table     tablaResultado;

    // Guardamos el usuario encontrado para usarlo en los botones de acción
    private Usuarios perfilEncontrado = null;

    public BuscarJugadorScreen(Main juego) {
        this.juego = juego;
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

        Label titulo = new Label(Textos.BUSCAR_JUGADOR(), estiloTitulo);
        titulo.setFontScale(1.6f);

        campoBusqueda = new TextField("", skin);
        campoBusqueda.setMessageText(Textos.INTRODUCE_USER());

        TextButton btnBuscar = new TextButton(Textos.BUSCAR_JUGADOR(), skin);
        TextButton btnVolver = new TextButton(Textos.VOLVER_MENU(), skin);

        labelResultado = new Label("", skin);
        labelResultado.setColor(Color.RED);

        tablaResultado = new Table();

        tabla.add(titulo).padBottom(30).row();
        tabla.add(campoBusqueda).width(300).padBottom(8).row();
        tabla.add(btnBuscar).width(200).padBottom(8).row();
        tabla.add(labelResultado).padBottom(15).row();
        tabla.add(tablaResultado).padBottom(20).row();
        tabla.add(btnVolver).width(200).row();

        btnBuscar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                buscarJugador();
            }
        });

        btnVolver.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                juego.setScreen(new MenuScreen(juego));
            }
        });
    }

    private void buscarJugador() {
        String username = campoBusqueda.getText().trim();
        tablaResultado.clear();
        perfilEncontrado = null;

        if (username.isEmpty()) {
            labelResultado.setText(Textos.INTRODUCE_USER());
            return;
        }

        // No permitir buscarse a uno mismo
        if (juego.getUsuarioActual() != null
                && username.equals(juego.getUsuarioActual().getUsername())) {
            labelResultado.setText("No puedes buscarte a ti mismo.");
            return;
        }

        if (!GestorArchivos.usuarioExiste(username)) {
            labelResultado.setText(Textos.NO_ENCONTRADO());
            return;
        }

        Usuarios perfil = GestorArchivos.cargarUsuario(username);
        if (perfil == null) {
            labelResultado.setText(Textos.NO_ENCONTRADO());
            return;
        }

        perfilEncontrado = perfil;
        labelResultado.setText("");
        Estadisticas stats = perfil.getEstadisticas();

        // ── Avatar 
        Image imgAvatar = cargarAvatar(perfil);
        if (imgAvatar != null) {
            tablaResultado.add(imgAvatar).size(80, 80).padBottom(10).row();
        }

        // ── Info del perfil 
        Label lblPerfil = new Label(Textos.PERFIL_DE() + perfil.getUsername(), skin);
        lblPerfil.setColor(new Color(0.32f, 0.29f, 0.72f, 1f));
        Label lblNombre   = new Label(perfil.getNombreCompleto(), skin);
        Label lblRanking  = new Label(Textos.RANKING() + perfil.getRanking(), skin);
        Label lblPartidas = new Label(Textos.PARTIDAS() + stats.getPartidasJugadas(), skin);
        Label lblCompletados = new Label(Textos.NIVELES_COMP() + stats.getNivelesCompletados(), skin);

        long tiempoTotal = stats.getTiempoTotalJugado();
        long min = tiempoTotal / 60, seg = tiempoTotal % 60;
        String tiempoStr = (min > 0 ? min + "m " : "") + seg + "s";
        Label lblTiempo = new Label(Textos.TIEMPO_TOTAL() + tiempoStr, skin);

        tablaResultado.add(lblPerfil).left().padBottom(5).row();
        tablaResultado.add(lblNombre).left().padBottom(3).row();
        tablaResultado.add(lblRanking).left().padBottom(10).row();
        tablaResultado.add(new Label("─── " + Textos.ESTADISTICAS() + " ───", skin)).left().padBottom(5).row();
        tablaResultado.add(lblPartidas).left().padBottom(3).row();
        tablaResultado.add(lblCompletados).left().padBottom(3).row();
        tablaResultado.add(lblTiempo).left().padBottom(15).row();

        // ── Botones de acción social
        agregarBotonesSocial(perfil);
    }

   
    private void agregarBotonesSocial(final Usuarios perfil) {
        Usuarios yo = juego.getUsuarioActual();
        if (yo == null) return;

        Table tablaBotones = new Table();

        // ── Solicitud de amistad 
        boolean yaEsAmigo = yo.getAmigos().contains(perfil.getUsername());

        if (yaEsAmigo) {
            Label lblAmigo = new Label("✔ Ya son amigos", skin);
            lblAmigo.setColor(Color.GREEN);
            tablaBotones.add(lblAmigo).padBottom(8).row();
        } else {
            TextButton btnSolicitud = new TextButton("Enviar solicitud de amistad", skin);
            btnSolicitud.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    boolean enviada = GestorSocial.enviarSolicitudAmistad(
                        yo.getUsername(), perfil.getUsername());
                    labelResultado.setColor(enviada ? Color.GREEN : Color.YELLOW);
                    labelResultado.setText(enviada
                        ? "✔ Solicitud enviada a " + perfil.getUsername()
                        : "Ya le enviaste una solicitud pendiente.");
                }
            });
            tablaBotones.add(btnSolicitud).width(280).padBottom(8).row();
        }

        // ── Reto 
        TextButton btnRetar = new TextButton("⚔  Retar a " + perfil.getUsername(), skin);
        btnRetar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Ir a la pantalla de selección de nivel para el reto
                juego.setScreen(new SeleccionNivelRetoScreen(juego, perfil.getUsername()));
            }
        });
        tablaBotones.add(btnRetar).width(280).padBottom(5).row();

        tablaResultado.add(tablaBotones).row();
    }

    private Image cargarAvatar(Usuarios usuario) {
        String ruta = usuario.getAvatar();
        if (ruta != null && !ruta.isEmpty() && !ruta.equals("avatar_default.png")) {
            try {
                File f = new File(ruta);
                if (f.exists()) {
                    Texture tex = new Texture(Gdx.files.absolute(ruta));
                    return new Image(new TextureRegionDrawable(new TextureRegion(tex)));
                }
            } catch (Exception e) { /* ignorar */ }
        }
        try {
            ruta = GestorArchivos.getRutaAvatar(usuario.getUsername());
            if (ruta != null) {
                Texture tex = new Texture(Gdx.files.absolute(ruta));
                return new Image(new TextureRegionDrawable(new TextureRegion(tex)));
            }
        } catch (Exception e) { /* ignorar */ }
        return null;
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