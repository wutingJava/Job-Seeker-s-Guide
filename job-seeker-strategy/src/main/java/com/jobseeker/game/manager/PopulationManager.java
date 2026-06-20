package com.jobseeker.game.manager;

public class PopulationManager {
    private static PopulationManager instance;
    public static void init() { instance = new PopulationManager(); }
    public static PopulationManager getInstance() { return instance; }
    public void update() {}
    public int getTotalPopulation() { return 10000; }
    public int getLaborCount() { return 5500; }
}