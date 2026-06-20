package com.jobseeker.game.manager;

public class PolicyManager {
    private static PolicyManager instance;

    private float birthSubsidy;
    private int birthPolicyMode;
    private boolean oneChildPolicy;

    private float minimumWage;
    private float employmentSubsidy;

    private float rdSubsidy;
    private float pensionLevel;
    private float unemploymentBenefit;

    public static void init() {
        instance = new PolicyManager();
        instance.birthSubsidy = 0f;
        instance.birthPolicyMode = 0;
        instance.oneChildPolicy = false;
        instance.minimumWage = 20f;
        instance.employmentSubsidy = 0f;
        instance.rdSubsidy = 0f;
        instance.pensionLevel = 100f;
        instance.unemploymentBenefit = 50f;
    }

    public static PolicyManager getInstance() {
        return instance;
    }

    public float getBirthPolicyEffect() {
        float effect = 0f;
        if (birthPolicyMode == 1) {
            effect += 0.3f;
            effect += birthSubsidy / 1000f;
        } else if (birthPolicyMode == -1) {
            effect -= 0.5f;
        }
        if (oneChildPolicy) {
            effect -= 0.3f;
        }
        return effect;
    }

    public float getEmploymentSubsidyBonus() {
        return employmentSubsidy / 1000f;
    }

    public void setBirthSubsidy(float amount) { this.birthSubsidy = amount; }
    public void setBirthPolicyMode(int mode) { this.birthPolicyMode = mode; }
    public void setOneChildPolicy(boolean enabled) { this.oneChildPolicy = enabled; }
    public void setMinimumWage(float wage) { this.minimumWage = wage; }
    public void setEmploymentSubsidy(float subsidy) { this.employmentSubsidy = subsidy; }
    public void setRdSubsidy(float subsidy) { this.rdSubsidy = subsidy; }
    public void setPensionLevel(float level) { this.pensionLevel = level; }
    public void setUnemploymentBenefit(float benefit) { this.unemploymentBenefit = benefit; }

    public float getBirthSubsidy() { return birthSubsidy; }
    public int getBirthPolicyMode() { return birthPolicyMode; }
    public boolean isOneChildPolicy() { return oneChildPolicy; }
    public float getMinimumWage() { return minimumWage; }
    public float getEmploymentSubsidy() { return employmentSubsidy; }
    public float getRdSubsidy() { return rdSubsidy; }
    public float getPensionLevel() { return pensionLevel; }
    public float getUnemploymentBenefit() { return unemploymentBenefit; }
}
