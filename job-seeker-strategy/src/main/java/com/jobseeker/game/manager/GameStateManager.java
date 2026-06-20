package com.jobseeker.game.manager;

import com.jobseeker.game.Game;
import com.jobseeker.game.config.GameConfig.Era;

public class GameStateManager {
    private static GameStateManager instance;

    private Era currentEra;
    private float techLevel;
    private float gdp;
    private float gdpPerCapita;
    private float inflationRate;
    private float happinessIndex;
    private float giniCoefficient;

    private Difficulty difficulty;

    public static void init() {
        instance = new GameStateManager();
        instance.currentEra = Era.PRIMITIVE_LABOR;
        instance.techLevel = 0.05f;
        instance.gdp = 1000000f;
        instance.gdpPerCapita = 100f;
        instance.inflationRate = 0.02f;
        instance.happinessIndex = 50f;
        instance.giniCoefficient = 0.4f;
        instance.difficulty = Difficulty.NORMAL;
    }

    public static GameStateManager getInstance() {
        return instance;
    }

    public void update() {
        int totalPopulation = PopulationManager.getInstance().getTotalPopulation();
        gdpPerCapita = totalPopulation > 0 ? gdp / totalPopulation : 0;

        updateHappiness();
        checkEraTransition();
    }

    private void updateHappiness() {
        float employmentEffect = (1 - EmploymentManager.getInstance().getUnemploymentRate()) * 20;
        float gdpEffect = Math.min(20, gdpPerCapita / 50);
        float giniEffect = (1 - giniCoefficient) * 15;
        float inflationEffect = Math.max(0, 15 - inflationRate * 100);

        happinessIndex = 20 + employmentEffect + gdpEffect + giniEffect + inflationEffect;
        happinessIndex = Math.max(0, Math.min(100, happinessIndex));
    }

    private void checkEraTransition() {
        Era nextEra = getNextEra();
        if (nextEra != null) {
            if (techLevel >= nextEra.techRequired && gdpPerCapita >= nextEra.gdpRequired * 1000) {
                transitionToEra(nextEra);
            }
        }
    }

    private Era getNextEra() {
        return switch (currentEra) {
            case PRIMITIVE_LABOR -> Era.HANDCRAFT;
            case HANDCRAFT -> Era.STEAM_REVOLUTION;
            case STEAM_REVOLUTION -> Era.ELECTRICAL;
            case ELECTRICAL -> Era.INFORMATION;
            case INFORMATION -> Era.SMART_ERA;
            case SMART_ERA -> null;
        };
    }

    private void transitionToEra(Era newEra) {
        currentEra = newEra;
        EventManager.getInstance().triggerEraTransitionEvent(newEra);
    }

    public Era getCurrentEra() { return currentEra; }
    public float getTechLevel() { return techLevel; }
    public float getGdp() { return gdp; }
    public float getGdpPerCapita() { return gdpPerCapita; }
    public float getInflationRate() { return inflationRate; }
    public float getHappinessIndex() { return happinessIndex; }
    public float getGiniCoefficient() { return giniCoefficient; }

    public void setTechLevel(float level) { this.techLevel = level; }
    public void setHappinessIndex(float value) { this.happinessIndex = value; }
    public void setInflationRate(float rate) { this.inflationRate = rate; }
    public void setGiniCoefficient(float coef) { this.giniCoefficient = coef; }

    public void addGdp(float amount) {
        this.gdp = Math.max(0, this.gdp + amount);
    }

    public void incrementTechLevel(float amount) {
        this.techLevel = Math.min(1.0f, this.techLevel + amount);
    }

    public enum Difficulty {
        HARMONY,
        NORMAL,
        CHALLENGE
    }
}
