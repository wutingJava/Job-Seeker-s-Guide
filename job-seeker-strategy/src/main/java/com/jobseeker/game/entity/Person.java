package com.jobseeker.game.entity;

import java.util.Random;

public class Person {
    private static int idCounter = 0;
    public final int id;

    // 基本属性
    public int age;
    public Gender gender;
    public float education;
    public float skill;
    public float experience;
    public float luck;

    // 状态
    public PersonState state;
    public int industryId;
    public float currentWage;

    // 家庭相关
    public int familySize;
    public float familyIncome;

    public Person() {
        this.id = idCounter++;
        this.age = 15 + new Random().nextInt(10);
        this.gender = Math.random() < 0.5 ? Gender.MALE : Gender.FEMALE;
        this.education = 5 + new Random().nextInt(10);
        this.skill = 5 + new Random().nextInt(10);
        this.experience = 0;
        this.luck = 30 + new Random().nextInt(40);
        this.state = PersonState.UNEMPLOYED;
        this.familySize = 1 + new Random().nextInt(4);
    }

    public static Person createBaby() {
        Person baby = new Person();
        baby.age = 0;
        baby.state = PersonState.CHILD;
        baby.education = 0;
        baby.skill = 0;
        baby.experience = 0;
        baby.luck = 30 + new Random().nextInt(40);
        return baby;
    }

    public static Person createImmigrant() {
        Person immigrant = new Person();
        immigrant.age = 20 + new Random().nextInt(20);
        immigrant.education = 30 + new Random().nextInt(40);
        immigrant.skill = 30 + new Random().nextInt(40);
        immigrant.state = PersonState.UNEMPLOYED;
        return immigrant;
    }

    public void ageOneYear() {
        this.age++;
        this.experience = Math.min(50, this.experience + 1);

        if (this.age >= 60) {
            this.state = PersonState.RETIRED;
        } else if (this.age >= 18 && this.state == PersonState.CHILD) {
            this.state = PersonState.UNEMPLOYED;
        }
    }

    public enum Gender {
        MALE, FEMALE
    }

    public enum PersonState {
        CHILD,
        STUDENT,
        UNEMPLOYED,
        EMPLOYED,
        RETIRED,
        DECEASED
    }
}
