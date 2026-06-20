package com.jobseeker.game.manager;

public class FinanceManager {
    private static FinanceManager instance;
    public static void init() { instance = new FinanceManager(); }
    public static FinanceManager getInstance() { return instance; }
    public void update() {}
    public float getDebtToGdpRatio() { return 0f; }
}