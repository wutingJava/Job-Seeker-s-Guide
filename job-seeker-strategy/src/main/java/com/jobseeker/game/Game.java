package com.jobseeker.game;

import com.jme3.app.SimpleApplication;
import com.jme3.scene.Node;
import com.jobseeker.game.manager.*;

public class Game {
    private static Game instance;
    private SimpleApplication app;
    private Node rootNode;
    private boolean paused = false;
    private float gameTime = 0f;
    private int currentYear = 1;
    private int currentMonth = 1;
    
    public static Game getInstance() {
        return instance;
    }
    
    public Game(SimpleApplication app) {
        instance = this;
        this.app = app;
        this.rootNode = app.getRootNode();
    }
    
    public void init() {
        System.out.println("[Game] Initializing...");
        
        // 初始化各管理器
        GameStateManager.init();
        System.out.println("[Game] GameStateManager initialized");
        
        PopulationManager.init();
        System.out.println("[Game] PopulationManager initialized");
        
        EnterpriseManager.init();
        System.out.println("[Game] EnterpriseManager initialized");
        
        FinanceManager.init();
        System.out.println("[Game] FinanceManager initialized");
        
        PolicyManager.init();
        System.out.println("[Game] PolicyManager initialized");
        
        EmploymentManager.init();
        System.out.println("[Game] EmploymentManager initialized");
        
        EventManager.init();
        System.out.println("[Game] EventManager initialized");
        
        System.out.println("[Game] All managers initialized. Game ready!");
    }
    
    public void update(float tpf) {
        if (paused) return;
        
        gameTime += tpf;
        
        // 每60秒=1游戏月
        if (gameTime >= 60f) {
            gameTime = 0f;
            tick();
        }
    }
    
    private void tick() {
        currentMonth++;
        if (currentMonth > 12) {
            currentMonth = 1;
            currentYear++;
            System.out.println("[Game] Year " + currentYear + " begins!");
        }
        
        // 更新各系统
        PopulationManager.update();
        EnterpriseManager.update();
        EmploymentManager.update();
        FinanceManager.update();
        EventManager.update();
        GameStateManager.update();
        
        // 打印状态（每6个月）
        if (currentMonth % 6 == 0) {
            printStatus();
        }
    }
    
    private void printStatus() {
        GameStateManager gsm = GameStateManager.getInstance();
        PopulationManager pm = PopulationManager.getInstance();
        FinanceManager fm = FinanceManager.getInstance();
        
        System.out.println("========== Game Status ==========");
        System.out.printf("Year %d, Month %d | Era: %s%n", 
            currentYear, currentMonth, gsm.getCurrentEra().name);
        System.out.printf("Population: %d | GDP: %.2f | Tech: %.2f%n", 
            pm.getTotalPopulation(), gsm.getGdp(), gsm.getTechLevel());
        System.out.printf("Unemployment: %.1f%% | Happiness: %.1f | Debt/GDP: %.2f%n",
            EmploymentManager.getInstance().getUnemploymentRate() * 100,
            gsm.getHappinessIndex(),
            fm.getDebtToGdpRatio());
        System.out.println("===================================");
    }
    
    public void pause() { this.paused = true; }
    public void resume() { this.paused = false; }
    public int getCurrentYear() { return currentYear; }
    public int getCurrentMonth() { return currentMonth; }
    public boolean isPaused() { return paused; }
    
    public SimpleApplication getApp() { return app; }
    public Node getRootNode() { return rootNode; }
}