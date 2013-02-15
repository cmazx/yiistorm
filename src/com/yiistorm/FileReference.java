package com.yiistorm;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nullable;

public class FileReference implements PsiReference {
    protected PsiElement element;
    protected TextRange textRange;
    protected Project project;
    protected String path;
    protected VirtualFile appDir;
    protected VirtualFile protectedPathDir;
    protected VirtualFile virtualFile;

    public FileReference(VirtualFile virtualFile, String path, PsiElement element, TextRange textRange, Project project,
                         VirtualFile protectedPathDir, VirtualFile appDir) {
        this.element = element;
        this.textRange = textRange;
        this.project = project;
        this.path = path;
        this.appDir = appDir;
        this.protectedPathDir = protectedPathDir;
        this.virtualFile = virtualFile;
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
        Project project = element.getProject();
        if(this.virtualFile !=null){
            return PsiManager.getInstance(project).findFile(virtualFile);
        }
        return null;
    }

    @Override
    public String getCanonicalText() {
        return path;
    }
}
