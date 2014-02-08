package com.yiistorm.helpers;

import com.intellij.codeInsight.TargetElementUtilBase;
import com.intellij.ide.util.gotoByName.GotoClassModel2;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiUtilCore;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import com.jetbrains.php.lang.psi.elements.impl.ArrayCreationExpressionImpl;
import com.yiistorm.completition.providers.ViewCompletionProvider;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: mazx
 * Date: 15.02.13
 * Time: 18:55
 * To change this template use File | Settings | File Templates.
 */
public class CommonHelper {

    private static int phpChecked = 0;

    /**
     * Detect windows OS
     *
     * @return
     */
    public static boolean isWindows() {
        String OS = System.getProperty("os.name").toLowerCase();
        return (OS.indexOf("win") >= 0);

    }

    /**
     * get prepend for command
     *
     * @return
     */
    public static String getCommandPrepend() {
        if (isWindows()) {
            return "cmd /c ";
        } else {
            return "";
        }
    }

    public static boolean phpVersionCheck() {

        if (phpChecked == 0) {
            phpChecked = 2;
            try {
                Process p = Runtime.getRuntime().exec(CommonHelper.getCommandPrepend() + " php -v");
                if (p != null) {
                    String lineAll = "";
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = reader.readLine();
                    while (line != null) {
                        line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        lineAll += line + "\n";
                    }
                    if (lineAll.contains("The PHP Group")) {
                        phpChecked = 1;
                        return true;
                    }
                }
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
        return phpChecked == 1;
    }


    public static TextRange getTextRange(PsiElement element, String str) {
        Class elementClass = element.getClass();

        try {
            Method method = elementClass.getMethod("getValueRange");
            Object obj = method.invoke(element);
            return (TextRange) obj;
        } catch (Exception e) {
            return null;
        }
    }


    public static String getViewsPathFromControllerFile(PsiFile originalFile, int linkType) {

        VirtualFile vFile = originalFile.getOriginalFile().getVirtualFile();
        if (vFile != null) {
            String projectBasePath = originalFile.getProject().getBasePath();
            if (projectBasePath != null) {
                String path = vFile.getCanonicalPath();
                String basePath = projectBasePath.replace("\\", "/");
                if (basePath != null && path != null) {
                    String relativePath = path.replaceAll("(?im)/[^/]+?Controller\\.php$", "");
                    String controllerViewsParent = relativePath.replaceAll("(?im)controllers(/.+)*$", "");
                    //IF absoluteLink from modules
                    if (linkType == ViewCompletionProvider.ABSOLUTE_LINK && relativePath.contains("modules")) {
                        controllerViewsParent = relativePath.replaceAll("(?im)modules(/.+)*$", "");
                    }

                    String controllerSubCat = relativePath.replaceAll("(?im).+controllers(/)*", "");
                    return controllerViewsParent + "views/" + controllerSubCat;
                }
            }
        }
        return null;
    }

    public static String getFilePath(PsiFile originalFile) {

        VirtualFile vFile = originalFile.getOriginalFile().getVirtualFile();
        if (vFile != null) {
            String path = vFile.getCanonicalPath();
            if (path != null) {
                return path.replaceAll("(?im)[a-z0-9_]+\\.[a-z]+$", "");
            }
        }
        return "";
    }

    public static String detectPathFileType(String path) {
        if (path.matches("(?im).+?/[a-z_0-9]+?Controller.php")) {
            return "controller";
        } else if (path.matches("(?im).+?/views/.+/[a-z_0-9]+?.php")) {
            return "view";
        } else if (path.matches("(?im).+?/models/(.+/)*[a-z_0-9]+?.php")) {
            return "model";
        } else if (path.matches("(?im).+?/[a-z_0-9]+?Widget.php")) {
            return "widget";
        }
        return "unknown";
    }

    public static String getControllerName(String fullname) {
        String controllerName = fullname.replace("Controller.php", "");
        if (controllerName.length() < 1) {
            return null;
        }
        return controllerName.substring(0, 1).toLowerCase() + controllerName.substring(1);
    }

    public static String cleanCompleterSearchString(String s) {
        String searchString = s.replace("IntellijIdeaRulezzz", "");
        searchString = searchString.replace("IntellijIdeaRulezzz ", "");
        searchString = searchString.replaceAll("^['\"]{1}", "").replaceAll("['\"]{1}$", "")
                .replaceAll("(?im)\\s+", "");
        return searchString;
    }

    public static String prepareClassName(String str) {
        return rmQuotes(str).replaceAll("^\\\\", "");
    }

    public static String rmQuotes(String str) {
        return str.replaceAll("^['\"]{1}", "").replaceAll("['\"]{1}$", "");
    }

    public static String getCleanClassName(String str) {
        return rmQuotes(str).replaceAll("^.+\\\\", "").replace("\\", "");
    }

    public static String getRegexMatch(String str, String regexp, int groupNumber) {
        try {
            Pattern pattern = Pattern.compile(regexp);
            Matcher m = pattern.matcher(str);
            m.matches();
            m.groupCount();
            return m.group(groupNumber);
        } catch (Exception e) {
            return null;
        }
    }

    public static String searchModulePath(String path) {
        String protectedPath = getRegexMatch(path, "(.+)/controllers.*", 1);
        if (protectedPath != null) {
            return protectedPath;
        }

        protectedPath = getRegexMatch(path, "(.+)/views.*", 1);
        if (protectedPath != null) {
            return protectedPath;
        }

        protectedPath = getRegexMatch(path, "(.+)/components.*", 1);
        if (protectedPath != null) {
            return protectedPath;
        }

        protectedPath = getRegexMatch(path, "(.+)/models.*", 1);
        if (protectedPath != null) {
            return protectedPath;
        }

        protectedPath = getRegexMatch(path, "(.+)/modules.*", 1);
        if (protectedPath != null) {
            return protectedPath;
        }

        return null;
    }

    public static String searchCurrentProtected(String path) {
        String protectedPath = getRegexMatch(path, "(.+?)/controllers.+", 1);
        if (protectedPath != null) {
            return protectedPath;
        }

        protectedPath = getRegexMatch(path, "(.+?)/views.+", 1);
        if (protectedPath != null) {
            return protectedPath;
        }

        protectedPath = getRegexMatch(path, "(.+?)/components.+", 1);
        if (protectedPath != null) {
            return protectedPath;
        }

        protectedPath = getRegexMatch(path, "(.+?)/models.+", 1);
        if (protectedPath != null) {
            return protectedPath;
        }

        protectedPath = getRegexMatch(path, "(.+?)/modules.+", 1);
        if (protectedPath != null) {
            return protectedPath;
        }

        return null;
    }

    public static ArrayList<String> searchClasses(String regex, Project project) {
        GotoClassModel2 model = new GotoClassModel2(project);
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        ArrayList classesNamesFounded = new ArrayList<String>();
        String[] models = model.getNames(true);
        for (String clazz : models) {

            if (p.matcher(clazz).find()) {
                classesNamesFounded.add(clazz);
            }
        }
        return classesNamesFounded;
    }

    public static PsiElement getFileByClass(String actionClass, Project project) {
        if (actionClass != null) {
            List<PsiElement> elements = PsiPhpHelper.getPsiElementsFromClassName(actionClass, project);
            if (elements.size() > 0) {
                PsiElement element = elements.get(0);
                PsiElement navElement = element.getNavigationElement();
                navElement = TargetElementUtilBase.getInstance().getGotoDeclarationTarget(element, navElement);
                if (navElement instanceof Navigatable) {
                    if (((Navigatable) navElement).canNavigate()) {
                        ((Navigatable) navElement).navigate(true);
                    }
                } else if (navElement != null) {
                    int navOffset = navElement.getTextOffset();
                    VirtualFile virtualFile = PsiUtilCore.getVirtualFile(navElement);
                    if (virtualFile != null) {
                        new OpenFileDescriptor(project, virtualFile, navOffset).navigate(true);
                    }
                }
            }
        }
        return null;
    }

    public static boolean testRegex(String regex, String test) {
        Pattern myPattern = Pattern.compile(regex);
        Matcher myMatcher = myPattern.matcher(test);
        return myMatcher.find();
    }

    public static String extractFirstCaptureRegex(String regex, String text) {
        Pattern myPattern = Pattern.compile(regex);
        Matcher myMatcher = myPattern.matcher(text);
        if (myMatcher.find()) {
            return myMatcher.group(1);
        }
        return null;
    }

    /**
     * Get first part inside text with caret
     *
     * @param text
     * @return
     */
    public static String getActiveTextPart(String text) {
        if (text.contains("IntellijIdeaRulezzz")) {
            return text = text.split("IntellijIdeaRulezzz")[0].replaceAll("['\"]+", "");
        }
        return text;
    }

    public static PsiFile getPsiFile(Project project, String path) {
        return PsiFileFactory.getInstance(project)
                .createFileFromText("x", PhpFileType.INSTANCE, CharSequenceFromFile(path));
    }

    public static HashMap<String, String> parsePhpArrayConfig(Project project, String filePath) {
        PsiFile pf = CommonHelper.getPsiFile(project, filePath);
        HashMap<String, String> hashx = new HashMap<String, String>();
        if (pf != null) {
            PsiElement groupStatement = pf.getFirstChild();
            if (groupStatement != null) {
                for (PsiElement pl : groupStatement.getChildren()) {
                    if (pl.toString().equals("Return")) {
                        PsiElement[] pl2 = pl.getChildren();
                        if (pl2.length > 0 && pl2[0].toString().equals("Array creation expression")) {
                            ArrayCreationExpressionImpl ar = (ArrayCreationExpressionImpl) pl2[0];
                            if (ar.getChildren().length > 0) {
                                for (ArrayHashElement he : ar.getHashElements()) {
                                    String val = he.getValue().getText();
                                    String key = he.getKey().getText();
                                    hashx.put(CommonHelper.rmQuotes(
                                            key.substring(0, key.length() > 40 ? 40 : key.length())
                                    ),
                                            CommonHelper.rmQuotes(
                                                    val.substring(0, val.length() > 40 ? 40 : val.length()))
                                    );
                                }
                            }
                        }
                    }
                }
            }
        }
        return hashx;
    }

    private static CharSequence CharSequenceFromFile(String filename) {
        CharBuffer cbuf = null;
        try {
            FileInputStream fis = new FileInputStream(filename);
            FileChannel fc = fis.getChannel();

            // Create a read-only CharBuffer on the file
            ByteBuffer bbuf = fc.map(FileChannel.MapMode.READ_ONLY, 0,
                    (int) fc.size());

            cbuf = Charset.forName("utf-8").newDecoder().decode(bbuf);
        } catch (CharacterCodingException e) {
            // e.printStackTrace();
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
        } catch (IOException e) {
            // e.printStackTrace();
        }
        return cbuf;
    }

    public static String getRelativePath(Project project, String string) {
        return string.replace(project.getBaseDir().getCanonicalPath(), "").replace("\\", "/");
    }
}
