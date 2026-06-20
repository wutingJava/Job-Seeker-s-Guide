package com.jobseeker.game.manager;

public class EnterpriseManager {
    private static EnterpriseManager instance;
    public static void init() { instance = new EnterpriseManager(); }
    public static EnterpriseManager getInstance() { return instance; }
    public void update() {}
}