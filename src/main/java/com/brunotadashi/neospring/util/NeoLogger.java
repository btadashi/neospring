package com.brunotadashi.neospring.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NeoLogger {
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String WHITE = "\u001B[37m";
    public static final String RESET = "\u001B[0m";
    public static DateTimeFormatter NEODATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void showBanner() {
        System.out.println(YELLOW);
        System.out.println("____     _   _            _____            _              ____");
        System.out.println("\\ \\ \\   | \\ | |          /  ___|          (_)             \\ \\ \\");
        System.out.println(" \\ \\ \\  |  \\| | ___  ___ \\ `--. _ __  _ __ _ _ __   __ _   \\ \\ \\");
        System.out.println("  > > > | . ` |/ _ \\/ _ \\ `--. \\ '_ \\| '__| | '_ \\ / _` |   > > > NeoSpring Web Framework");
        System.out.println(" / / /  | |\\  |  __/ (_) /\\__/ / |_) | |  | | | | | (_| |  / / /  For Educational Purposes");
        System.out.println("/_/_/   \\_| \\_/\\___|\\___/\\____/| .__/|_|  |_|_| |_|\\__, | /_/_/   By Bruno Tadashi");
        System.out.println("                               | |                  __/ |");
        System.out.println("                               |_|                 |___/");
        System.out.println(RESET);
    };

    public static void log (String module, String message) {
        String date = LocalDateTime.now().format(NEODATE);

        System.out.printf(GREEN + "%15s " + YELLOW + "%-30s:" + WHITE + "%s\n" + RESET, date, module, message);
    }
}
