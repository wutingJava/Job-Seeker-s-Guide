package com.jobseeker.game;

import com.jobseeker.game.ui.GameUI;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== 求职者攻略 - Job Seeker Strategy ===");
        System.out.println("一款社会经济策略模拟游戏");
        System.out.println();

        Game.createInstance();
        Game.getInstance().init();
        GameUI.init();

        Scanner scanner = new Scanner(System.in);

        while (Game.getInstance().isRunning()) {
            GameUI.renderDashboard();

            System.out.println("\n--- 操作菜单 ---");
            System.out.println("1. 推进1个月");
            System.out.println("2. 推进12个月(1年)");
            System.out.println("3. 查看事件日志");
            System.out.println("4. 调整政策");
            System.out.println("5. 退出游戏");
            System.out.print("请选择: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    Game.getInstance().tick();
                    break;
                case "2":
                    for (int i = 0; i < 12; i++) {
                        Game.getInstance().tick();
                    }
                    break;
                case "3":
                    GameUI.renderEvents();
                    break;
                case "4":
                    GameUI.renderPolicyMenu(scanner);
                    break;
                case "5":
                    Game.getInstance().setRunning(false);
                    System.out.println("游戏结束，感谢游玩！");
                    break;
                default:
                    System.out.println("无效输入，请重新选择。");
            }
        }

        scanner.close();
    }
}
