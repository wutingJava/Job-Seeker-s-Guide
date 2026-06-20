package com.jobseeker.game.manager;

import com.jobseeker.game.Game;

import com.jobseeker.game.config.GameConfig;
import com.jobseeker.game.config.GameConfig.Era;
import com.jobseeker.game.entity.Person;
import com.jobseeker.game.entity.Person.PersonState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class PopulationManager {
    private static PopulationManager instance;

    private List<Person> allPersons;
    private int totalPopulation;

    private int childCount;
    private int laborCount;
    private int elderlyCount;
    private int fertilityWomen;

    private Random random = new Random();

    public static void init() {
        instance = new PopulationManager();
        instance.allPersons = new ArrayList<>();
        instance.totalPopulation = GameConfig.INITIAL_POPULATION;

        for (int i = 0; i < GameConfig.INITIAL_POPULATION; i++) {
            Person person = new Person();
            instance.allPersons.add(person);
        }

        instance.updateStatistics();
    }

    public static PopulationManager getInstance() {
        return instance;
    }

    public void update() {
        int currentMonth = Game.getInstance().getCurrentMonth();
        if (currentMonth != 1) return;

        applyBirth();
        applyDeath();
        applyAging();
        applyMigration();
        updateStatistics();
    }

    private void applyBirth() {
        float birthWillingness = calculateBirthWillingness();
        int actualBirths = (int)(fertilityWomen * birthWillingness * 0.1f);
        actualBirths = Math.max(0, actualBirths);

        for (int i = 0; i < actualBirths; i++) {
            allPersons.add(Person.createBaby());
        }

        totalPopulation += actualBirths;
    }

    private float calculateBirthWillingness() {
        float base = 1.0f;

        float happinessEffect = (GameStateManager.getInstance().getHappinessIndex() - 50) * 0.02f;

        float eraEffect = switch (GameStateManager.getInstance().getCurrentEra()) {
            case PRIMITIVE_LABOR -> 0.3f;
            case HANDCRAFT -> 0.2f;
            case STEAM_REVOLUTION -> 0.0f;
            case ELECTRICAL -> -0.1f;
            case INFORMATION -> -0.2f;
            case SMART_ERA -> -0.3f;
        };

        float policyEffect = PolicyManager.getInstance().getBirthPolicyEffect();
        float economicPressure = -FinanceManager.getInstance().getAverageLivingCost() / 1000f;

        return Math.max(0.1f, Math.min(2.0f, base + happinessEffect + eraEffect + policyEffect + economicPressure));
    }

    private void applyDeath() {
        List<Person> toRemove = new ArrayList<>();
        for (Person person : allPersons) {
            if (person.state == PersonState.DECEASED) {
                toRemove.add(person);
                continue;
            }
            if (person.age >= 60) {
                if (random.nextFloat() < 0.05f) {
                    person.state = PersonState.DECEASED;
                    toRemove.add(person);
                }
            } else if (person.age < 5) {
                if (random.nextFloat() < 0.02f) {
                    person.state = PersonState.DECEASED;
                    toRemove.add(person);
                }
            }
        }
        allPersons.removeAll(toRemove);
        totalPopulation -= toRemove.size();
    }

    private void applyAging() {
        for (Person person : allPersons) {
            if (person.state != PersonState.DECEASED) {
                person.ageOneYear();
            }
        }
    }

    private void applyMigration() {
        float netMigration = calculateNetMigration();
        int migrationCount = (int)(totalPopulation * Math.abs(netMigration));

        if (netMigration > 0) {
            for (int i = 0; i < migrationCount; i++) {
                allPersons.add(Person.createImmigrant());
            }
        } else if (netMigration < 0) {
            List<Person> toRemove = new ArrayList<>();
            for (Person person : allPersons) {
                if (toRemove.size() >= migrationCount) break;
                if (person.age >= 20 && person.age <= 40 && person.education > 30) {
                    toRemove.add(person);
                }
            }
            allPersons.removeAll(toRemove);
        }

        totalPopulation = allPersons.size();
    }

    private float calculateNetMigration() {
        float base = GameConfig.MIGRATION_BASE_RATE;

        float happiness = GameStateManager.getInstance().getHappinessIndex();
        if (happiness < 30) return -0.02f;
        if (happiness > 70) return 0.01f;

        float unemployment = EmploymentManager.getInstance().getUnemploymentRate();
        if (unemployment > 0.25f) return -0.015f;

        return base;
    }

    private void updateStatistics() {
        childCount = 0;
        laborCount = 0;
        elderlyCount = 0;
        fertilityWomen = 0;

        for (Person person : allPersons) {
            if (person.state == PersonState.DECEASED) continue;

            if (person.age < 15) {
                childCount++;
            } else if (person.age >= 15 && person.age < 60) {
                laborCount++;
                if (person.gender == Person.Gender.FEMALE && person.age >= 20 && person.age <= 45) {
                    fertilityWomen++;
                }
            } else {
                elderlyCount++;
            }
        }
    }

    public int getTotalPopulation() { return totalPopulation; }
    public int getLaborCount() { return laborCount; }
    public int getChildCount() { return childCount; }
    public int getElderlyCount() { return elderlyCount; }
    public int getFertilityWomen() { return fertilityWomen; }

    public float getDependencyRatio() {
        return laborCount > 0 ? (float)(childCount + elderlyCount) / laborCount : 1f;
    }

    public List<Person> getLaborForce() {
        List<Person> laborForce = new ArrayList<>();
        for (Person person : allPersons) {
            if (person.age >= 15 && person.age < 60 && person.state != PersonState.DECEASED) {
                laborForce.add(person);
            }
        }
        return laborForce;
    }

    public int getEmployedCount() {
        int count = 0;
        for (Person person : allPersons) {
            if (person.state == PersonState.EMPLOYED) count++;
        }
        return count;
    }

    public int getUnemployedCount() {
        int count = 0;
        for (Person person : allPersons) {
            if (person.state == PersonState.UNEMPLOYED) count++;
        }
        return count;
    }
}
