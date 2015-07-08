package com.yiistorm.elements;

import com.yiistorm.helpers.CommonHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Yiic {

    static public boolean yiicIsRunnable(String yiiFile) {

        try {

            String prependPath = CommonHelper.getCommandPrepend();

            if (yiiFile == null) {
                return false;
            }

            Process p = Runtime.getRuntime().exec(prependPath + yiiFile + " --interactive=0");

            if (p != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = reader.readLine();
                while (line != null) {
                    if (line.contains("Yii command runner")) {
                        return true;
                    }
                    line = reader.readLine();
                }
            }
            return false;
        } catch (Exception e1) {
            return false;
        }
    }
}
