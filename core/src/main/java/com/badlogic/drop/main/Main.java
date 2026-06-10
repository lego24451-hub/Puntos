package com.badlogic.drop.main;

import com.badlogic.drop.config.Usuarios;
import com.badlogic.drop.juego.FlowFreeJuego;
import com.badlogic.drop.screens.LoginScreen;
import com.badlogic.gdx.Game;

public class Main extends Game {

    private Usuarios usuarioActual;
    private FlowFreeJuego flowJuego;

    @Override
    public void create() {
        // Inicia con la pantalla de login
        setScreen(new LoginScreen(this));
    }

    // --- Usuario actual ---
    public Usuarios getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(Usuarios usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    // --- FlowFreeJuego (lógica del juego) ---
    public FlowFreeJuego getFlowFreeJuego() {
        return flowJuego;
    }

    public void setFlowFreeJuego(FlowFreeJuego flowJuego) {
        this.flowJuego = flowJuego;
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
    }
}
