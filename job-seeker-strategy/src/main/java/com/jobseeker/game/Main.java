package com.jobseeker.game;

import com.jme3.app.SimpleApplication;

public class Main extends SimpleApplication {
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        // 禁用flycam
        flyCam.setEnabled(false);
        
        Game game = new Game(this);
        game.init();
    }
}