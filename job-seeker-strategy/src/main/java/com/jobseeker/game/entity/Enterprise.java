package com.jobseeker.game.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Enterprise {
    private static int idCounter = 0;
    public final int id;

    public String name;
    public int industryId;
    public int regionId;
    public float productivity;
    public float profit;
    public float wageOffered;
    public int employeeCount;
    public int maxEmployeeCount;
    public EnterpriseState state;

    public List<JobPost> jobPosts;

    public Enterprise(int industryId, int regionId) {
        this.id = idCounter++;
        this.industryId = industryId;
        this.regionId = regionId;
        this.profit = 1000 + new Random().nextFloat() * 5000;
        this.wageOffered = 30 + new Random().nextFloat() * 20;
        this.employeeCount = 10 + new Random().nextInt(50);
        this.maxEmployeeCount = this.employeeCount * 2;
        this.state = EnterpriseState.GROWING;
        this.jobPosts = new ArrayList<>();
        this.name = generateName();
    }

    private String generateName() {
        String[] prefixes = {"新兴", "华星", "中联", "国泰", "民富", "盛世", "宏图", "未来"};
        String[] suffixes = {"公司", "企业", "集团", "工厂", "农场", "联盟", "工作室"};
        String prefix = prefixes[new Random().nextInt(prefixes.length)];
        String suffix = suffixes[new Random().nextInt(suffixes.length)];
        return prefix + suffix;
    }

    public void update() {
        if (profit < 0) {
            employeeCount = Math.max(0, employeeCount - 1);
        }
        if (employeeCount == 0) {
            state = EnterpriseState.BANKRUPT;
        }
    }

    public void hirePerson() {
        if (employeeCount < maxEmployeeCount) {
            employeeCount++;
        }
    }

    public boolean firePerson() {
        if (employeeCount > 1) {
            employeeCount--;
            return true;
        }
        return false;
    }

    public enum EnterpriseState {
        STARTUP,
        GROWING,
        MATURE,
        DECLINE,
        BANKRUPT
    }
}
