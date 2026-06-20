package com.jobseeker.game.config;

public class GameConfig {
    // 时间配置
    public static final float SECONDS_PER_MONTH = 60f;
    
    // 时代定义
    public enum Era {
        PRIMITIVE_LABOR(1, "原始劳动时代", 0f, 0f),
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
        AGRICULTURE("农业", 0),
        MANUFACTURING("制造业", 1),
        COMMERCE("商业贸易", 2),
        TRANSPORTATION("交通运输", 3),
        SERVICE("服务业", 4),
        TECHNOLOGY("科技研发", 5),
        PUBLIC("公共事业", 6);
        
        public final String name;
        public final int index;
        
        Industry(String name, int index) {
            this.name = name;
            this.index = index;
        }
    }
    
    // 人口配置
    public static final int INITIAL_POPULATION = 10000;
    public static final float BIRTH_RATE_BASE = 0.02f;
    public static final float DEATH_RATE_BASE = 0.01f;
    public static final float MIGRATION_BASE_RATE = 0.001f;
    
    // 游戏难度
    public enum Difficulty {
        HARMONY,  // 和谐模式
        NORMAL,   // 普通模式
        CHALLENGE // 挑战模式
    }
}