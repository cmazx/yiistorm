package com.yiistorm;

import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Queryable;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.PsiFile;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.apache.xmlbeans.XmlToken;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ViewsReference implements PsiReference {
    protected PsiElement element;
    protected TextRange textRange;
    protected Project project;
    protected String path;
    protected VirtualFile appDir;
    protected VirtualFile protectedPathDir;
    protected String controllerName;

    public ViewsReference(String controllerName, String path, PsiElement element, TextRange textRange, Project project,
                          VirtualFile protectedPathDir, VirtualFile appDir) {
        this.element = element;
        this.textRange = textRange;
        this.project = project;
        this.path = path;
        this.appDir = appDir;
        this.controllerName = controllerName;
        this.protectedPathDir = protectedPathDir;
    }

    @Override
    public String toString() {
        return getCanonicalText();
    }

    public PsiElement getElement() {
        return this.element;
    }

    public TextRange getRangeInElement() {
        return textRange;
    }




    public PsiElement handleElementRename(String newElementName)
            throws IncorrectOperationException {
        // TODO: Implement this method
        throw new IncorrectOperationException();
    }

    public PsiElement bindToElement(PsiElement element) throws IncorrectOperationException {
        // TODO: Implement this method
        throw new IncorrectOperationException();
    }

    public boolean isReferenceTo(PsiElement element) {
        return resolve() == element;
    }

    public Object[] getVariants() {
        // TODO: Implement this method
        return new Object[0];
    }

    public boolean isSoft() {
        return false;
    }


    @Nullable
    public PsiElement resolve() {
        String uri = path;
        String dir = "views/";
        Project project = element.getProject();
        VirtualFile targetFile;

        if (!path.matches("^[/]{2}.+")) {
            targetFile = appDir.findFileByRelativePath(controllerName + "/" + path + ".php");
        } else {
            String absolute_url = dir + path.replace("//", "") + ".php";
            targetFile = protectedPathDir.findFileByRelativePath(absolute_url);
        }

        if (targetFile != null) {
            return PsiManager.getInstance(project).findFile(targetFile);
        }
        return null;
    }

    @Override
    public String getCanonicalText() {
        return path;
    }
}
