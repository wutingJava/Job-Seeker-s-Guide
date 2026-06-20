package com.jobseeker.game.manager;

import com.jobseeker.game.entity.Enterprise;
import com.jobseeker.game.entity.Enterprise.EnterpriseState;
import com.jobseeker.game.entity.JobPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnterpriseManager {
    private static EnterpriseManager instance;

    private List<Enterprise> allEnterprises;
    private Random random = new Random();

    public static void init() {
        instance = new EnterpriseManager();
        instance.allEnterprises = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            int initialCount = getInitialEnterpriseCount(i);
            for (int j = 0; j < initialCount; j++) {
                instance.createEnterprise(i, 0);
            }
        }
    }

    private static int getInitialEnterpriseCount(int industryId) {
        return switch (industryId) {
            case 0 -> 50;
            case 1, 2 -> 15;
            case 3, 4 -> 10;
            case 5, 6 -> 5;
            default -> 10;
        };
    }

    public static EnterpriseManager getInstance() {
        return instance;
    }

    public void update() {
        for (Enterprise enterprise : allEnterprises) {
            if (enterprise.state == EnterpriseState.BANKRUPT) continue;

            updateEnterpriseProfit(enterprise);
            updateEnterpriseState(enterprise);

            if (enterprise.state == EnterpriseState.GROWING && enterprise.profit > 5000) {
                enterprise.maxEmployeeCount += 5;
            } else if (enterprise.state == EnterpriseState.DECLINE && enterprise.profit < -1000) {
                enterprise.maxEmployeeCount = Math.max(1, enterprise.maxEmployeeCount - 5);
            }
        }

        maybeCreateNewEnterprise();
    }

    private void updateEnterpriseProfit(Enterprise enterprise) {
        float employeeOutput = enterprise.employeeCount * enterprise.productivity;
        float wageCost = enterprise.employeeCount * enterprise.wageOffered;

        enterprise.profit += employeeOutput - wageCost;
        enterprise.profit += (random.nextFloat() - 0.5f) * 500;
    }

    private void updateEnterpriseState(Enterprise enterprise) {
        if (enterprise.profit < -2000) {
            enterprise.state = EnterpriseState.DECLINE;
        } else if (enterprise.profit > 10000) {
            enterprise.state = EnterpriseState.MATURE;
        } else if (enterprise.profit > 20000) {
            enterprise.state = EnterpriseState.GROWING;
        }

        if (enterprise.profit < -5000) {
            enterprise.state = EnterpriseState.BANKRUPT;
        }
    }

    private void maybeCreateNewEnterprise() {
        float gdpGrowth = GameStateManager.getInstance().getGdpPerCapita();
        if (gdpGrowth > 200 && random.nextFloat() < 0.05f) {
            int industry = random.nextInt(7);
            createEnterprise(industry, random.nextInt(4));
        }
    }

    public Enterprise createEnterprise(int industryId, int regionId) {
        Enterprise enterprise = new Enterprise(industryId, regionId);
        enterprise.productivity = getProductivityForEra(industryId);
        enterprise.wageOffered = PolicyManager.getInstance().getMinimumWage() * (1 + random.nextFloat());
        allEnterprises.add(enterprise);
        return enterprise;
    }

    private float getProductivityForEra(int industryId) {
        float base = switch (industryId) {
            case 0 -> 50f;
            case 1 -> 40f;
            case 2 -> 45f;
            case 3 -> 35f;
            case 4 -> 40f;
            case 5 -> 60f;
            case 6 -> 30f;
            default -> 40f;
        };

        float techLevel = GameStateManager.getInstance().getTechLevel();
        return base * (1 + techLevel);
    }

    public List<Enterprise> getEnterprisesByIndustry(int industryId) {
        List<Enterprise> result = new ArrayList<>();
        for (Enterprise e : allEnterprises) {
            if (e.industryId == industryId && e.state != EnterpriseState.BANKRUPT) {
                result.add(e);
            }
        }
        return result;
    }

    public List<Enterprise> getAllEnterprises() {
        return new ArrayList<>(allEnterprises);
    }

    public List<JobPost> getAllJobPosts() {
        List<JobPost> allPosts = new ArrayList<>();
        for (Enterprise e : allEnterprises) {
            if (e.state != EnterpriseState.BANKRUPT) {
                allPosts.addAll(e.jobPosts);
            }
        }
        return allPosts;
    }

    public int getIndustryEmployeeCount(int industryId) {
        int count = 0;
        for (Enterprise e : allEnterprises) {
            if (e.industryId == industryId) {
                count += e.employeeCount;
            }
        }
        return count;
    }

    public int getTotalEmployees() {
        int count = 0;
        for (Enterprise e : allEnterprises) {
            count += e.employeeCount;
        }
        return count;
    }

    public int getActiveEnterpriseCount() {
        int count = 0;
        for (Enterprise e : allEnterprises) {
            if (e.state != EnterpriseState.BANKRUPT) count++;
        }
        return count;
    }
}
