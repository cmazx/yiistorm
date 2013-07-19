package com.yiistorm.helpers;

import org.apache.tools.ant.DirectoryScanner;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 17.07.13
 * Time: 23:57
 * To change this template use File | Settings | File Templates.
 */
public class CompleterHelper {
    public static String[] searchFiles(String searchPath, String searchString) {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(new String[]{searchString + "*.php"});

        scanner.setBasedir(searchPath);
        scanner.setCaseSensitive(false);
        scanner.scan();
        return scanner.getIncludedFiles();
    }


 /*   public static String prepareViewPath(String controllerPath){

    }      */
}
