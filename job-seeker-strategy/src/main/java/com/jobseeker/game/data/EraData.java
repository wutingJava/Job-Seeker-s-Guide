package com.jobseeker.game.data;

public class EraData {
    public float techLevel;
    public float gdpPerCapita;
    public float employmentRate;

    // 各行业在当前时代的特征
    public IndustryEraData[] industryEraData = new IndustryEraData[7];

    public static class IndustryEraData {
        public int industryId;
        public float productivity;
        public float wageLevel;
        public float skillRequirement;
        public float educationRequirement;
        public float laborRatio;
    }

    public static EraData getInitialEraData() {
        EraData data = new EraData();
        data.techLevel = 0.05f;
        data.gdpPerCapita = 100f;
        data.employmentRate = 0.7f;

        float[] baseProductivity = {50f, 30f, 40f, 20f, 25f, 10f, 15f};
        float[] baseWage = {30f, 25f, 35f, 20f, 40f, 15f, 20f};
        float[] baseSkill = {5f, 10f, 8f, 5f, 15f, 20f, 10f};
        float[] baseEdu = {5f, 8f, 10f, 5f, 15f, 25f, 10f};
        float[] baseLabor = {0.90f, 0.03f, 0.02f, 0.02f, 0.01f, 0.01f, 0.01f};

        for (int i = 0; i < 7; i++) {
            IndustryEraData industry = new IndustryEraData();
            industry.industryId = i;
            industry.productivity = baseProductivity[i];
            industry.wageLevel = baseWage[i];
            industry.skillRequirement = baseSkill[i];
            industry.educationRequirement = baseEdu[i];
            industry.laborRatio = baseLabor[i];
            data.industryEraData[i] = industry;
        }
        return data;
    }
}
