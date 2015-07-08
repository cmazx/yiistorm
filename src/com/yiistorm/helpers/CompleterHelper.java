package com.yiistorm.helpers;

import org.apache.tools.ant.DirectoryScanner;

public class CompleterHelper {
    public static String[] searchFiles(String searchPath, String searchString) {

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(new String[]{searchString + "*.php"});

        scanner.setBasedir(searchPath);
        scanner.setCaseSensitive(false);
        scanner.scan();
        return scanner.getIncludedFiles();

    }

    public static String[] searchFolders(String searchPath, String searchString) {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setIncludes(new String[]{searchString + "*"});
        scanner.setExcludes(new String[]{searchString + "*.php"});

        scanner.setBasedir(searchPath);
        scanner.setCaseSensitive(false);
        scanner.scan();
        return scanner.getIncludedDirectories();
    }
}
