package com.yiistorm.elements;

import com.yiistorm.elements.console.Yii1;
import com.yiistorm.elements.console.Yii2;
import com.yiistorm.helpers.CommonHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 05.04.13
 * Time: 19:24
 * To change this template use File | Settings | File Templates.
 */
public abstract class Yii {
    private String yiiFile;
    private String runnable_pattern = "Yii command runner";

    public static Yii getInstance(String yiiFile) {

        if (yiiFile == null) {
            return null;
        }

        String output = getConsoleDefaultOutput(yiiFile);
        if (output.contains(Yii2.getVersionMarker())) {
            return new Yii2(yiiFile);
        } else if (output.contains(Yii1.getVersionMarker())) {
            return new Yii1(yiiFile);
        }

        return null;
    }

    public Yii(String yiiFile) {
        this.yiiFile = yiiFile;
        if (yiiFile == null) {
            return;
        }

        this.yiiFile = CommonHelper.getCommandPrepend() + yiiFile;
    }

    public static String getConsoleDefaultOutput(String yiiFile) {
        String string = "";
        try {
            Process p = Runtime.getRuntime().exec(yiiFile);
            if (p != null) {
                InputStreamReader stream = new InputStreamReader(p.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    string += line + "\n";
                }
            }
        } catch (Exception e1) {
            //
        }
        return string;
    }

    public boolean isRunnable() {
        try {
            Process p = Runtime.getRuntime().exec(this.yiiFile);

            if (p != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = reader.readLine();
                while (line != null) {
                    if (line.contains(runnable_pattern) || line.contains("The following commands are available")) {
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

    protected String runCommand(String migrateCommand) {
        return runCommand(migrateCommand, null);
    }

    protected String runCommand(String migrateCommand, String output) {

        try {
            Process p = Runtime.getRuntime().exec(this.yiiFile + " " + migrateCommand + (output != null ? "" : " --interactive=0"));
            String lineAll = "";
            if (p != null) {
                OutputStream stdout = p.getOutputStream();
                if (output != null) {
                    stdout.write(output.getBytes());
                    stdout.flush();
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = reader.readLine();
                while (line != null) {
                    line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    lineAll += line + "\n";
                }
            }
            if (p != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                String line = reader.readLine();
                while (line != null) {
                    line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    lineAll += line + "\n";
                }
            }


            return lineAll;
        } catch (Exception e1) {
            return "Error! " + e1.getMessage();
        }

    }

    abstract public String migrateUp();

    abstract public String migrateDown();

    abstract public String migrateCreate(String name);

    abstract public String migrateHistory();
}
