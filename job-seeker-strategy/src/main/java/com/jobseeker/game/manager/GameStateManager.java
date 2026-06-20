package com.jobseeker.game.manager;

import com.jobseeker.game.config.GameConfig.Era;

public class GameStateManager {
    private static GameStateManager instance;
    private Era currentEra = Era.PRIMITIVE_LABOR;
    private float techLevel = 0.05f;
    private float gdp = 1000000f;
    private float happinessIndex = 50f;
    
    public static void init() {
        instance = new GameStateManager();
    }
    
    public static GameStateManager getInstance() { return instance; }
    
    public void update() {}
    
    public Era getCurrentEra() { return currentEra; }
    public float getTechLevel() { return techLevel; }
    public float getGdp() { return gdp; }
    public float getHappinessIndex() { return happinessIndex; }
    public void addGdp(float amount) { gdp += amount; }
}