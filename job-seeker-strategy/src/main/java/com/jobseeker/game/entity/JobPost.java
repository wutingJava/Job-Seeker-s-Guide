package com.jobseeker.game.entity;

public class JobPost {
    private static int idCounter = 0;
    public final int id;

    public int enterpriseId;
    public int industryId;
    public float requiredSkill;
    public float requiredEducation;
    public float requiredExperience;
    public float wage;
    public int applicants;
    public boolean filled;

    public JobPost(int enterpriseId, int industryId, float requiredSkill,
                   float requiredEducation, float wage) {
        this.id = idCounter++;
        this.enterpriseId = enterpriseId;
        this.industryId = industryId;
        this.requiredSkill = requiredSkill;
        this.requiredEducation = requiredEducation;
        this.requiredExperience = 0;
        this.wage = wage;
        this.applicants = 0;
        this.filled = false;
    }
}
