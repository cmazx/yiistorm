package com.yiistorm.helpers;

import java.io.File;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 17.07.13
 * Time: 23:57
 * To change this template use File | Settings | File Templates.
 */
public class CompleterHelper {
    public static ArrayList<String> searchFiles(String searchPath, String searchString) {
        File folder = new File(searchPath);
        File[] listOfFiles = folder.listFiles();

        searchString = searchString.toLowerCase();
        ArrayList<String> files = new ArrayList<String>();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && !file.getName().isEmpty()) {
                    String name = file.getName().toLowerCase();
                    if (name.startsWith(searchString) && getExtension(name).equals("php")) {
                        files.add(name);
                    }
                }
            }
        }
        return files;
    }

    public static ArrayList<String> searchFolders(String searchPath, String searchString) {
        File folder = new File(searchPath);
        File[] listOfFiles = folder.listFiles();

        searchString = searchString.toLowerCase();
        ArrayList<String> files = new ArrayList<String>();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isDirectory() && !file.getName().isEmpty()) {
                    String name = file.getName().toLowerCase();
                    if (name.startsWith(searchString) && getExtension(name).equals("php")) {
                        files.add(name);
                    }
                }
            }
        }
        return files;
    }

    private static String getExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }


 /*   public static String prepareViewPath(String controllerPath){

    }      */
}
