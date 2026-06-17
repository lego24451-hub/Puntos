package com.badlogic.drop.screens;

import com.badlogic.drop.archivos.GestorArchivos;
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
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;

public class AvatarScreen implements Screen {
    private final Main juego;
    private Stage stage;
    private Skin skin;
    private Label lblEstado;

    // Avatar upload
    private Image imgAvatar;

    // Username change
    private TextField campoNuevoUsuario;
    private TextField campoContrasena;

    public AvatarScreen(Main juego) {
        this.juego = juego;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Usuarios usuario = juego.getUsuarioActual();

        Table tabla = new Table();
        tabla.setFillParent(true);
        tabla.center();
        stage.addActor(tabla);

        Label.LabelStyle estiloTitulo = new Label.LabelStyle();
        estiloTitulo.font = skin.getFont("default-font");
        estiloTitulo.fontColor = new Color(0.32f, 0.29f, 0.72f, 1f);

        Label titulo = new Label(Textos.PERFIL_AVATAR(), estiloTitulo);
        titulo.setFontScale(1.6f);
        tabla.add(titulo).padBottom(25).row();

        // Sección: Avatar
        Label lblSecAvatar = new Label(Textos.SUBIR_AVATAR(), skin);
        lblSecAvatar.setColor(new Color(0.32f, 0.29f, 0.72f, 1f));
        tabla.add(lblSecAvatar).left().padBottom(8).row();

        imgAvatar = cargarAvatar(usuario);
        if (imgAvatar == null) {
            imgAvatar = new Image();
        }
        tabla.add(imgAvatar).size(100, 100).padBottom(8).row();

        TextButton btnExaminar = new TextButton(t("Elegir imagen (PNG, JPG)", "Choose image (PNG, JPG)"), skin);
        tabla.add(btnExaminar).width(280).padBottom(20).row();

        btnExaminar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                abrirSelectorArchivos(usuario);
            }
        });

        tabla.add(new Label("─────────────────", skin)).padBottom(15).row();

        //  Sección: Cambiar nombre de usuario 
        Label lblSecUser = new Label(Textos.NUEVO_USER(), skin);
        lblSecUser.setColor(new Color(0.32f, 0.29f, 0.72f, 1f));
        tabla.add(lblSecUser).left().padBottom(8).row();

        campoNuevoUsuario = new TextField(usuario.getUsername(), skin);
        campoNuevoUsuario.setMessageText(Textos.NUEVO_USER());
        tabla.add(campoNuevoUsuario).width(300).padBottom(8).row();

        campoContrasena = new TextField("", skin);
        campoContrasena.setMessageText(Textos.CONFIRMA_PASS());
        campoContrasena.setPasswordMode(true);
        campoContrasena.setPasswordCharacter('*');
        tabla.add(campoContrasena).width(300).padBottom(10).row();

        //  Estado y botones 
        lblEstado = new Label("", skin);
        tabla.add(lblEstado).padBottom(10).row();

        TextButton btnGuardar = new TextButton(Textos.GUARDAR(), skin);
        tabla.add(btnGuardar).width(250).padBottom(8).row();

        TextButton btnVolver = new TextButton(Textos.VOLVER_MENU(), skin);
        tabla.add(btnVolver).width(250).row();

        btnGuardar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                guardarCambios(usuario);
            }
        });

        btnVolver.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                juego.setScreen(new MenuScreen(juego));
            }
        });

        // ─── Eliminar cuenta 
        tabla.add(new Label("─────────────────", skin)).padBottom(10).row();

        TextButton btnEliminarCuenta = new TextButton(Textos.ELIMINAR_CUENTA(), skin);
        btnEliminarCuenta.setColor(Color.RED);
        tabla.add(btnEliminarCuenta).width(250).padBottom(8).row();

        btnEliminarCuenta.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                mostrarDialogoEliminar(usuario);
            }
        });
    }

    private void mostrarDialogoEliminar(final Usuarios usuario) {
        final Dialog dialogo = new Dialog("", skin) {
            @Override
            protected void result(Object object) {
                // Se cierra con hide()
            }
        };
        dialogo.setModal(true);
        dialogo.setMovable(false);
        dialogo.pad(20);

        Label lblAdvertencia = new Label(Textos.CONFIRMAR_ELIMINAR(), skin);
        lblAdvertencia.setColor(Color.RED);
        lblAdvertencia.setWrap(true);
        dialogo.getContentTable().add(lblAdvertencia).width(350).padBottom(15).row();

        final TextField passField = new TextField("", skin);
        passField.setPasswordMode(true);
        passField.setPasswordCharacter('*');
        passField.setMessageText(t("Contraseña", "Password"));
        dialogo.getContentTable().add(passField).width(280).padBottom(15).row();

        final Label lblError = new Label("", skin);
        lblError.setColor(Color.RED);
        dialogo.getContentTable().add(lblError).padBottom(10).row();

        TextButton btnConfirmar = new TextButton(Textos.SI_ELIMINAR(), skin);
        TextButton btnCancelar  = new TextButton(Textos.CANCELAR(), skin);

        Table btnTable = new Table();
        btnTable.add(btnConfirmar).width(200).padRight(10);
        btnTable.add(btnCancelar).width(150);
        dialogo.getContentTable().add(btnTable).row();

        btnConfirmar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                String pass = passField.getText();
                if (!usuario.getContrasena().equals(GestorArchivos.hashContrasena(pass))) {
                    lblError.setText(Textos.PASS_INCORRECTA());
                    return;
                }
                // Eliminar carpeta del usuario
                try {
                    File userDir = new File("usuarios/" + usuario.getUsername());
                    if (userDir.exists()) {
                        for (File f : userDir.listFiles()) f.delete();
                        userDir.delete();
                    }
                } catch (Exception e) {
                    lblError.setText(Textos.ERROR_ELIMINAR());
                    return;
                }
                juego.setUsuarioActual(null);
                dialogo.hide();
                juego.setScreen(new LoginScreen(juego));
            }
        });

        btnCancelar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                dialogo.hide();
            }
        });

        dialogo.show(stage);
    }

    //  Cambio de nombre de usuario 

    private void guardarCambios(Usuarios usuario) {
        String pass = campoContrasena.getText();
        String hashActual = usuario.getContrasena();

        if (!hashActual.equals(GestorArchivos.hashContrasena(pass))) {
            lblEstado.setColor(Color.RED);
            lblEstado.setText(Textos.PASS_INCORRECTA());
            return;
        }

        String nuevoUser = campoNuevoUsuario.getText().trim();
        if (nuevoUser.isEmpty()) {
            lblEstado.setColor(Color.RED);
            lblEstado.setText(Textos.ERROR_CAMPOS());
            return;
        }

        if (!nuevoUser.equals(usuario.getUsername())) {
            if (GestorArchivos.usuarioExiste(nuevoUser)) {
                lblEstado.setColor(Color.RED);
                lblEstado.setText(Textos.ERROR_USER_EXISTS());
                return;
            }
            String oldUsername = usuario.getUsername();
            usuario.setUsername(nuevoUser);
            // Limpiar carpeta del usuario anterior
            File oldDir = new File("usuarios/" + oldUsername);
            if (oldDir.exists()) {
                for (File f : oldDir.listFiles()) f.delete();
                oldDir.delete();
            }
        }

        GestorArchivos.guardarUsuario(usuario);
        juego.setUsuarioActual(usuario);
        lblEstado.setColor(Color.GREEN);
        lblEstado.setText(Textos.CAMBIOS_GUARDADOS());
    }

    //  Subida de avatar 

    private void abrirSelectorArchivos(Usuarios usuario) {
        new Thread(() -> {
            try {
                FileDialog dialogo = new FileDialog((Frame) null, t("Seleccionar imagen", "Select image"));
                dialogo.setMode(FileDialog.LOAD);
                dialogo.setFilenameFilter((dir, nombre) -> {
                    String n = nombre.toLowerCase();
                    return n.endsWith(".png") || n.endsWith(".jpg") || n.endsWith(".jpeg");
                });
                dialogo.setVisible(true);

                String directorio = dialogo.getDirectory();
                String archivo = dialogo.getFile();

                if (directorio != null && archivo != null) {
                    String rutaCompleta = directorio + archivo;
                    String rutaGuardada = GestorArchivos.guardarAvatarPersonalizado(
                        usuario.getUsername(), rutaCompleta);

                    Gdx.app.postRunnable(() -> {
                        if (rutaGuardada != null) {
                            usuario.setAvatar(rutaGuardada);
                            GestorArchivos.guardarUsuario(usuario);
                            juego.setScreen(new AvatarScreen(juego)); // recargar
                        } else {
                            lblEstado.setColor(Color.RED);
                            lblEstado.setText(t("No se pudo guardar la imagen",
                                                  "Could not save the image"));
                        }
                    });
                } else {
                    Gdx.app.postRunnable(() -> {
                        lblEstado.setColor(Color.GRAY);
                        lblEstado.setText(t("Selección cancelada", "Selection cancelled"));
                    });
                }
            } catch (Exception e) {
                Gdx.app.postRunnable(() -> {
                    lblEstado.setColor(Color.RED);
                    lblEstado.setText(t("ERROR AL ABRIR EL SELECTOR",
                                          "ERROR OPENING FILE SELECTOR"));
                });
            }
        }).start();
    }

    //  Carga de avatar 

    private Image cargarAvatar(Usuarios usuario) {
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
        try {
            ruta = GestorArchivos.getRutaAvatar(usuario.getUsername());
            if (ruta != null) {
                Texture tex = new Texture(Gdx.files.absolute(ruta));
                return new Image(new TextureRegionDrawable(new TextureRegion(tex)));
            }
        } catch (Exception e) {
            // ignorar
        }
        return null;
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

    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { dispose(); }
    @Override public void dispose() {
        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();
    }
}
