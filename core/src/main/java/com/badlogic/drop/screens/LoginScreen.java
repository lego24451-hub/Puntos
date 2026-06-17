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

public class LoginScreen implements Screen {

    private final Main juego;
    private Stage stage;
    private Skin skin;

    private TextField campoUsuario;
    private TextField campoContrasena;
    private Label labelError;

    public LoginScreen(Main juego) {
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
        titulo.setFontScale(2f);

        campoUsuario = new TextField("", skin);
        campoUsuario.setMessageText(Textos.USUARIO());

        campoContrasena = new TextField("", skin);
        campoContrasena.setMessageText(Textos.CONTRASENA());
        campoContrasena.setPasswordMode(true);
        campoContrasena.setPasswordCharacter('*');

        final CheckBox chkMostrarPass = new CheckBox(Textos.MOSTRAR_PASS(), skin);

        TextButton btnLogin = new TextButton(Textos.INICIAR_SESION(), skin);
        TextButton btnRegistro = new TextButton(Textos.NO_TIENES_CUENTA(), skin);

        labelError = new Label("", skin);
        labelError.setColor(Color.RED);

        tabla.add(titulo).colspan(2).padBottom(40).row();
        tabla.add(new Label(Textos.USUARIO() + ":", skin)).left().padBottom(5);
        tabla.add(campoUsuario).width(300).padBottom(5).row();
        tabla.add(new Label(Textos.CONTRASENA() + ":", skin)).left().padBottom(5);
        tabla.add(campoContrasena).width(300).padBottom(5).row();
        tabla.add(new Label("", skin)).left();
        tabla.add(chkMostrarPass).left().padBottom(20).row();
        tabla.add(labelError).colspan(2).padBottom(10).row();
        tabla.add(btnLogin).colspan(2).width(300).padBottom(10).row();
        tabla.add(btnRegistro).colspan(2).width(300).row();

        chkMostrarPass.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                boolean mostrar = chkMostrarPass.isChecked();
                campoContrasena.setPasswordMode(!mostrar);
            }
        });

        btnLogin.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                intentarLogin();
            }
        });

        btnRegistro.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                juego.setScreen(new RegistroScreen(juego));
            }
        });
    }

    private void intentarLogin() {
        String usuario = campoUsuario.getText().trim();
        String contrasena = campoContrasena.getText();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            labelError.setText(Textos.ERROR_CAMPOS());
            return;
        }

        if (!GestorArchivos.usuarioExiste(usuario)) {
            labelError.setText(Textos.ERROR_NO_ENCONTRADO());
            return;
        }

        Usuarios u = GestorArchivos.cargarUsuario(usuario);
        if (u == null || !u.getContrasena().equals(GestorArchivos.hashContrasena(contrasena))) {
            labelError.setText(Textos.ERROR_PASS_INC());
            return;
        }

        juego.setUsuarioActual(u);
        u.setUltimaSesion(java.time.LocalDateTime.now());
        GestorArchivos.guardarUsuario(u);
        juego.setScreen(new MenuScreen(juego));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h){ stage.getViewport().update(w, h, true); 
    }
    @Override public void pause(){
    }
    @Override public void resume(){
    }
    @Override public void hide(){ 
        dispose(); 
    }
    @Override public void dispose(){ stage.dispose(); skin.dispose(); 
    }
}
