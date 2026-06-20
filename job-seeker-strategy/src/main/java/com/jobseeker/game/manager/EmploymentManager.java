package com.jobseeker.game.manager;

import com.jobseeker.game.Game;

import com.jobseeker.game.entity.Enterprise;
import com.jobseeker.game.entity.Enterprise.EnterpriseState;
import com.jobseeker.game.entity.JobPost;
import com.jobseeker.game.entity.Person;
import com.jobseeker.game.entity.Person.PersonState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EmploymentManager {
    private static EmploymentManager instance;

    private float currentUnemploymentRate;
    private Random random = new Random();

    public static void init() {
        instance = new EmploymentManager();
    }

    public static EmploymentManager getInstance() {
        return instance;
    }

    public void update() {
        // 收集招聘岗位
        List<JobPost> availableJobs = collectAvailableJobs();

        // 收集失业人口
        List<Person> unemployedPersons = collectUnemployedPersons();

        // 执行就业匹配
        performMatching(unemployedPersons, availableJobs);

        // 计算失业率
        calculateUnemploymentRate();

        // 增加GDP（基于就业产出）
        updateGDP();
    }

    private List<JobPost> collectAvailableJobs() {
        List<JobPost> jobs = new ArrayList<>();
        List<Enterprise> enterprises = EnterpriseManager.getInstance().getAllEnterprises();

        for (Enterprise enterprise : enterprises) {
            if (enterprise.state == EnterpriseState.BANKRUPT) continue;

            int vacancies = enterprise.maxEmployeeCount - enterprise.employeeCount;
            if (vacancies > 0) {
                for (int i = 0; i < vacancies; i++) {
                    jobs.add(new JobPost(
                        enterprise.id,
                        enterprise.industryId,
                        getRequiredSkill(enterprise.industryId),
                        getRequiredEducation(enterprise.industryId),
                        enterprise.wageOffered
                    ));
                }
            }
        }
        return jobs;
    }

    private List<Person> collectUnemployedPersons() {
        List<Person> persons = new ArrayList<>();
        for (Person person : PopulationManager.getInstance().getLaborForce()) {
            if (person.state == PersonState.UNEMPLOYED) {
                persons.add(person);
            }
        }
        return persons;
    }

    private void performMatching(List<Person> unemployedPersons, List<JobPost> availableJobs) {
        for (Person person : unemployedPersons) {
            if (availableJobs.isEmpty()) break;

            JobPost bestJob = findBestMatch(person, availableJobs);
            if (bestJob != null && calculateEmploymentProbability(person, bestJob) > 0.5f) {
                person.state = PersonState.EMPLOYED;
                person.industryId = bestJob.industryId;
                person.currentWage = bestJob.wage;

                Enterprise enterprise = findEnterprise(bestJob.enterpriseId);
                if (enterprise != null) {
                    enterprise.hirePerson();
                }
                availableJobs.remove(bestJob);
            }
        }
    }

    private JobPost findBestMatch(Person person, List<JobPost> jobs) {
        JobPost best = null;
        float bestScore = -1;

        for (JobPost job : jobs) {
            float score = calculateMatchScore(person, job);
            if (score > bestScore) {
                bestScore = score;
                best = job;
            }
        }
        return best;
    }

    private float calculateMatchScore(Person person, JobPost job) {
        float skillScore = Math.max(0, 1 - Math.abs(person.skill - job.requiredSkill) / 50);
        float eduScore = Math.max(0, 1 - Math.abs(person.education - job.requiredEducation) / 50);
        float wageScore = person.currentWage > 0 ? job.wage / person.currentWage : 1f;
        return skillScore * 0.4f + eduScore * 0.3f + wageScore * 0.3f;
    }

    private float calculateEmploymentProbability(Person person, JobPost job) {
        float baseMatch = calculateMatchScore(person, job);
        float luckFactor = person.luck / 100f;
        float policyBonus = PolicyManager.getInstance().getEmploymentSubsidyBonus();
        float industryBonus = getIndustryProsperityBonus(job.industryId);

        float probability = baseMatch * (1 + luckFactor * 0.2f + policyBonus + industryBonus);
        return Math.max(0.1f, Math.min(0.9f, probability));
    }

    private float getIndustryProsperityBonus(int industryId) {
        return (float) (Math.sin(industryId + Game.getInstance().getCurrentYear()) * 0.1f);
    }

    private Enterprise findEnterprise(int enterpriseId) {
        for (Enterprise e : EnterpriseManager.getInstance().getAllEnterprises()) {
            if (e.id == enterpriseId) return e;
        }
        return null;
    }

    private float getRequiredSkill(int industryId) {
        float eraTech = GameStateManager.getInstance().getTechLevel();
        float baseSkill = switch (industryId) {
            case 0 -> 10f;
            case 1 -> 20f;
            case 2 -> 15f;
            case 3 -> 15f;
            case 4 -> 25f;
            case 5 -> 40f;
            case 6 -> 20f;
            default -> 20f;
        };
        return baseSkill * (0.5f + eraTech);
    }

    private float getRequiredEducation(int industryId) {
        float eraTech = GameStateManager.getInstance().getTechLevel();
        float baseEdu = switch (industryId) {
            case 0 -> 5f;
            case 1 -> 15f;
            case 2 -> 10f;
            case 3 -> 10f;
            case 4 -> 20f;
            case 5 -> 45f;
            case 6 -> 25f;
            default -> 15f;
        };
        return baseEdu * (0.5f + eraTech);
    }

    private void calculateUnemploymentRate() {
        int laborCount = PopulationManager.getInstance().getLaborCount();
        int unemployedCount = PopulationManager.getInstance().getUnemployedCount();
        currentUnemploymentRate = laborCount > 0 ? (float) unemployedCount / laborCount : 0;
    }

    private void updateGDP() {
        // 基于就业总产出增加GDP
        float totalWage = 0f;
        for (Person person : PopulationManager.getInstance().getLaborForce()) {
            if (person.state == PersonState.EMPLOYED) {
                totalWage += person.currentWage;
            }
        }
        GameStateManager.getInstance().addGdp(totalWage * 0.5f);

        // 科技进步加速
        float rdBonus = PolicyManager.getInstance().getRdSubsidy() / 1000f;
        GameStateManager.getInstance().incrementTechLevel(0.001f + rdBonus);
    }

    public float getUnemploymentRate() {
        return currentUnemploymentRate;
    }
}
