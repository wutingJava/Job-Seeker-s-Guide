package com.jobseeker.game.manager;

import com.jobseeker.game.config.GameConfig.Era;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EventManager {
    private static EventManager instance;

    private List<GameEvent> activeEvents;
    private List<GameEvent> eventLog;
    private Random random = new Random();

    public static void init() {
        instance = new EventManager();
        instance.activeEvents = new ArrayList<>();
        instance.eventLog = new ArrayList<>();
    }

    public static EventManager getInstance() {
        return instance;
    }

    public void update() {
        checkEventTriggers();
        processActiveEvents();
    }

    private void checkEventTriggers() {
        FinanceManager finance = FinanceManager.getInstance();
        if (finance.getDebtToGdpRatio() > 1.5f) {
            triggerEvent(new GameEvent("主权债务危机", "国债/GDP超过150%，经济崩溃风险！", EventType.CRISIS));
        }

        if (EmploymentManager.getInstance().getUnemploymentRate() > 0.3f) {
            triggerEvent(new GameEvent("高失业危机", "失业率超过30%，社会动荡风险！", EventType.CRISIS));
        }

        if (random.nextFloat() < 0.02f) {
            triggerRandomEvent();
        }
    }

    private void triggerRandomEvent() {
        int type = random.nextInt(6);
        GameEvent event = switch (type) {
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
        for (GameEvent active : activeEvents) {
            if (active.name.equals(event.name)) return; // 避免重复
        }
        activeEvents.add(event);
        eventLog.add(event);
        applyEventEffects(event);
    }

    private void applyEventEffects(GameEvent event) {
        switch (event.type) {
            case CRISIS:
                if (event.name.contains("债务")) {
                    GameStateManager.getInstance().setTechLevel(
                        GameStateManager.getInstance().getTechLevel() * 0.9f);
                }
                GameStateManager.getInstance().setHappinessIndex(
                    GameStateManager.getInstance().getHappinessIndex() - 10);
                break;
            case TECH_BREAKTHROUGH:
                GameStateManager.getInstance().incrementTechLevel(0.05f);
                GameStateManager.getInstance().setHappinessIndex(
                    GameStateManager.getInstance().getHappinessIndex() + 5);
                break;
            case ECONOMIC_BOOM:
                GameStateManager.getInstance().addGdp(
                    GameStateManager.getInstance().getGdp() * 0.1f);
                break;
            case MIGRATION:
                // 在PopulationManager中可以触发，但这里直接增加GDP
                GameStateManager.getInstance().addGdp(5000f);
                break;
            case NATURAL_DISASTER:
                GameStateManager.getInstance().addGdp(
                    -GameStateManager.getInstance().getGdp() * 0.05f);
                break;
            case PANDEMIC:
                GameStateManager.getInstance().setHappinessIndex(
                    GameStateManager.getInstance().getHappinessIndex() - 15);
                break;
            case POLICY_SUCCESS:
                GameStateManager.getInstance().setHappinessIndex(
                    GameStateManager.getInstance().getHappinessIndex() + 8);
                break;
            case ERA_TRANSITION:
                GameStateManager.getInstance().setHappinessIndex(
                    GameStateManager.getInstance().getHappinessIndex() + 20);
                break;
        }
    }

    private void processActiveEvents() {
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
            "时代演进：" + newEra.name(),
            "社会进入了" + newEra.name() + "！",
            EventType.ERA_TRANSITION
        );
        event.duration = 12;
        triggerEvent(event);
    }

    public List<GameEvent> getActiveEvents() {
        return new ArrayList<>(activeEvents);
    }

    public List<GameEvent> getEventLog() {
        return new ArrayList<>(eventLog);
    }

    public static class GameEvent {
        public String name;
        public String description;
        public EventType type;
        public int duration;
        public long timestamp;

        public GameEvent(String name, String description, EventType type) {
            this.name = name;
            this.description = description;
            this.type = type;
            this.duration = 6;
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
