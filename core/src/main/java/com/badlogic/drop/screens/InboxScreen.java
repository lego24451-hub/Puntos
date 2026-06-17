package com.badlogic.drop.screens;

import com.badlogic.drop.archivos.GestorSocial;
import com.badlogic.drop.config.Reto;
import com.badlogic.drop.config.SolicitudAmistad;
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

import java.util.List;


 // Pantalla de Inbox: muestra al usuario actual todas sus
 // solicitudes de amistad y retos pendientes/finalizados.
 
public class InboxScreen implements Screen {

    private final Main juego;
    private Stage stage;
    private Skin  skin;

    public InboxScreen(Main juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin  = new Skin(Gdx.files.internal("uiskin.json"));

        // Scroll externo para todo el contenido
        Table contenido = new Table();
        contenido.top().padTop(10).padLeft(15).padRight(15);

        ScrollPane scroll = new ScrollPane(contenido, skin);
        scroll.setFillParent(true);
        scroll.setScrollingDisabled(true, false);
        stage.addActor(scroll);

        String username = juego.getUsuarioActual().getUsername();

        // ── Título principal ─────────────────────────────────
        Label.LabelStyle estiloTitulo = new Label.LabelStyle();
        estiloTitulo.font = skin.getFont("default-font");
        estiloTitulo.fontColor = new Color(0.32f, 0.29f, 0.72f, 1f);

        Label titulo = new Label("📬  Inbox", estiloTitulo);
        titulo.setFontScale(1.8f);
        contenido.add(titulo).padBottom(20).row();

       
        //  SECCIÓN 1: Solicitudes de amistad
        
        List<SolicitudAmistad> solicitudes = GestorSocial.cargarSolicitudes(username);
        boolean haySolicitudes = false;
        for (SolicitudAmistad s : solicitudes) {
            if (s.isPendiente()) { haySolicitudes = true; break; }
        }

        Label lblSecAmigos = new Label("── Solicitudes de Amistad ──", skin);
        lblSecAmigos.setColor(new Color(0.32f, 0.29f, 0.72f, 1f));
        contenido.add(lblSecAmigos).left().padBottom(8).row();

        if (!haySolicitudes) {
            contenido.add(new Label("No tienes solicitudes pendientes.", skin))
                     .left().padBottom(15).row();
        } else {
            for (final SolicitudAmistad solicitud : solicitudes) {
                if (!solicitud.isPendiente()) continue;

                Table fila = new Table();
                fila.left();

                Label lbl = new Label(solicitud.getDeMiParte() + " quiere ser tu amigo", skin);
                TextButton btnAceptar  = new TextButton("Aceptar",  skin);
                TextButton btnRechazar = new TextButton("Rechazar", skin);

                btnAceptar.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        GestorSocial.aceptarSolicitud(solicitud);
                        juego.setScreen(new InboxScreen(juego)); // refrescar
                    }
                });

                btnRechazar.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        GestorSocial.rechazarSolicitud(solicitud);
                        juego.setScreen(new InboxScreen(juego)); // refrescar
                    }
                });

                fila.add(lbl).expandX().left().padRight(10);
                fila.add(btnAceptar).padRight(5);
                fila.add(btnRechazar);

                contenido.add(fila).fillX().padBottom(6).row();
            }
        }

        
        //  SECCIÓN 2: Retos recibidos (pendientes)
        
        List<Reto> retos = GestorSocial.cargarRetos(username);

        Label lblSecRetosRec = new Label("── Retos Recibidos ──", skin);
        lblSecRetosRec.setColor(new Color(0.32f, 0.29f, 0.72f, 1f));
        contenido.add(lblSecRetosRec).left().padTop(15).padBottom(8).row();

        boolean hayRetosRecibidos = false;
        for (final Reto reto : retos) {
            // Sólo los retos donde yo soy el RETADO y están PENDIENTES
            if (!reto.getUsernameRetado().equals(username)) continue;
            if (!reto.isPendiente()) continue;

            hayRetosRecibidos = true;

            Table fila = new Table();
            fila.left();

            Label lbl = new Label(
                reto.getUsernameRetador() + " te retó en Nivel "
                + reto.getNivelNumero() + " (" + obtenerDificultad(reto.getNivelNumero()) + ")",
                skin);

            TextButton btnAceptar  = new TextButton("Aceptar y Jugar", skin);
            TextButton btnRechazar = new TextButton("Rechazar",         skin);

            btnAceptar.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    // Marcar como aceptado y lanzar el juego en modo reto (retado)
                    reto.aceptar();
                    GestorSocial.actualizarReto(reto);
                    // Lanzar FirstScreen en modo retado (sin usernameRetado = es el retado jugando)
                    juego.setScreen(new FirstScreen(juego, reto.getNivelNumero(), reto));
                }
            });

            btnRechazar.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    reto.rechazar();
                    GestorSocial.actualizarReto(reto);
                    juego.setScreen(new InboxScreen(juego)); // refrescar
                }
            });

            fila.add(lbl).expandX().left().padRight(10);
            fila.add(btnAceptar).padRight(5);
            fila.add(btnRechazar);

            contenido.add(fila).fillX().padBottom(6).row();
        }

        if (!hayRetosRecibidos) {
            contenido.add(new Label("No tienes retos pendientes.", skin))
                     .left().padBottom(15).row();
        }

        
        //  SECCIÓN 3: Retos enviados (resultados)
        
        Label lblSecRetosEnv = new Label("── Retos Enviados / Resultados ──", skin);
        lblSecRetosEnv.setColor(new Color(0.32f, 0.29f, 0.72f, 1f));
        contenido.add(lblSecRetosEnv).left().padTop(15).padBottom(8).row();

        boolean hayResultados = false;
        for (Reto reto : retos) {
            // Sólo retos donde yo soy el RETADOR
            if (!reto.getUsernameRetador().equals(username)) continue;

            hayResultados = true;
            String texto;
            Color  color;

            switch (reto.getEstado()) {
                case PENDIENTE:
                    texto = "➤ " + reto.getUsernameRetado()
                          + " | Nivel " + reto.getNivelNumero()
                          + " — Esperando respuesta...";
                    color = Color.YELLOW;
                    break;
                case ACEPTADO:
                    texto = "➤ " + reto.getUsernameRetado()
                          + " | Nivel " + reto.getNivelNumero()
                          + " — Aceptó, jugando...";
                    color = Color.CYAN;
                    break;
                case RECHAZADO:
                    texto = "✗ " + reto.getUsernameRetado()
                          + " | Nivel " + reto.getNivelNumero()
                          + " — Rechazó el reto.";
                    color = Color.RED;
                    break;
                case FINALIZADO:
                    String ganador = reto.getGanador();
                    if (ganador == null) {
                        texto = "= " + reto.getUsernameRetado()
                              + " | Nivel " + reto.getNivelNumero()
                              + " — Empate (" + reto.getPuntajeRetador()
                              + " vs " + reto.getPuntajeRetado() + " pts)";
                        color = Color.YELLOW;
                    } else if (ganador.equals(username)) {
                        texto = "✔ " + reto.getUsernameRetado()
                              + " | Nivel " + reto.getNivelNumero()
                              + " — ¡Ganaste! (" + reto.getPuntajeRetador()
                              + " vs " + reto.getPuntajeRetado() + " pts)";
                        color = Color.GREEN;
                    } else {
                        texto = "✗ " + reto.getUsernameRetado()
                              + " | Nivel " + reto.getNivelNumero()
                              + " — Perdiste (" + reto.getPuntajeRetador()
                              + " vs " + reto.getPuntajeRetado() + " pts)";
                        color = Color.RED;
                    }
                    break;
                default:
                    texto = reto.toString();
                    color = Color.WHITE;
            }

            Label lblReto = new Label(texto, skin);
            lblReto.setColor(color);
            contenido.add(lblReto).left().padBottom(5).row();
        }

        if (!hayResultados) {
            contenido.add(new Label("No has retado a nadie aún.", skin))
                     .left().padBottom(15).row();
        }

        
        //  Botón volver
       
        TextButton btnVolver = new TextButton("Volver al Menú", skin);
        btnVolver.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                juego.setScreen(new MenuScreen(juego));
            }
        });
        contenido.add(btnVolver).width(200).padTop(25).row();
    }

  
    private String obtenerDificultad(int nivel) {
        try {
            return com.badlogic.drop.config.Nivel.getNivel(nivel).getDificultad();
        } catch (Exception e) {
            return "?";
        }
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