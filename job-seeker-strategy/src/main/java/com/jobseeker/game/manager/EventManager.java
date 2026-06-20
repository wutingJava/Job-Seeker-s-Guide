package com.jobseeker.game.manager;

public class EventManager {
    private static EventManager instance;
    public static void init() { instance = new EventManager(); }
    public static EventManager getInstance() { return instance; }
    public void update() {}
}