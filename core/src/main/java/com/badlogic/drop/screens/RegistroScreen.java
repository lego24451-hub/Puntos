package com.badlogic.drop.screens;

import com.badlogic.drop.main.Main;
import com.badlogic.drop.config.Usuarios;
import com.badlogic.drop.archivos.GestorArchivos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class RegistroScreen implements Screen {

    private final Main juego;
    private Stage stage;
    private Skin skin;

    private TextField campoNombre;
    private TextField campoUsuario;
    private TextField campoContrasena;
    private TextField campoConfirmar;
    private Label labelError;

    public RegistroScreen(Main juego) {
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

        // Título
        Label.LabelStyle estiloTitulo = new Label.LabelStyle();
        estiloTitulo.font = skin.getFont("default-font");
        estiloTitulo.fontColor = new Color(0.32f, 0.29f, 0.72f, 1f);

        Label titulo = new Label("Flow Free Game", estiloTitulo);
        titulo.setFontScale(1.8f);
        Label subtitulo = new Label("Crear cuenta", skin);

        // Campos
        campoNombre = new TextField("", skin);
        campoNombre.setMessageText("Nombre completo");

        campoUsuario = new TextField("", skin);
        campoUsuario.setMessageText("Nombre de usuario");

        campoContrasena = new TextField("", skin);
        campoContrasena.setMessageText("Contraseña (mín. 6 caracteres)");
        campoContrasena.setPasswordMode(true);
        campoContrasena.setPasswordCharacter('*');

        campoConfirmar = new TextField("", skin);
        campoConfirmar.setMessageText("Confirmar contraseña");
        campoConfirmar.setPasswordMode(true);
        campoConfirmar.setPasswordCharacter('*');

        // Botones
        TextButton btnRegistrar = new TextButton("Crear cuenta", skin);
        TextButton btnVolver = new TextButton("Ya tengo cuenta", skin);

        labelError = new Label("", skin);
        labelError.setColor(Color.RED);

        // Layout
        tabla.add(titulo).colspan(2).padBottom(5).row();
        tabla.add(subtitulo).colspan(2).padBottom(30).row();
        tabla.add(new Label("Nombre completo:", skin)).left().padBottom(5);
        tabla.add(campoNombre).width(300).padBottom(5).row();
        tabla.add(new Label("Usuario:", skin)).left().padBottom(5);
        tabla.add(campoUsuario).width(300).padBottom(5).row();
        tabla.add(new Label("Contraseña:", skin)).left().padBottom(5);
        tabla.add(campoContrasena).width(300).padBottom(5).row();
        tabla.add(new Label("Confirmar:", skin)).left().padBottom(5);
        tabla.add(campoConfirmar).width(300).padBottom(20).row();
        tabla.add(labelError).colspan(2).padBottom(10).row();
        tabla.add(btnRegistrar).colspan(2).width(300).padBottom(10).row();
        tabla.add(btnVolver).colspan(2).width(300).row();

        // Acciones
        btnRegistrar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                intentarRegistro();
            }
        });

        btnVolver.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                juego.setScreen(new LoginScreen(juego));
            }
        });
    }

    private void intentarRegistro() {
        String nombre = campoNombre.getText().trim();
        String usuario = campoUsuario.getText().trim();
        String contrasena = campoContrasena.getText();
        String confirmar = campoConfirmar.getText();

        if (nombre.isEmpty() || usuario.isEmpty() || contrasena.isEmpty() || confirmar.isEmpty()) {
            labelError.setText("Completa todos los campos.");
            return;
        }
        if (contrasena.length() < 6) {
            labelError.setText("La contraseña debe tener al menos 6 caracteres.");
            return;
        }
        if (!contrasena.equals(confirmar)) {
            labelError.setText("Las contraseñas no coinciden.");
            return;
        }
        if (GestorArchivos.usuarioExiste(usuario)) {
            labelError.setText("Ese nombre de usuario ya existe.");
            return;
        }

        // Crear y guardar usuario
        String hashContrasena = GestorArchivos.hashContrasena(contrasena);
        Usuarios nuevoUsuario = new Usuarios(usuario, hashContrasena, nombre);
        GestorArchivos.guardarUsuario(nuevoUsuario);

        juego.setUsuarioActual(nuevoUsuario);
        juego.setScreen(new MenuScreen(juego));
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
