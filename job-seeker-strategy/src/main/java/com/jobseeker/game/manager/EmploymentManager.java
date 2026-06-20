package com.jobseeker.game.manager;

public class EmploymentManager {
    private static EmploymentManager instance;
    public static void init() { instance = new EmploymentManager(); }
    public static EmploymentManager getInstance() { return instance; }
    public void update() {}
    public float getUnemploymentRate() { return 0.2f; }
}