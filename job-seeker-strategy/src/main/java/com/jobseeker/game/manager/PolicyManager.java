package com.jobseeker.game.manager;

public class PolicyManager {
    private static PolicyManager instance;
    public static void init() { instance = new PolicyManager(); }
    public static PolicyManager getInstance() { return instance; }
}