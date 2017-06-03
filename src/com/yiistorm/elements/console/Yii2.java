package com.yiistorm.elements.console;

import com.yiistorm.elements.Yii;

/**
 *
 */
public class Yii2 extends Yii {
    public Yii2(String yiiFile) {
        super(yiiFile);
    }

    public static String getVersionMarker() {
        return "This is Yii version 2.";
    }

    public String migrateUp() {
        return runCommand("migrate/up");
    }

    public String migrateDown() {
        return runCommand("migrate/down 1");
    }

    public String migrateCreate(String name) {
        return runCommand("migrate/create " + name);
    }

    public String migrateHistory() {
        return runCommand("migrate", "N\n");
    }
}
