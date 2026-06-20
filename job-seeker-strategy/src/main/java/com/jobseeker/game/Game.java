package com.jobseeker.game;

import com.jobseeker.game.manager.*;

public class Game {
    private static Game instance;

    private boolean running = true;
    private boolean paused = false;
    private int currentYear = 1;
    private int currentMonth = 1;

    private Game() {
    }

    public static void createInstance() {
        if (instance == null) {
            instance = new Game();
        }
    }

    public static Game getInstance() {
        return instance;
    }

    public void init() {
        GameStateManager.init();
        PolicyManager.init();
        FinanceManager.init();
        PopulationManager.init();
        EnterpriseManager.init();
        EmploymentManager.init();
        EventManager.init();
    }

    public void tick() {
        currentMonth++;
        if (currentMonth > 12) {
            currentMonth = 1;
            currentYear++;
        }

        PopulationManager.getInstance().update();
        EnterpriseManager.getInstance().update();
        EmploymentManager.getInstance().update();
        FinanceManager.getInstance().update();
        EventManager.getInstance().update();
        GameStateManager.getInstance().update();
    }

    public void pause() { this.paused = true; }
    public void resume() { this.paused = false; }
    public boolean isPaused() { return paused; }
    public int getCurrentYear() { return currentYear; }
    public int getCurrentMonth() { return currentMonth; }
    public boolean isRunning() { return running; }
    public void setRunning(boolean running) { this.running = running; }
}
