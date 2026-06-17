package com.badlogic.drop.screens;

import com.badlogic.drop.main.Main;
import com.badlogic.drop.config.Usuarios;
import com.badlogic.drop.config.Textos;
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

        Label.LabelStyle estiloTitulo = new Label.LabelStyle();
        estiloTitulo.font = skin.getFont("default-font");
        estiloTitulo.fontColor = new Color(0.32f, 0.29f, 0.72f, 1f);

        Label titulo = new Label(Textos.TITULO_JUEGO(), estiloTitulo);
        titulo.setFontScale(1.8f);
        Label subtitulo = new Label(Textos.CREAR_CUENTA(), skin);

        campoNombre = new TextField("", skin);
        campoNombre.setMessageText(Textos.NOMBRE_COMPLETO());

        campoUsuario = new TextField("", skin);
        campoUsuario.setMessageText(Textos.USUARIO());

        campoContrasena = new TextField("", skin);
        campoContrasena.setMessageText(Textos.CONTRASENA_MIN());
        campoContrasena.setPasswordMode(true);
        campoContrasena.setPasswordCharacter('*');

        campoConfirmar = new TextField("", skin);
        campoConfirmar.setMessageText(Textos.CONFIRMAR());
        campoConfirmar.setPasswordMode(true);
        campoConfirmar.setPasswordCharacter('*');

        final CheckBox chkMostrarPass = new CheckBox(Textos.MOSTRAR_PASSES(), skin);

        TextButton btnRegistrar = new TextButton(Textos.CREAR_CUENTA(), skin);
        TextButton btnVolver = new TextButton(Textos.YA_TENGO_CUENTA(), skin);

        labelError = new Label("", skin);
        labelError.setColor(Color.RED);

        tabla.add(titulo).colspan(2).padBottom(5).row();
        tabla.add(subtitulo).colspan(2).padBottom(30).row();
        tabla.add(new Label(Textos.NOMBRE_COMPLETO() + ":", skin)).left().padBottom(5);
        tabla.add(campoNombre).width(300).padBottom(5).row();
        tabla.add(new Label(Textos.USUARIO() + ":", skin)).left().padBottom(5);
        tabla.add(campoUsuario).width(300).padBottom(5).row();
        tabla.add(new Label(Textos.CONTRASENA() + ":", skin)).left().padBottom(5);
        tabla.add(campoContrasena).width(300).padBottom(5).row();
        tabla.add(new Label(Textos.CONFIRMAR() + ":", skin)).left().padBottom(5);
        tabla.add(campoConfirmar).width(300).padBottom(5).row();
        tabla.add(new Label("", skin)).left();
        tabla.add(chkMostrarPass).left().padBottom(20).row();
        tabla.add(labelError).colspan(2).padBottom(10).row();
        tabla.add(btnRegistrar).colspan(2).width(300).padBottom(10).row();
        tabla.add(btnVolver).colspan(2).width(300).row();

        chkMostrarPass.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                boolean mostrar = chkMostrarPass.isChecked();
                campoContrasena.setPasswordMode(!mostrar);
                campoConfirmar.setPasswordMode(!mostrar);
            }
        });

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
            labelError.setText(Textos.ERROR_CAMPOS());
            return;
        }
        if (contrasena.length() < 6) {
            labelError.setText(Textos.ERROR_PASS_LEN());
            return;
        }
        if (!contrasena.equals(confirmar)) {
            labelError.setText(Textos.ERROR_PASS_MATCH());
            return;
        }
        if (GestorArchivos.usuarioExiste(usuario)) {
            labelError.setText(Textos.ERROR_USER_EXISTS());
            return;
        }

        String hashContrasena = GestorArchivos.hashContrasena(contrasena);
        Usuarios nuevoUsuario = new Usuarios(usuario, hashContrasena, nombre);
        GestorArchivos.guardarUsuario(nuevoUsuario);

        juego.setUsuarioActual(nuevoUsuario);
        juego.setScreen(new MenuScreen(juego));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) { 
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
