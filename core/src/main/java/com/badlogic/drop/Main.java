package com.badlogic.drop;

import com.badlogic.gdx.Game;

public class Main extends Game {
    @Override
    public void create() {
        setScreen(new FirstScreen());
    }

    @Override
    public void resize(int width, int height) {
       
        if(width <= 0 || height <= 0) return;

    }


}