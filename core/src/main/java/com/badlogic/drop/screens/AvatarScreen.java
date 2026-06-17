/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.badlogic.drop.screens;

import com.badlogic.drop.archivos.GestorArchivos;
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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;

/**
 *
 * @author user
 */
public class AvatarScreen implements Screen {
    private final Main juego;
    private Stage stage;
    private Skin skin;
    private Label lblEstado;
    
    public AvatarScreen (Main juego){
        this.juego = juego;
         }
        
        public void show(){
            stage = new Stage (new ScreenViewport());
            Gdx.input.setInputProcessor(stage);
            skin = new Skin (Gdx.files.internal("uiskin.json"));
            
            Usuarios usuario = juego.getUsuarioActual();
            
          Table tabla = new Table();
          tabla.setFillParent(true);
          tabla.center();
          stage.addActor(tabla);
          
          Label.LabelStyle estiloTitulo = new Label.LabelStyle();
          estiloTitulo.font = skin.getFont("default-font");
          estiloTitulo.fontColor = new Color (0.32f, 0.29f, 0.72f, 1f);
          Label titulo = new Label ("Avatar de perfil", estiloTitulo);
          titulo.setFontScale(1.6f);
          tabla.add(titulo).padBottom(20).row();
          
          Label lblActual = new Label("Avatar actual:", skin);
          lblActual.setColor(Color.GRAY);
          tabla.add(lblActual).padBottom(8).row();
          
         Image imgAvatar = cargarAvatar (usuario.getAvatar());
         tabla.add(imgAvatar).size(120,120).padBottom(20).row();
         
         TextButton btnExaminar = new TextButton ("Elegir imagen desde mi dispositivo", skin);
         tabla.add(btnExaminar).width(300).padBottom(10).row();
         
         
         Label lblInfo = new Label ("Formatos aceptados: PNG, JPG", skin);
         lblInfo.setColor(Color.GRAY);
         tabla.add(lblInfo).padBottom(15).row();
         
         lblEstado = new Label("", skin);
         tabla.add(lblEstado).padBottom(25).row();
         
         btnExaminar.addListener(new ChangeListener(){
             @Override
             public void changed (ChangeEvent event, Actor actor){
                 abrirSelectorArchivos(usuario);
             }
         });
          
         TextButton btnVolver = new TextButton ("Volver al menú", skin);
         tabla.add(btnVolver).width(200).row();
         btnVolver.addListener(new ChangeListener(){
             public void changed (ChangeEvent event, Actor actor){
                 juego.setScreen(new MenuScreen(juego));
             }
         });
         
        }
        
        
       private void abrirSelectorArchivos(Usuarios usuario){
           new Thread(() -> {
              try{
                  FileDialog dialogo = new FileDialog((Frame) null, "Seleccionar imagen de avatar");
                  dialogo.setMode(FileDialog.LOAD);
                  dialogo.setFilenameFilter((dir, nombre) ->{
                      String n = nombre.toLowerCase();
                      return n.endsWith(".png") || n.endsWith(".jpg") || n.endsWith(".jpeg");
                  });
                  dialogo.setVisible(true);
                  
                  String directorio = dialogo.getDirectory();
                  String archivo = dialogo.getFile();
                  
                  if (directorio != null && archivo != null){
                      String rutaCompleta = directorio + archivo;
                      String rutaGuardada = GestorArchivos.guardarAvatarPersonalizado(usuario.getUsername(), rutaCompleta);
                      
                      Gdx.app.postRunnable(()-> {
                        if (rutaGuardada !=null){
                            usuario.setAvatar(rutaGuardada);
                            GestorArchivos.guardarUsuario(usuario);
                            lblEstado.setColor(Color.GREEN);
                            lblEstado.setText("Avatar actualizado correctamente");
                            juego.setScreen(new AvatarScreen(juego));
                        }
                        else{
                            lblEstado.setColor(Color.RED);
                            lblEstado.setText("No se pudo guardar la imagen actual");
                        }
                      });
                      
                  } else{
                      Gdx.app.postRunnable(() ->{
                          lblEstado.setColor(Color.GRAY);
                          lblEstado.setText("Selección cancelada");
                      });
                  }
                  
              }catch (Exception e){
                  Gdx.app.postRunnable(() ->{
                      lblEstado.setColor(Color.RED);
                      lblEstado.setText("ERROR AL ABRIR EL ESELECTOR DE ARCHIVOS");
                  });
              } 
           }).start();
     
       
       
       }
           private Image cargarAvatar(String ruta){
           try{
               if (ruta != null && !ruta.equals("avatar_default.png") && !ruta.isEmpty()){
                    File f = new File (ruta);
                    if (f.exists()){
                        Texture tex = new Texture (Gdx.files.absolute(ruta));
                        return new Image (new TextureRegionDrawable(new TextureRegion(tex)));
                    }
               }
           } catch (Exception e){
             System.out.println("Error cargando avatar: " + e.getMessage());
}
           return new Image();
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
    @Override public void dispose() {
        if (stage != null) stage.dispose();
        if (skin  != null) skin.dispose();
    }
    
}
