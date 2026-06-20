package com.jobseeker.game.config;

public class GameConfig {
    public static final int INITIAL_POPULATION = 10000;
    public static final float BIRTH_RATE_BASE = 0.02f;
    public static final float DEATH_RATE_BASE = 0.01f;
    public static final float MIGRATION_BASE_RATE = 0.001f;

    public enum Era {
        PRIMITIVE_LABOR(1, "原始劳动时代", 0.0f, 0.0f),
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

    public enum Industry {
        AGRICULTURE(0, "农业"),
        MANUFACTURING(1, "制造业"),
        COMMERCE(2, "商业贸易"),
        TRANSPORTATION(3, "交通运输"),
        SERVICE(4, "服务业"),
        TECHNOLOGY(5, "科技研发"),
        PUBLIC(6, "公共事业");

        public final int id;
        public final String name;

        Industry(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public static Industry getIndustryById(int id) {
        for (Industry industry : Industry.values()) {
            if (industry.id == id) return industry;
        }
        return Industry.AGRICULTURE;
    }
}
