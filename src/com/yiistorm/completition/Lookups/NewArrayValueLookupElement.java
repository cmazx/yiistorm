package com.yiistorm.completition.lookups;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.ProjectCodeStyleSettingsManager;
import com.intellij.util.PlatformIcons;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.psi.PhpPsiElementFactory;
import com.jetbrains.php.lang.psi.elements.ArrayHashElement;
import com.jetbrains.php.lang.psi.elements.impl.ArrayCreationExpressionImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;


public class NewArrayValueLookupElement extends LookupElement {

    private String insertString, filePath, fileName;
    private PsiElement psiElement = null;
    private Project project = null;
    private String createTitle = "create array value";

    @Nullable
    private InsertHandler<LookupElement> insertHandler = null;

    public NewArrayValueLookupElement(String insertString, String filePath, String fileName, Project project) {

        this.insertString = insertString;
        this.filePath = filePath;
        this.fileName = fileName;
        this.project = project;
    }

    @NotNull
    @Override
    public String getLookupString() {
        return insertString;
    }

    @NotNull
    public Object getObject() {
        return this.psiElement != null ? this.psiElement : super.getObject();
    }

    public void insertIntoArrayConfig(String string, String openFilePath) {
        if (null == project.getBasePath()) {
            return;
        }
        String relpath = openFilePath.replace(project.getBasePath(), "").substring(1).replace("\\", "/");
        VirtualFile vf = project.getBaseDir().findFileByRelativePath(relpath);

        if (vf == null) {
            return;
        }
        PsiFile pf = PsiManager.getInstance(project).findFile(vf);

        String lineSeparator = " " + ProjectCodeStyleSettingsManager.getSettings(project).getLineSeparator();
        if (pf != null) {
            PsiElement groupStatement = pf.getFirstChild();
            if (groupStatement != null) {
                Document document = pf.getViewProvider().getDocument();
                if (document == null) {
                    return;
                }
                PsiDocumentManager.getInstance(project).commitDocument(pf.getViewProvider().getDocument());

                pf.getManager().reloadFromDisk(pf);
                for (PsiElement pl : groupStatement.getChildren()) {
                    if (pl.toString().equals("Return")) {
                        PsiElement[] pl2 = pl.getChildren();
                        if (pl2.length > 0 && pl2[0].toString().equals("Array creation expression")) {
                            ArrayCreationExpressionImpl ar = (ArrayCreationExpressionImpl) pl2[0];

                            ArrayHashElement p = (ArrayHashElement) PhpPsiElementFactory.createFromText(project,
                                    PhpElementTypes.HASH_ARRAY_ELEMENT,
                                    "array('" + string + "'=>'')");
                            PsiElement closingBrace = ar.getLastChild();
                            String preLast = closingBrace.getPrevSibling().toString();
                            if (!preLast.equals("Comma")) {
                                pf.getViewProvider().getDocument().insertString(
                                        closingBrace.getTextOffset(), "," + lineSeparator + p.getText());
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    public void handleInsert(InsertionContext context) {
        File f = new File(filePath + fileName + ".php");

        VirtualFile base = this.project.getBaseDir();

        if (base != null) {
            final String relativePath = filePath.replace(base.getPath(), "");
            final Project project = this.project;

            final VirtualFile viewsPath = base.findFileByRelativePath(relativePath);

            if (viewsPath != null) {
                insertIntoArrayConfig("test", f.getPath());
                if (this.insertHandler != null) {
                    this.insertHandler.handleInsert(context, this);
                }
            }
        }
    }


    public void renderElement(LookupElementPresentation presentation) {
        presentation.setItemText(insertString);
        presentation.setIcon(PlatformIcons.ADD_ICON);
        presentation.setTypeText(createTitle);
        presentation.setTailText(".php");
        presentation.setTypeGrayed(false);
    }

}
