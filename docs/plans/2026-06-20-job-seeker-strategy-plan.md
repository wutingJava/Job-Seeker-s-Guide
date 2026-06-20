# 《求职者攻略》游戏实现计划 v1.0

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建一款完整的PC策略模拟游戏，玩家扮演社会管理者，通过政策调控推动人类社会从原始劳动时代演进至大智能时代。

**Architecture:** 基于JMonkeyEngine 3.x的Java游戏，采用模块化设计，核心系统包括：经济系统、人才系统、企业系统、财政系统、人口系统、事件系统。

**Tech Stack:** Java 17+, JMonkeyEngine 3.x, Gradle构建

---

## 项目结构

```
job-seeker-strategy/
├── build.gradle
├── settings.gradle
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── jobseeker/
│       │           └── game/
│       │               ├── Main.java                    # 游戏入口
│       │               ├── Game.java                   # 游戏主循环
│       │               ├── config/                     # 配置
│       │               │   └── GameConfig.java         # 游戏配置常量
│       │               ├── data/                        # 数据模型
│       │               │   ├── Time.java               # 时代数据(6个时代定义)
│       │               │   ├── Industry.java           # 行业数据(7大行业)
│       │               │   └── JobType.java            # 岗位类型数据
│       │               ├── entity/                     # 游戏实体
│       │               │   ├── Person.java             # 市民/求职者
│       │               │   ├── Enterprise.java         # 企业
│       │               │   ├── JobPost.java            # 岗位
│       │               │   └── Building.java          # 建筑
│       │               ├── manager/                   # 核心管理器
│       │               │   ├── GameStateManager.java   # 游戏状态管理
│       │               │   ├── EconomyManager.java     # 经济系统
│       │               │   ├── PopulationManager.java  # 人口系统
│       │               │   ├── EnterpriseManager.java # 企业管理
│       │               │   ├── EmploymentManager.java  # 就业匹配
│       │               │   ├── FinanceManager.java     # 财政系统
│       │               │   ├── PolicyManager.java     # 政策系统
│       │               │   └── EventManager.java      # 事件系统
│       │               ├── ui/                         # UI层
│       │               │   ├── GameUI.java            # 主UI
│       │               │   ├── DashboardPanel.java    # 仪表盘
│       │               │   ├── PolicyPanel.java       # 政策面板
│       │               │   └── MapView.java           # 地图视图
│       │               └── util/                       # 工具类
│       │                   └── RandomUtil.java
│       └── resources/
│           └── assets/
│               └── textures/                          # 临时占位纹理
└── docs/
    └── plans/
```

---

## Phase 1: 项目框架与核心数据结构

### Task 1: 项目初始化

**Files:**
- Create: `job-seeker-strategy/build.gradle`
- Create: `job-seeker-strategy/settings.gradle`
- Create: `job-seeker-strategy/src/main/java/com/jobseeker/game/Main.java`
- Create: `job-seeker-strategy/src/main/java/com/jobseeker/game/Game.java`
- Create: `job-seeker-strategy/src/main/java/com/jobseeker/game/config/GameConfig.java`

- [ ] **Step 1: 创建Gradle项目配置**

```gradle
// build.gradle
plugins {
    id 'java'
    id 'application'
}

group = 'com.jobseeker'
version = '1.0.0'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.jmonkeyengine:jme3-core:3.7.0'
    implementation 'org.jmonkeyengine:jme3-desktop:3.7.0'
    implementation 'org.jmonkeyengine:jme3-lwjgl:3.7.0'
    implementation 'org.jmonkeyengine:jme3-jogg:3.7.0'
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

application {
    mainClass = 'com.jobseeker.game.Main'
}
```

- [ ] **Step 2: 创建游戏入口**

```java
// Main.java
package com.jobseeker.game;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;

public class Main extends SimpleApplication {
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        Game game = new Game(this);
        game.init();
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        Game game = Game.getInstance();
        if (game != null) {
            game.update(tpf);
        }
    }
    
    @Override
    public void simpleRender(RenderManager rm) {
        super.simpleRender(rm);
    }
}
```

- [ ] **Step 3: 创建游戏主循环**

```java
// Game.java
package com.jobseeker.game;

import com.jme3.app.SimpleApplication;
import com.jme3.scene.Node;

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
        // 初始化游戏状态
        GameStateManager.init();
        // 初始化经济系统
        EconomyManager.init();
        // 初始化人口系统
        PopulationManager.init();
        // 初始化企业系统
        EnterpriseManager.init();
        // 初始化财政系统
        FinanceManager.init();
        // 初始化UI
        GameUI.init(app.getAssetManager(), app.getGuiNode());
    }
    
    public void update(float tpf) {
        if (paused) return;
        
        gameTime += tpf;
        
        // 每月结算（简化：每60秒=1月）
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
        }
        
        // 更新各系统
        PopulationManager.update();
        EnterpriseManager.update();
        EmploymentManager.update();
        FinanceManager.update();
        EventManager.update();
        
        // 检查时代演进
        checkEraEvolution();
    }
    
    private void checkEraEvolution() {
        // 根据科技进步率和条件检查是否进入新时代
    }
    
    public void pause() { this.paused = true; }
    public void resume() { this.paused = false; }
    public int getCurrentYear() { return currentYear; }
    public int getCurrentMonth() { return currentMonth; }
}
```

- [ ] **Step 4: 创建游戏配置常量**

```java
// GameConfig.java
package com.jobseeker.game.config;

public class GameConfig {
    // 时间配置
    public static final float SECONDS_PER_MONTH = 60f;
    
    // 时代定义
    public enum Era {
        PRIMITIVE_LABOR(1, "原始劳动时代", 0, 0),
        HANDCRAFT(2, "手工场时代", 0.2f, 0.3f),
        STEAM_REVOLUTION(3, "蒸汽革命时代", 0.4f, 0.5f),
        ELECTRICAL(4, "电气时代", 0.6f, 0.7f),
        INFORMATION(5, "信息时代", 0.8f, 0.85f),
        SMART_ERA(6, "大智能时代", 1.0f, 1.0f);
        
        public final int id;
        public final String name;
        public final float techRequired;
        public final float gdpRequired;
        
        Era(int id, String name, float techRequired, float gdpRequired) {
            this.id = id;
            this.name = name;
            this.techRequired = techRequired;
            this.gdpRequired = gdpRequired;
        }
    }
    
    // 行业定义
    public enum Industry {
        AGRICULTURE("农业"),
        MANUFACTURING("制造业"),
        COMMERCE("商业贸易"),
        TRANSPORTATION("交通运输"),
        SERVICE("服务业"),
        TECHNOLOGY("科技研发"),
        PUBLIC("公共事业");
        
        public final String name;
        Industry(String name) { this.name = name; }
    }
    
    // 人口配置
    public static final int INITIAL_POPULATION = 10000;
    public static final float BIRTH_RATE_BASE = 0.02f;
    public static final float DEATH_RATE_BASE = 0.01f;
    public static final float MIGRATION_BASE_RATE = 0.001f;
}
```

- [ ] **Step 5: 提交代码**

```bash
cd job-seeker-strategy && git init && git add . && git commit -m "feat: 项目框架初始化，Gradle配置，JMonkeyEngine基础设置"
```

---

### Task 2: 数据模型定义

**Files:**
- Create: `src/main/java/com/jobseeker/game/data/Time.java`
- Create: `src/main/java/com/jobseeker/game/data/Industry.java`
- Create: `src/main/java/com/jobseeker/game/data/JobType.java`
- Create: `src/main/java/com/jobseeker/game/data/EraData.java`

- [ ] **Step 1: 创建时代数据**

```java
// EraData.java
package com.jobseeker.game.data;

import com.jobseeker.game.config.GameConfig.Era;

public class EraData {
    public Era era;
    public float techLevel;
    public float gdpPerCapita;
    public float employmentRate;
    
    // 各行业在当前时代的特征
    public IndustryEra特征[] = new IndustryEra特征[7];
    
    public static class IndustryEra特征 {
        public GameConfig.Industry industry;
        public float productivity;      // 人均产出乘数
        public float wageLevel;        // 工资水平乘数
        public float skillRequirement; // 技能要求
        public float educationRequirement; // 学历要求
        public float laborRatio;       // 劳动力占比
    }
    
    public static EraData getInitialEraData() {
        EraData data = new EraData();
        data.era = Era.PRIMITIVE_LABOR;
        data.techLevel = 0.05f;
        data.gdpPerCapita = 100f;
        data.employmentRate = 0.7f;
        
        // 初始化各行业特征
        for (int i = 0; i < 7; i++) {
            data.industryEra特征[i] = new IndustryEra特征();
            data.industryEra特征[i].industry = GameConfig.Industry.values()[i];
            data.industryEra特征[i].productivity = getInitialProductivity(i);
            data.industryEra特征[i].wageLevel = getInitialWage(i);
            data.industryEra特征[i].skillRequirement = getInitialSkillReq(i);
            data.industryEra特征[i].educationRequirement = getInitialEduReq(i);
            data.industryEra特征[i].laborRatio = getInitialLaborRatio(i);
        }
        return data;
    }
    
    private static float getInitialProductivity(int industryIndex) {
        // 根据行业和时代返回初始人均产出
        float[] baseProductivity = {50f, 30f, 40f, 20f, 25f, 10f, 15f};
        return baseProductivity[industryIndex];
    }
    
    private static float getInitialWage(int industryIndex) {
        float[] baseWage = {30f, 25f, 35f, 20f, 40f, 15f, 20f};
        return baseWage[industryIndex];
    }
    
    private static float getInitialSkillReq(int industryIndex) {
        float[] skillReq = {5f, 10f, 8f, 5f, 15f, 20f, 10f};
        return skillReq[industryIndex];
    }
    
    private static float getInitialEduReq(int industryIndex) {
        float[] eduReq = {5f, 8f, 10f, 5f, 15f, 25f, 10f};
        return eduReq[industryIndex];
    }
    
    private static float getInitialLaborRatio(int industryIndex) {
        // 原始时代：农业占90%
        float[] ratios = {0.90f, 0.03f, 0.02f, 0.02f, 0.01f, 0.01f, 0.01f};
        return ratios[industryIndex];
    }
}
```

- [ ] **Step 2: 创建市民/求职者实体**

```java
// Person.java
package com.jobseeker.game.entity;

import java.util.Random;

public class Person {
    private static int idCounter = 0;
    public final int id;
    
    // 基本属性
    public int age;                    // 年龄
    public Gender gender;              // 性别
    public float education;            // 学历 1-100
    public float skill;               // 技能 1-100
    public float experience;          // 经验 0-50
    public float luck;                // 运气 0-100
    
    // 状态
    public PersonState state;         // 状态：CHILD, STUDENT, UNEMPLOYED, EMPLOYED, RETIRED, DECEASED
    public int industryId;            // 所在行业（如果是就业状态）
    public float currentWage;          // 当前工资
    
    // 家庭相关（简化）
    public int familySize;            // 家庭成员数
    public float familyIncome;        // 家庭收入
    
    public Person() {
        this.id = idCounter++;
        this.age = 15 + new Random().nextInt(10);  // 初始15-24岁
        this.gender = Math.random() < 0.5 ? Gender.MALE : Gender.FEMALE;
        this.education = 5 + new Random().nextInt(10);  // 初始学历5-15
        this.skill = 5 + new Random().nextInt(10);      // 初始技能5-15
        this.experience = 0;
        this.luck = 30 + new Random().nextInt(40);      // 初始运气30-70
        this.state = PersonState.UNEMPLOYED;
        this.familySize = 1 + new Random().nextInt(4);
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
        CHILD,      // 0-14岁
        STUDENT,    // 求学中
        UNEMPLOYED, // 失业
        EMPLOYED,   // 就业
        RETIRED,    // 退休
        DECEASED    // 死亡
    }
}
```

- [ ] **Step 3: 创建企业实体**

```java
// Enterprise.java
package com.jobseeker.game.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Enterprise {
    private static int idCounter = 0;
    public final int id;
    
    public String name;
    public int industryId;            // 所属行业
    public int regionId;              // 所在区域
    public float productivity;        // 生产力水平
    public float profit;             // 当前利润
    public float wageOffered;         // 提供工资
    public int employeeCount;         // 员工数
    public int maxEmployeeCount;     // 最大员工数
    public EnterpriseState state;     // 企业状态
    
    public List<JobPost> jobPosts;   // 招聘岗位
    
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
        String[] prefixes = {"新兴", "华星", "中联", "国泰", "民富"};
        String[] suffixes = {"公司", "企业", "集团", "工厂", "农场"};
        return prefixes[new Random().nextInt(prefixes.length)] 
             + suffixes[new Random().nextInt(suffixes.length)];
    }
    
    public void update() {
        // 更新企业状态
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
        STARTUP,    // 初创
        GROWING,    // 成长
        MATURE,     // 成熟
        DECLINE,    // 衰退
        BANKRUPT    // 倒闭
    }
}
```

- [ ] **Step 4: 创建岗位实体**

```java
// JobPost.java
package com.jobseeker.game.entity;

import java.util.Random;

public class JobPost {
    private static int idCounter = 0;
    public final int id;
    
    public int enterpriseId;          // 所属企业
    public int industryId;            // 所属行业
    public float requiredSkill;        // 技能要求
    public float requiredEducation;   // 学历要求
    public float requiredExperience;  // 经验要求
    public float wage;                // 工资
    public int applicants;            // 申请人数
    public boolean filled;            // 是否已招满
    
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
```

- [ ] **Step 5: 提交代码**

```bash
cd job-seeker-strategy && git add . && git commit -m "feat: 数据模型定义 - EraData, Person, Enterprise, JobPost"
```

---

## Phase 2: 核心系统实现

### Task 3: 游戏状态管理器

**Files:**
- Create: `src/main/java/com/jobseeker/game/manager/GameStateManager.java`

- [ ] **Step 1: 创建游戏状态管理器**

```java
// GameStateManager.java
package com.jobseeker.game.manager;

import com.jobseeker.game.config.GameConfig;
import com.jobseeker.game.config.GameConfig.Era;
import com.jobseeker.game.data.EraData;

public class GameStateManager {
    private static GameStateManager instance;
    
    private Era currentEra;
    private EraData currentEraData;
    private float techLevel;           // 科技进步 0-1
    private float gdp;                 // 国内生产总值
    private float gdpPerCapita;        // 人均GDP
    private float inflationRate;       // 通胀率
    private float happinessIndex;      // 幸福指数 0-100
    private float giniCoefficient;     // 基尼系数 0-1
    
    // 难度设置
    private Difficulty difficulty;
    
    public static void init() {
        instance = new GameStateManager();
        instance.currentEra = Era.PRIMITIVE_LABOR;
        instance.currentEraData = EraData.getInitialEraData();
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
        // 计算人均GDP
        int totalPopulation = PopulationManager.getTotalPopulation();
        gdpPerCapita = totalPopulation > 0 ? gdp / totalPopulation : 0;
        
        // 更新幸福指数
        updateHappiness();
        
        // 检查时代演进
        checkEraTransition();
    }
    
    private void updateHappiness() {
        // 幸福指数受多因素影响
        float employmentEffect = (1 - EmploymentManager.getUnemploymentRate()) * 20;
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
        // 触发时代演进事件
        EventManager.triggerEraTransitionEvent(newEra);
    }
    
    // Getters
    public Era getCurrentEra() { return currentEra; }
    public float getTechLevel() { return techLevel; }
    public float getGdp() { return gdp; }
    public float getGdpPerCapita() { return gdpPerCapita; }
    public float getInflationRate() { return inflationRate; }
    public float getHappinessIndex() { return happinessIndex; }
    public float getGiniCoefficient() { return giniCoefficient; }
    
    // Setters
    public void setTechLevel(float level) { this.techLevel = level; }
    public void addGdp(float amount) { this.gdp += amount; }
    public void setInflationRate(float rate) { this.inflationRate = rate; }
    public void setGiniCoefficient(float coef) { this.giniCoefficient = coef; }
    
    public enum Difficulty {
        HARMONY,  // 和谐模式
        NORMAL,   // 普通模式
        CHALLENGE // 挑战模式
    }
}
```

- [ ] **Step 2: 提交代码**

```bash
cd job-seeker-strategy && git add . && git commit -m "feat: GameStateManager - 游戏状态管理，时代演进"
```

---

### Task 4: 人口系统

**Files:**
- Create: `src/main/java/com/jobseeker/game/manager/PopulationManager.java`

- [ ] **Step 1: 创建人口管理器**

```java
// PopulationManager.java
package com.jobseeker.game.manager;

import com.jobseeker.game.config.GameConfig;
import com.jobseeker.game.entity.Person;
import com.jobseeker.game.entity.Person.PersonState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PopulationManager {
    private static PopulationManager instance;
    
    private List<Person> allPersons;
    private int totalPopulation;
    private float birthRate;
    private float deathRate;
    private float naturalGrowthRate;
    private float netMigrationRate;
    
    // 人口统计
    private int childCount;
    private int laborCount;       // 劳动人口(15-59)
    private int elderlyCount;     // 老年人口(60+)
    private int fertilityWomen;   // 育龄女性数量
    
    private Random random = new Random();
    
    public static void init() {
        instance = new PopulationManager();
        instance.allPersons = new ArrayList<>();
        instance.totalPopulation = GameConfig.INITIAL_POPULATION;
        instance.birthRate = GameConfig.BIRTH_RATE_BASE;
        instance.deathRate = GameConfig.DEATH_RATE_BASE;
        
        // 初始化人口
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
        // 每年更新人口
        // 简化：每12个月为一年
        int currentMonth = Game.getInstance().getCurrentMonth();
        if (currentMonth != 1) return;  // 只在每年1月更新人口
        
        // 人口增长
        applyBirth();
        applyDeath();
        applyAging();
        applyMigration();
        
        updateStatistics();
    }
    
    private void applyBirth() {
        // 生育意愿计算
        float birthWillingness = calculateBirthWillingness();
        
        // 实际出生人数 = 育龄女性 × 生育率 × 政策乘数
        int actualBirths = (int)(fertilityWomen * birthWillingness * 0.1f);
        
        for (int i = 0; i < actualBirths; i++) {
            Person baby = new Person();
            baby.age = 0;
            baby.state = PersonState.CHILD;
            baby.education = 0;
            baby.skill = 0;
            baby.experience = 0;
            allPersons.add(baby);
        }
        
        totalPopulation += actualBirths;
    }
    
    private float calculateBirthWillingness() {
        float base = 1.0f;
        
        // 幸福指数修正
        float happinessEffect = (GameStateManager.getInstance().getHappinessIndex() - 50) * 0.02f;
        
        // 时代文化修正
        float eraEffect = switch (GameStateManager.getInstance().getCurrentEra()) {
            case PRIMITIVE_LABOR -> 0.3f;
            case HANDCRAFT -> 0.2f;
            case STEAM_REVOLUTION -> 0.0f;
            case ELECTRICAL -> -0.1f;
            case INFORMATION -> -0.2f;
            case SMART_ERA -> -0.3f;
        };
        
        // 政策修正（从PolicyManager获取）
        float policyEffect = PolicyManager.getInstance().getBirthPolicyEffect();
        
        // 经济压力修正
        float economicPressure = -FinanceManager.getInstance().getAverageLivingCost() / 1000f;
        
        return Math.max(0.1f, Math.min(2.0f, base + happinessEffect + eraEffect + policyEffect + economicPressure));
    }
    
    private void applyDeath() {
        // 死亡率基于基础死亡率 + 年龄 + 健康状况
        int deaths = (int)(totalPopulation * deathRate);
        
        List<Person> toRemove = new ArrayList<>();
        for (Person person : allPersons) {
            if (person.state == PersonState.DECEASED) {
                toRemove.add(person);
                continue;
            }
            
            // 老年死亡率高
            if (person.age >= 60) {
                if (random.nextFloat() < 0.05f) {  // 5%年度死亡率
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
        // 迁入/迁出计算
        float netMigration = calculateNetMigration();
        int migrationCount = (int)(totalPopulation * Math.abs(netMigration));
        
        if (netMigration > 0) {
            // 迁入：添加高技能人才
            for (int i = 0; i < migrationCount; i++) {
                Person immigrant = createImmigrant();
                allPersons.add(immigrant);
            }
        } else if (netMigration < 0) {
            // 迁出：移除年轻人和高学历者
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
        
        // 幸福指数影响
        float happiness = GameStateManager.getInstance().getHappinessIndex();
        if (happiness < 30) return -0.02f;
        if (happiness > 70) return 0.01f;
        
        // 失业率影响
        float unemployment = EmploymentManager.getUnemploymentRate();
        if (unemployment > 0.25f) return -0.015f;
        
        return base;
    }
    
    private Person createImmigrant() {
        Person immigrant = new Person();
        immigrant.education = 30 + random.nextInt(40);  // 较高学历
        immigrant.skill = 30 + random.nextInt(40);
        return immigrant;
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
}
```

- [ ] **Step 2: 提交代码**

```bash
cd job-seeker-strategy && git add . && git commit -m "feat: PopulationManager - 人口系统，生育意愿，人口迁移"
```

---

### Task 5: 财政系统

**Files:**
- Create: `src/main/java/com/jobseeker/game/manager/FinanceManager.java`

- [ ] **Step 1: 创建财政管理器**

```java
// FinanceManager.java
package com.jobseeker.game.manager;

import com.jobseeker.game.entity.Person;
import com.jobseeker.game.entity.Person.PersonState;

import java.util.List;

public class FinanceManager {
    private static FinanceManager instance;
    
    // 财政收入
    private float governmentRevenue;      // 政府月度收入
    private float governmentExpenditure;  // 政府月度支出
    private float fiscalBalance;         // 财政盈余/赤字
    
    // 国债
    private float nationalDebt;           // 国债总额
    private float debtInterestRate;      // 国债利率
    private float debtInterest;          // 年度债务利息
    
    // 税收配置（从PolicyManager获取）
    private float personalIncomeTaxRate;
    private float corporateTaxRate;
    private float valueAddedTaxRate;
    
    // 公共服务定价
    private float educationFeeLevel;      // 教育学费水平
    private float transportationFeeLevel; // 交通票价水平
    private float healthcareFeeLevel;     // 医疗费用水平
    
    public static void init() {
        instance = new FinanceManager();
        instance.nationalDebt = 0f;
        instance.debtInterestRate = 0.03f;
        instance.personalIncomeTaxRate = 0.15f;
        instance.corporateTaxRate = 0.20f;
        instance.valueAddedTaxRate = 0.10f;
        instance.educationFeeLevel = 0.5f;
        instance.transportationFeeLevel = 0.3f;
        instance.healthcareFeeLevel = 0.4f;
    }
    
    public static FinanceManager getInstance() {
        return instance;
    }
    
    public void update() {
        calculateMonthlyRevenue();
        calculateMonthlyExpenditure();
        fiscalBalance = governmentRevenue - governmentExpenditure;
        updateNationalDebt();
    }
    
    private void calculateMonthlyRevenue() {
        governmentRevenue = 0f;
        
        // 个人所得税
        governmentRevenue += calculatePersonalIncomeTax();
        
        // 企业所得税
        governmentRevenue += calculateCorporateTax();
        
        // 增值税
        governmentRevenue += calculateVAT();
        
        // 公共服务收费
        governmentRevenue += calculateServiceFees();
    }
    
    private float calculatePersonalIncomeTax() {
        float totalIncome = 0f;
        List<Person> laborForce = PopulationManager.getInstance().getLaborForce();
        
        for (Person person : laborForce) {
            if (person.state == PersonState.EMPLOYED) {
                totalIncome += person.currentWage;
            }
        }
        
        // 简化累进税制
        return totalIncome * personalIncomeTaxRate;
    }
    
    private float calculateCorporateTax() {
        float totalCorporateProfit = 0f;
        List<Enterprise> enterprises = EnterpriseManager.getInstance().getAllEnterprises();
        
        for (Enterprise enterprise : enterprises) {
            if (enterprise.state != Enterprise.EnterpriseState.BANKRUPT) {
                totalCorporateProfit += Math.max(0, enterprise.profit);
            }
        }
        
        return totalCorporateProfit * corporateTaxRate;
    }
    
    private float calculateVAT() {
        // 基于GDP的简化增值税
        float gdp = GameStateManager.getInstance().getGdp();
        return gdp * 0.001f * valueAddedTaxRate;  // 月度VAT
    }
    
    private float calculateServiceFees() {
        // 教育收费
        float educationRevenue = PopulationManager.getInstance().getChildCount() 
                                 * educationFeeLevel * 10f;
        
        // 交通收费（简化）
        float transportRevenue = PopulationManager.getInstance().getLaborCount() 
                                * transportationFeeLevel * 5f;
        
        // 医疗收费
        float healthcareRevenue = PopulationManager.getInstance().getTotalPopulation() 
                                  * healthcareFeeLevel * 2f;
        
        return educationRevenue + transportRevenue + healthcareRevenue;
    }
    
    private void calculateMonthlyExpenditure() {
        governmentExpenditure = 0f;
        
        // 教育支出
        governmentExpenditure += calculateEducationExpenditure();
        
        // 医疗支出
        governmentExpenditure += calculateHealthcareExpenditure();
        
        // 社会保障支出
        governmentExpenditure += calculateSocialSecurityExpenditure();
        
        // 基础设施维护
        governmentExpenditure += calculateInfrastructureExpenditure();
        
        // 行政支出
        governmentExpenditure += calculateAdministrationExpenditure();
        
        // 债务利息
        governmentExpenditure += nationalDebt * debtInterestRate / 12f;
    }
    
    private float calculateEducationExpenditure() {
        int studentCount = PopulationManager.getInstance().getChildCount() / 2;
        return studentCount * 50f;  // 每个学生50元/月
    }
    
    private float calculateHealthcareExpenditure() {
        int population = PopulationManager.getInstance().getTotalPopulation();
        return population * 10f;  // 人均10元/月
    }
    
    private float calculateSocialSecurityExpenditure() {
        int elderlyCount = PopulationManager.getInstance().getElderlyCount();
        return elderlyCount * 100f;  // 每人100元/月养老金
    }
    
    private float calculateInfrastructureExpenditure() {
        return 5000f;  // 固定基础建设维护费
    }
    
    private float calculateAdministrationExpenditure() {
        int population = PopulationManager.getInstance().getTotalPopulation();
        return population * 2f;  // 人均2元/月行政费
    }
    
    private void updateNationalDebt() {
        if (fiscalBalance < 0) {
            nationalDebt -= fiscalBalance;  // 赤字增加债务
        } else if (nationalDebt > 0 && fiscalBalance > 0) {
            nationalDebt = Math.max(0, nationalDebt - fiscalBalance * 0.1f);  // 盈余还债
        }
        
        // 债务/GDP比率影响利率
        float debtToGdp = nationalDebt / GameStateManager.getInstance().getGdp();
        if (debtToGdp > 1.5f) {
            debtInterestRate = 0.08f;  // 高风险利率
        } else if (debtToGdp > 1.0f) {
            debtInterestRate = 0.06f;
        } else if (debtToGdp > 0.6f) {
            debtInterestRate = 0.04f;
        } else {
            debtInterestRate = 0.03f;
        }
    }
    
    // 政策效果
    public void setPersonalIncomeTaxRate(float rate) {
        this.personalIncomeTaxRate = rate;
    }
    
    public void setCorporateTaxRate(float rate) {
        this.corporateTaxRate = rate;
    }
    
    public void setEducationFeeLevel(float level) {
        this.educationFeeLevel = level;
    }
    
    public void setTransportationFeeLevel(float level) {
        this.transportationFeeLevel = level;
    }
    
    public void setHealthcareFeeLevel(float level) {
        this.healthcareFeeLevel = level;
    }
    
    public void issueGovernmentBonds(float amount) {
        nationalDebt += amount;
        GameStateManager.getInstance().addGdp(amount * 0.9f);  // 发债刺激经济
    }
    
    // Getters
    public float getGovernmentRevenue() { return governmentRevenue; }
    public float getGovernmentExpenditure() { return governmentExpenditure; }
    public float getFiscalBalance() { return fiscalBalance; }
    public float getNationalDebt() { return nationalDebt; }
    public float getDebtToGdpRatio() {
        float gdp = GameStateManager.getInstance().getGdp();
        return gdp > 0 ? nationalDebt / gdp : 0;
    }
    
    public float getAverageLivingCost() {
        // 简化的平均生活成本
        return educationFeeLevel * 50 + transportationFeeLevel * 30 + healthcareFeeLevel * 20;
    }
}
```

- [ ] **Step 2: 提交代码**

```bash
cd job-seeker-strategy && git add . && git commit -m "feat: FinanceManager - 财政系统，税收支出，国债管理"
```

---

### Task 6: 企业系统

**Files:**
- Create: `src/main/java/com/jobseeker/game/manager/EnterpriseManager.java`

- [ ] **Step 1: 创建企业管理器**

```java
// EnterpriseManager.java
package com.jobseeker.game.manager;

import com.jobseeker.game.config.GameConfig;
import com.jobseeker.game.entity.Enterprise;
import com.jobseeker.game.entity.Enterprise.EnterpriseState;
import com.jobseeker.game.entity.JobPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnterpriseManager {
    private static EnterpriseManager instance;
    
    private List<Enterprise> allEnterprises;
    private int nextEnterpriseId = 0;
    private Random random = new Random();
    
    public static void init() {
        instance = new EnterpriseManager();
        instance.allEnterprises = new ArrayList<>();
        
        // 初始化各行业企业
        for (int i = 0; i < 7; i++) {
            int initialCount = getInitialEnterpriseCount(i);
            for (int j = 0; j < initialCount; j++) {
                instance.createEnterprise(i, 0);  // 初始在区域0
            }
        }
    }
    
    private int getInitialEnterpriseCount(int industryId) {
        // 根据行业和时代返回初始企业数
        return switch (industryId) {
            case 0 -> 50;   // 农业
            case 1, 2 -> 15; // 制造业、商业
            case 3, 4 -> 10; // 交通、服务
            case 5, 6 -> 5;  // 科技、公共
            default -> 10;
        };
    }
    
    public static EnterpriseManager getInstance() {
        return instance;
    }
    
    public void update() {
        // 企业年度更新
        for (Enterprise enterprise : allEnterprises) {
            if (enterprise.state == EnterpriseState.BANKRUPT) continue;
            
            updateEnterpriseProfit(enterprise);
            updateEnterpriseState(enterprise);
            
            // 可能的扩张或收缩
            if (enterprise.state == EnterpriseState.GROWING && enterprise.profit > 5000) {
                enterprise.maxEmployeeCount += 5;
            } else if (enterprise.state == EnterpriseState.DECLINE && enterprise.profit < -1000) {
                enterprise.maxEmployeeCount = Math.max(1, enterprise.maxEmployeeCount - 5);
            }
        }
        
        // 可能有新企业创建
        maybeCreateNewEnterprise();
    }
    
    private void updateEnterpriseProfit(Enterprise enterprise) {
        // 简化：企业利润 = 员工产出 - 工资成本
        float employeeOutput = enterprise.employeeCount * enterprise.productivity;
        float wageCost = enterprise.employeeCount * enterprise.wageOffered;
        
        enterprise.profit += employeeOutput - wageCost;
        
        // 添加随机波动
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
        // 经济好时有新企业创建
        float gdpGrowth = GameStateManager.getInstance().getGdpPerCapita();
        if (gdpGrowth > 200 && random.nextFloat() < 0.05f) {
            int industry = random.nextInt(7);
            createEnterprise(industry, random.nextInt(4));
        }
    }
    
    public Enterprise createEnterprise(int industryId, int regionId) {
        Enterprise enterprise = new Enterprise(industryId, regionId);
        enterprise.productivity = getProductivityForEra(industryId);
        allEnterprises.add(enterprise);
        return enterprise;
    }
    
    private float getProductivityForEra(int industryId) {
        float base = switch (industryId) {
            case 0 -> 50f;    // 农业
            case 1 -> 40f;     // 制造业
            case 2 -> 45f;     // 商业
            case 3 -> 35f;     // 交通
            case 4 -> 40f;     // 服务
            case 5 -> 60f;     // 科技
            case 6 -> 30f;     // 公共
            default -> 40f;
        };
        
        // 根据时代调整
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
}
```

- [ ] **Step 2: 提交代码**

```bash
cd job-seeker-strategy && git add . && git commit -m "feat: EnterpriseManager - 企业系统，行业演进，盈利/倒闭"
```

---

### Task 7: 就业匹配系统

**Files:**
- Create: `src/main/java/com/jobseeker/game/manager/EmploymentManager.java`
- Create: `src/main/java/com/jobseeker/game/manager/PolicyManager.java`

- [ ] **Step 1: 创建政策管理器**

```java
// PolicyManager.java
package com.jobseeker.game.manager;

public class PolicyManager {
    private static PolicyManager instance;
    
    // 人口政策
    private float birthSubsidy = 0f;           // 生育补贴
    private int birthPolicyMode = 0;           // 0=无限制, 1=鼓励, -1=限制
    private boolean oneChildPolicy = false;    // 独生子女政策
    
    // 就业政策
    private float minimumWage = 20f;           // 最低工资
    private float employmentSubsidy = 0f;      // 就业促进补贴
    
    // 科技政策
    private float rdSubsidy = 0f;              // 研发补贴
    
    // 社会福利
    private float pensionLevel = 100f;         // 养老金水平
    private float unemploymentBenefit = 50f;   // 失业救济金
    
    public static void init() {
        instance = new PolicyManager();
    }
    
    public static PolicyManager getInstance() {
        return instance;
    }
    
    // 人口政策效果
    public float getBirthPolicyEffect() {
        float effect = 0f;
        
        if (birthPolicyMode == 1) {  // 鼓励生育
            effect += 0.3f;
            effect += birthSubsidy / 1000f;
        } else if (birthPolicyMode == -1) {  // 限制生育
            effect -= 0.5f;
        }
        
        if (oneChildPolicy) {
            effect -= 0.3f;
        }
        
        return effect;
    }
    
    // 政策设置
    public void setBirthSubsidy(float amount) { this.birthSubsidy = amount; }
    public void setBirthPolicyMode(int mode) { this.birthPolicyMode = mode; }
    public void setOneChildPolicy(boolean enabled) { this.oneChildPolicy = enabled; }
    public void setMinimumWage(float wage) { this.minimumWage = wage; }
    public void setRdSubsidy(float subsidy) { this.rdSubsidy = subsidy; }
    public void setPensionLevel(float level) { this.pensionLevel = level; }
    public void setUnemploymentBenefit(float benefit) { this.unemploymentBenefit = benefit; }
    
    // Getters
    public float getBirthSubsidy() { return birthSubsidy; }
    public int getBirthPolicyMode() { return birthPolicyMode; }
    public boolean isOneChildPolicy() { return oneChildPolicy; }
    public float getMinimumWage() { return minimumWage; }
    public float getRdSubsidy() { return rdSubsidy; }
    public float getPensionLevel() { return pensionLevel; }
    public float getUnemploymentBenefit() { return unemploymentBenefit; }
}
```

- [ ] **Step 2: 创建就业管理器**

```java
// EmploymentManager.java
package com.jobseeker.game.manager;

import com.jobseeker.game.entity.Enterprise;
import com.jobseeker.game.entity.JobPost;
import com.jobseeker.game.entity.Person;
import com.jobseeker.game.entity.Person.PersonState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EmploymentManager {
    private static EmploymentManager instance;
    
    private List<Person> unemployedPersons;
    private List<JobPost> availableJobs;
    private Random random = new Random();
    
    private float currentUnemploymentRate;
    
    public static void init() {
        instance = new EmploymentManager();
        instance.unemployedPersons = new ArrayList<>();
        instance.availableJobs = new ArrayList<>();
    }
    
    public static EmploymentManager getInstance() {
        return instance;
    }
    
    public void update() {
        // 收集失业人口
        collectUnemployedPersons();
        
        // 收集招聘岗位
        collectAvailableJobs();
        
        // 执行就业匹配
        performMatching();
        
        // 计算失业率
        calculateUnemploymentRate();
    }
    
    private void collectUnemployedPersons() {
        unemployedPersons.clear();
        List<Person> laborForce = PopulationManager.getInstance().getLaborForce();
        
        for (Person person : laborForce) {
            if (person.state == PersonState.UNEMPLOYED) {
                unemployedPersons.add(person);
            }
        }
    }
    
    private void collectAvailableJobs() {
        availableJobs.clear();
        List<Enterprise> enterprises = EnterpriseManager.getInstance().getAllEnterprises();
        
        for (Enterprise enterprise : enterprises) {
            if (enterprise.state == Enterprise.EnterpriseState.BANKRUPT) continue;
            
            // 企业招聘
            if (enterprise.employeeCount < enterprise.maxEmployeeCount) {
                JobPost job = new JobPost(
                    enterprise.id,
                    enterprise.industryId,
                    getRequiredSkill(enterprise.industryId),
                    getRequiredEducation(enterprise.industryId),
                    enterprise.wageOffered
                );
                availableJobs.add(job);
            }
        }
    }
    
    private float getRequiredSkill(int industryId) {
        float eraTech = GameStateManager.getInstance().getTechLevel();
        float baseSkill = switch (industryId) {
            case 0 -> 10f;   // 农业
            case 1 -> 20f;   // 制造业
            case 2 -> 15f;   // 商业
            case 3 -> 15f;   // 交通
            case 4 -> 25f;   // 服务
            case 5 -> 40f;   // 科技
            case 6 -> 20f;   // 公共
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
    
    private void performMatching() {
        for (Person person : unemployedPersons) {
            if (availableJobs.isEmpty()) break;
            
            // 找到最匹配的岗位
            JobPost bestJob = findBestMatch(person);
            if (bestJob != null && calculateEmploymentProbability(person, bestJob) > 0.5f) {
                // 就业成功
                person.state = PersonState.EMPLOYED;
                person.industryId = bestJob.industryId;
                person.currentWage = bestJob.wage;
                
                // 更新企业
                Enterprise enterprise = findEnterprise(bestJob.enterpriseId);
                if (enterprise != null) {
                    enterprise.hirePerson();
                }
                
                availableJobs.remove(bestJob);
            }
        }
    }
    
    private JobPost findBestMatch(Person person) {
        JobPost best = null;
        float bestScore = -1;
        
        for (JobPost job : availableJobs) {
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
        // 就业概率公式
        float baseMatch = calculateMatchScore(person, job);
        
        // 运气系数
        float luckFactor = person.luck / 100f;
        
        // 政策加成
        float policyBonus = PolicyManager.getInstance().getEmploymentSubsidyBonus();
        
        // 行业景气
        float industryBonus = getIndustryProsperityBonus(job.industryId);
        
        float probability = baseMatch * (1 + luckFactor * 0.2f + policyBonus + industryBonus);
        return Math.max(0.1f, Math.min(0.9f, probability));
    }
    
    private float getIndustryProsperityBonus(int industryId) {
        // 简化：各行业景气度随机波动
        float base = 0f;
        long seed = industryId + Game.getInstance().getCurrentYear();
        return (float) (Math.sin(seed) * 0.1f);
    }
    
    private Enterprise findEnterprise(int enterpriseId) {
        List<Enterprise> enterprises = EnterpriseManager.getInstance().getAllEnterprises();
        for (Enterprise e : enterprises) {
            if (e.id == enterpriseId) return e;
        }
        return null;
    }
    
    private void calculateUnemploymentRate() {
        int laborCount = PopulationManager.getInstance().getLaborCount();
        int unemployedCount = unemployedPersons.size();
        
        currentUnemploymentRate = laborCount > 0 ? (float) unemployedCount / laborCount : 0;
    }
    
    public float getUnemploymentRate() {
        return currentUnemploymentRate;
    }
    
    public int getUnemployedCount() {
        return unemployedPersons.size();
    }
}
```

- [ ] **Step 3: 提交代码**

```bash
cd job-seeker-strategy && git add . && git commit -m "feat: EmploymentManager, PolicyManager - 就业匹配系统，政策系统"
```

---

### Task 8: 事件系统

**Files:**
- Create: `src/main/java/com/jobseeker/game/manager/EventManager.java`

- [ ] **Step 1: 创建事件管理器**

```java
// EventManager.java
package com.jobseeker.game.manager;

import com.jobseeker.game.config.GameConfig.Era;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EventManager {
    private static EventManager instance;
    
    private List<GameEvent> eventQueue;
    private List<GameEvent> activeEvents;
    private Random random = new Random();
    
    public static void init() {
        instance = new EventManager();
        instance.eventQueue = new ArrayList<>();
        instance.activeEvents = new ArrayList<>();
    }
    
    public static EventManager getInstance() {
        return instance;
    }
    
    public void update() {
        // 检查触发条件
        checkEventTriggers();
        
        // 处理活跃事件
        processActiveEvents();
    }
    
    private void checkEventTriggers() {
        // 财政危机检查
        FinanceManager finance = FinanceManager.getInstance();
        if (finance.getDebtToGdpRatio() > 1.5f) {
            triggerEvent(new GameEvent("主权债务危机", "国债/GDP超过150%，经济崩溃风险！", EventType.CRISIS));
        }
        
        // 高失业率检查
        if (EmploymentManager.getInstance().getUnemploymentRate() > 0.3f) {
            triggerEvent(new GameEvent("高失业危机", "失业率超过30%，社会动荡风险！", EventType.CRISIS));
        }
        
        // 低生育率检查
        // ... 人口事件触发
        
        // 随机事件
        if (random.nextFloat() < 0.01f) {  // 1%概率
            triggerRandomEvent();
        }
    }
    
    private void triggerRandomEvent() {
        GameEvent event = switch (random.nextInt(6)) {
            case 0 -> new GameEvent("技术突破", "一项新技术发明提升了生产效率！", EventType.TECH_BREAKTHROUGH);
            case 1 -> new GameEvent("经济繁荣", "市场需求旺盛，企业利润上升！", EventType.ECONOMIC_BOOM);
            case 2 -> new GameEvent("移民潮", "大量移民涌入，带来劳动力和人才！", EventType.MIGRATION);
            case 3 -> new GameEvent("自然灾害", "一场自然灾害影响了生产！", EventType.NATURAL_DISASTER);
            case 4 -> new GameEvent("疫情爆发", "疾病传播，医疗系统压力增大！", EventType.PANDEMIC);
            default -> new GameEvent("政策成功", "一项政策取得了显著成效！", EventType.POLICY_SUCCESS);
        };
        triggerEvent(event);
    }
    
    private void triggerEvent(GameEvent event) {
        activeEvents.add(event);
        applyEventEffects(event);
    }
    
    private void applyEventEffects(GameEvent event) {
        switch (event.type) {
            case CRISIS -> applyCrisisEffects(event);
            case TECH_BREAKTHROUGH -> applyTechBreakthrough(event);
            case ECONOMIC_BOOM -> applyEconomicBoom(event);
            case MIGRATION -> applyMigrationEvent(event);
            case NATURAL_DISASTER -> applyNaturalDisaster(event);
            case PANDEMIC -> applyPandemic(event);
            case POLICY_SUCCESS -> applyPolicySuccess(event);
            case ERA_TRANSITION -> {}  // 特殊处理
        }
    }
    
    private void applyCrisisEffects(GameEvent event) {
        if (event.name.contains("债务")) {
            GameStateManager.getInstance().setTechLevel(
                GameStateManager.getInstance().getTechLevel() * 0.9f);
        }
        GameStateManager.getInstance().setHappinessIndex(
            GameStateManager.getInstance().getHappinessIndex() - 10);
    }
    
    private void applyTechBreakthrough(GameEvent event) {
        GameStateManager.getInstance().setTechLevel(
            Math.min(1.0f, GameStateManager.getInstance().getTechLevel() + 0.05f));
        GameStateManager.getInstance().setHappinessIndex(
            GameStateManager.getInstance().getHappinessIndex() + 5);
    }
    
    private void applyEconomicBoom(GameEvent event) {
        GameStateManager.getInstance().addGdp(
            GameStateManager.getInstance().getGdp() * 0.1f);
    }
    
    private void applyMigrationEvent(GameEvent event) {
        // 增加人口
        // 简化：直接调用
    }
    
    private void applyNaturalDisaster(GameEvent event) {
        GameStateManager.getInstance().addGdp(
            -GameStateManager.getInstance().getGdp() * 0.05f);
        FinanceManager.getInstance().setHealthcareFeeLevel(
            FinanceManager.getInstance().getHealthcareFeeLevel() * 1.2f);
    }
    
    private void applyPandemic(GameEvent event) {
        // 死亡率和医疗压力增加
    }
    
    private void applyPolicySuccess(GameEvent event) {
        GameStateManager.getInstance().setHappinessIndex(
            GameStateManager.getInstance().getHappinessIndex() + 8);
    }
    
    private void processActiveEvents() {
        // 事件持续一段时间后移除
        List<GameEvent> toRemove = new ArrayList<>();
        for (GameEvent event : activeEvents) {
            event.duration--;
            if (event.duration <= 0) {
                toRemove.add(event);
            }
        }
        activeEvents.removeAll(toRemove);
    }
    
    public void triggerEraTransitionEvent(Era newEra) {
        GameEvent event = new GameEvent(
            "时代演进：" + newEra.name,
            "社会进入了" + newEra.name + "！",
            EventType.ERA_TRANSITION
        );
        event.duration = 12;  // 持续1年
        triggerEvent(event);
    }
    
    public List<GameEvent> getActiveEvents() {
        return new ArrayList<>(activeEvents);
    }
    
    // 事件类
    public static class GameEvent {
        public String name;
        public String description;
        public EventType type;
        public int duration;  // 持续月数
        public long timestamp;
        
        public GameEvent(String name, String description, EventType type) {
            this.name = name;
            this.description = description;
            this.type = type;
            this.duration = 6;  // 默认持续6个月
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    public enum EventType {
        CRISIS,
        TECH_BREAKTHROUGH,
        ECONOMIC_BOOM,
        MIGRATION,
        NATURAL_DISASTER,
        PANDEMIC,
        POLICY_SUCCESS,
        ERA_TRANSITION
    }
}
```

- [ ] **Step 2: 提交代码**

```bash
cd job-seeker-strategy && git add . && git commit -m "feat: EventManager - 事件系统，危机/机遇事件"
```

---

## Phase 3: UI层（简化版）

### Task 9: 游戏UI

**Files:**
- Create: `src/main/java/com/jobseeker/game/ui/GameUI.java`
- Create: `src/main/java/com/jobseeker/game/ui/DashboardPanel.java`

- [ ] **Step 1: 创建游戏UI入口**

```java
// GameUI.java
package com.jobseeker.game.ui;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;

public class GameUI {
    private static GameUI instance;
    private AssetManager assetManager;
    private Node guiNode;
    private DashboardPanel dashboardPanel;
    
    public static void init(AssetManager assetManager, Node guiNode) {
        instance = new GameUI();
        instance.assetManager = assetManager;
        instance.guiNode = guiNode;
        
        // 初始化各面板
        instance.dashboardPanel = new DashboardPanel();
        instance.guiNode.attachChild(instance.dashboardPanel.getRootNode());
    }
    
    public static GameUI getInstance() {
        return instance;
    }
    
    public void update() {
        dashboardPanel.update();
    }
    
    public DashboardPanel getDashboardPanel() {
        return dashboardPanel;
    }
}
```

- [ ] **Step 2: 创建仪表盘面板**

```java
// DashboardPanel.java
package com.jobseeker.game.ui;

import com.jme3.scene.Node;
import com.jme3.scene.layout.BorderLayout;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jobseeker.game.manager.*;

public class DashboardPanel {
    private Node rootNode;
    private BitmapText yearText;
    private BitmapText populationText;
    private BitmapText gdpText;
    private BitmapText unemploymentText;
    private BitmapText happinessText;
    private BitmapText techLevelText;
    
    public DashboardPanel() {
        rootNode = new Node("Dashboard");
        rootNode.setLocalTranslation(0, 720, 0);  // 左上角
        
        createBackground();
        createTexts();
    }
    
    private void createBackground() {
        Geometry bg = new Geometry("DashboardBG", new Quad(400, 150));
        Material mat = new Material();
        mat.setColor("Color", new ColorRGBA(0.1f, 0.15f, 0.2f, 0.9f));
        bg.setMaterial(mat);
        rootNode.attachChild(bg);
    }
    
    private void createTexts() {
        // 简化：创建文字标签
        // 实际项目中应使用Nifty GUI或JDK的Swing/JavaFX
    }
    
    public void update() {
        // 更新显示数据
        GameState state = GameStateManager.getInstance();
        FinanceManager finance = FinanceManager.getInstance();
        
        // 更新各文本
    }
    
    public Node getRootNode() {
        return rootNode;
    }
}
```

- [ ] **Step 2: 提交代码**

```bash
cd job-seeker-strategy && git add . && git commit -m "feat: GameUI - 基础UI框架"
```

---

## 实施检查清单

### Phase 1 完成检查
- [ ] Gradle项目配置成功
- [ ] JMonkeyEngine初始化无错误
- [ ] 游戏主循环正常运行
- [ ] 6个时代数据定义正确
- [ ] 7大行业数据定义正确
- [ ] Person实体创建正常
- [ ] Enterprise实体创建正常
- [ ] JobPost实体创建正常

### Phase 2 完成检查
- [ ] GameStateManager正常更新
- [ ] 时代演进检测正常
- [ ] PopulationManager人口统计正确
- [ ] 生育意愿计算正确
- [ ] 人口迁移计算正确
- [ ] FinanceManager税收计算正确
- [ ] 财政支出计算正确
- [ ] 国债系统正常
- [ ] EnterpriseManager企业管理正常
- [ ] 企业生命周期正常
- [ ] EmploymentManager就业匹配正常
- [ ] PolicyManager政策效果正常
- [ ] EventManager事件触发正常

### Phase 3 完成检查
- [ ] UI正常显示
- [ ] 仪表盘更新正常
- [ ] 事件通知显示正常

---

**Plan saved to:** `docs/superpowers/plans/2026-06-20-job-seeker-strategy-plan.md`
