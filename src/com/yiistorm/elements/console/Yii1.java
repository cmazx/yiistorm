package com.yiistorm.elements.console;

import com.yiistorm.elements.Yii;

/**
 *
 */
public class Yii1 extends Yii {
    public Yii1(String yiiFile) {
        super(yiiFile);
    }

    public static String getVersionMarker() {
        return "The following commands are available";
    }

    public String migrateUp() {
        return runCommand("migrate");
    }

    public String migrateDown() {
        return runCommand("migrate down 1");
    }

    public String migrateCreate(String name) {
        return runCommand("migrate create " + name);
    }

    public String migrateHistory() {
        return runCommand("migrate history");
    }

}
