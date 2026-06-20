package com.jobseeker.game.ui;

import com.jobseeker.game.manager.*;
import com.jobseeker.game.manager.EventManager.GameEvent;
import com.jobseeker.game.Game;

import java.util.Scanner;

public class GameUI {
    public static void init() {
        // 控制台UI不需要特别初始化
    }

    public static void renderDashboard() {
        GameStateManager state = GameStateManager.getInstance();
        PopulationManager population = PopulationManager.getInstance();
        FinanceManager finance = FinanceManager.getInstance();
        EmploymentManager employment = EmploymentManager.getInstance();
        EnterpriseManager enterprise = EnterpriseManager.getInstance();

        System.out.println("\n" + "=".repeat(70));
        System.out.println(" 【求职者攻略 - 社会经济模拟器】");
        System.out.println("=".repeat(70));
        System.out.println("  时代: " + state.getCurrentEra().name());
        System.out.println("  时间: 第 " + Game.getInstance().getCurrentYear() + " 年, 第 " + Game.getInstance().getCurrentMonth() + " 月");
        System.out.println("-".repeat(70));
        System.out.println("  【人口统计】");
        System.out.println("    总人口: " + population.getTotalPopulation() + " | 劳动力: " + population.getLaborCount());
        System.out.println("    儿童: " + population.getChildCount() + " | 老年: " + population.getElderlyCount());
        System.out.println("    抚养比: " + String.format("%.2f", population.getDependencyRatio()));
        System.out.println("-".repeat(70));
        System.out.println("  【经济统计】");
        System.out.println("    GDP: " + String.format("%.0f", state.getGdp()) + " | 人均GDP: " + String.format("%.2f", state.getGdpPerCapita()));
        System.out.println("    科技水平: " + String.format("%.3f", state.getTechLevel()) + " | 幸福指数: " + String.format("%.1f", state.getHappinessIndex()));
        System.out.println("    通胀率: " + String.format("%.2f", state.getInflationRate() * 100) + "%");
        System.out.println("-".repeat(70));
        System.out.println("  【就业统计】");
        System.out.println("    失业率: " + String.format("%.2f", employment.getUnemploymentRate() * 100) + "%");
        System.out.println("    企业数量: " + enterprise.getActiveEnterpriseCount() + " 家");
        System.out.println("-".repeat(70));
        System.out.println("  【财政统计】");
        System.out.println("    政府收入: " + String.format("%.0f", finance.getGovernmentRevenue()));
        System.out.println("    政府支出: " + String.format("%.0f", finance.getGovernmentExpenditure()));
        System.out.println("    财政余额: " + String.format("%.0f", finance.getFiscalBalance()) + " | 国债: " + String.format("%.0f", finance.getNationalDebt()));
        System.out.println("    债务/GDP比率: " + String.format("%.2f", finance.getDebtToGdpRatio() * 100) + "%");
        System.out.println("-".repeat(70));
    }

    public static void renderEvents() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("  【事件日志】");
        System.out.println("=".repeat(70));

        var activeEvents = EventManager.getInstance().getActiveEvents();
        if (activeEvents.isEmpty()) {
            System.out.println("    当前无活跃事件");
        } else {
            System.out.println("    [活跃事件]");
            for (GameEvent event : activeEvents) {
                System.out.println("    - " + event.name + " (剩余" + event.duration + "月): " + event.description);
            }
        }

        var log = EventManager.getInstance().getEventLog();
        if (log.size() > activeEvents.size()) {
            System.out.println("\n    [历史事件]");
            for (GameEvent event : log) {
                if (!activeEvents.contains(event)) {
                    System.out.println("    - " + event.name + ": " + event.description);
                }
            }
        }
    }

    public static void renderPolicyMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n" + "=".repeat(70));
            System.out.println("  【政策调整】");
            System.out.println("=".repeat(70));
            System.out.println("  1. 人口与生育政策");
            System.out.println("  2. 税收政策");
            System.out.println("  3. 公共服务定价");
            System.out.println("  4. 科技与就业政策");
            System.out.println("  5. 返回主菜单");
            System.out.print("  请选择: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1": renderPopulationPolicy(scanner); break;
                case "2": renderTaxPolicy(scanner); break;
                case "3": renderPublicServicePolicy(scanner); break;
                case "4": renderTechPolicy(scanner); break;
                case "5": return;
                default: System.out.println("无效输入，请重新选择。");
            }
        }
    }

    private static void renderPopulationPolicy(Scanner scanner) {
        PolicyManager policy = PolicyManager.getInstance();
        while (true) {
            System.out.println("\n  [人口与生育政策]");
            System.out.println("    1. 设置生育补贴金额 (当前: " + String.format("%.0f", policy.getBirthSubsidy()) + ")");
            System.out.println("    2. 切换生育鼓励政策 (当前: " + getBirthPolicyName(policy.getBirthPolicyMode()) + ")");
            System.out.println("    3. 独生子女政策开关 (当前: " + (policy.isOneChildPolicy() ? "开启" : "关闭") + ")");
            System.out.println("    4. 设置最低工资标准 (当前: " + String.format("%.0f", policy.getMinimumWage()) + ")");
            System.out.println("    5. 返回政策菜单");
            System.out.print("    请选择: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    System.out.print("    输入生育补贴金额: ");
                    float subsidy = Float.parseFloat(scanner.nextLine().trim());
                    policy.setBirthSubsidy(subsidy);
                    System.out.println("    已设置！");
                    break;
                case "2":
                    int newMode = (policy.getBirthPolicyMode() + 2) % 3 - 1;  // -1, 0, 1
                    policy.setBirthPolicyMode(newMode);
                    System.out.println("    已切换至: " + getBirthPolicyName(newMode));
                    break;
                case "3":
                    policy.setOneChildPolicy(!policy.isOneChildPolicy());
                    System.out.println("    已切换！");
                    break;
                case "4":
                    System.out.print("    输入最低工资: ");
                    float wage = Float.parseFloat(scanner.nextLine().trim());
                    policy.setMinimumWage(wage);
                    System.out.println("    已设置！");
                    break;
                case "5": return;
                default: System.out.println("    无效输入，请重新选择。");
            }
        }
    }

    private static String getBirthPolicyName(int mode) {
        return switch (mode) {
            case -1 -> "限制生育";
            case 0 -> "无政策";
            case 1 -> "鼓励生育";
            default -> "未知";
        };
    }

    private static void renderTaxPolicy(Scanner scanner) {
        FinanceManager finance = FinanceManager.getInstance();
        while (true) {
            System.out.println("\n  [税收政策]");
            System.out.println("    1. 个人所得税税率 (当前: " + String.format("%.2f", finance.getPersonalIncomeTaxRate() * 100) + "%)");
            System.out.println("    2. 企业所得税税率 (当前: " + String.format("%.2f", finance.getCorporateTaxRate() * 100) + "%)");
            System.out.println("    3. 增值税税率 (当前: " + String.format("%.2f", finance.getValueAddedTaxRate() * 100) + "%)");
            System.out.println("    4. 返回政策菜单");
            System.out.print("    请选择: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    System.out.print("    输入个人所得税税率 (0-100%): ");
                    float rate1 = Float.parseFloat(scanner.nextLine().trim()) / 100f;
                    finance.setPersonalIncomeTaxRate(Math.max(0, Math.min(1f, rate1)));
                    System.out.println("    已设置！");
                    break;
                case "2":
                    System.out.print("    输入企业所得税税率 (0-100%): ");
                    float rate2 = Float.parseFloat(scanner.nextLine().trim()) / 100f;
                    finance.setCorporateTaxRate(Math.max(0, Math.min(1f, rate2)));
                    System.out.println("    已设置！");
                    break;
                case "3":
                    System.out.print("    输入增值税税率 (0-100%): ");
                    float rate3 = Float.parseFloat(scanner.nextLine().trim()) / 100f;
                    finance.setValueAddedTaxRate(Math.max(0, Math.min(1f, rate3)));
                    System.out.println("    已设置！");
                    break;
                case "4": return;
                default: System.out.println("    无效输入，请重新选择。");
            }
        }
    }

    private static void renderPublicServicePolicy(Scanner scanner) {
        FinanceManager finance = FinanceManager.getInstance();
        while (true) {
            System.out.println("\n  [公共服务定价]");
            System.out.println("    1. 教育费用水平 (当前: " + String.format("%.2f", finance.getEducationFeeLevel()) + ")");
            System.out.println("    2. 交通费用水平 (当前: " + String.format("%.2f", finance.getTransportationFeeLevel()) + ")");
            System.out.println("    3. 医疗费用水平 (当前: " + String.format("%.2f", finance.getHealthcareFeeLevel()) + ")");
            System.out.println("    4. 返回政策菜单");
            System.out.print("    请选择: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    System.out.print("    输入教育费用水平 (0-10): ");
                    float level1 = Float.parseFloat(scanner.nextLine().trim());
                    finance.setEducationFeeLevel(Math.max(0, Math.min(10, level1)));
                    System.out.println("    已设置！");
                    break;
                case "2":
                    System.out.print("    输入交通费用水平 (0-10): ");
                    float level2 = Float.parseFloat(scanner.nextLine().trim());
                    finance.setTransportationFeeLevel(Math.max(0, Math.min(10, level2)));
                    System.out.println("    已设置！");
                    break;
                case "3":
                    System.out.print("    输入医疗费用水平 (0-10): ");
                    float level3 = Float.parseFloat(scanner.nextLine().trim());
                    finance.setHealthcareFeeLevel(Math.max(0, Math.min(10, level3)));
                    System.out.println("    已设置！");
                    break;
                case "4": return;
                default: System.out.println("    无效输入，请重新选择。");
            }
        }
    }

    private static void renderTechPolicy(Scanner scanner) {
        PolicyManager policy = PolicyManager.getInstance();
        while (true) {
            System.out.println("\n  [科技与就业政策]");
            System.out.println("    1. 研发补贴 (当前: " + String.format("%.0f", policy.getRdSubsidy()) + ")");
            System.out.println("    2. 就业促进补贴 (当前: " + String.format("%.0f", policy.getEmploymentSubsidy()) + ")");
            System.out.println("    3. 养老金水平 (当前: " + String.format("%.0f", policy.getPensionLevel()) + ")");
            System.out.println("    4. 失业救济金 (当前: " + String.format("%.0f", policy.getUnemploymentBenefit()) + ")");
            System.out.println("    5. 返回政策菜单");
            System.out.print("    请选择: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    System.out.print("    输入研发补贴金额: ");
                    float rd = Float.parseFloat(scanner.nextLine().trim());
                    policy.setRdSubsidy(rd);
                    System.out.println("    已设置！");
                    break;
                case "2":
                    System.out.print("    输入就业促进补贴: ");
                    float emp = Float.parseFloat(scanner.nextLine().trim());
                    policy.setEmploymentSubsidy(emp);
                    System.out.println("    已设置！");
                    break;
                case "3":
                    System.out.print("    输入养老金水平: ");
                    float pen = Float.parseFloat(scanner.nextLine().trim());
                    policy.setPensionLevel(pen);
                    System.out.println("    已设置！");
                    break;
                case "4":
                    System.out.print("    输入失业救济金: ");
                    float unemp = Float.parseFloat(scanner.nextLine().trim());
                    policy.setUnemploymentBenefit(unemp);
                    System.out.println("    已设置！");
                    break;
                case "5": return;
                default: System.out.println("    无效输入，请重新选择。");
            }
        }
    }
}
