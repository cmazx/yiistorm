package com.yiistorm.elements.Lookups;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;


public class NewFileLookupElement extends LookupElement {

    private String fileName;
    private String filePath;
    private PsiElement psiElement = null;
    private Project project = null;

    @Nullable
    private InsertHandler<LookupElement> insertHandler = null;

    public NewFileLookupElement(String fileName, String filePath, Project project) {

        this.fileName = fileName;
        this.filePath = filePath;
        this.project = project;
    }

    public NewFileLookupElement(String fileName, String filePath, PsiElement psiElement, InsertHandler<LookupElement> insertHandler) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.insertHandler = insertHandler;
        this.psiElement = psiElement;
    }

    @NotNull
    @Override
    public String getLookupString() {
        return fileName;
    }

    @NotNull
    public Object getObject() {
        return this.psiElement != null ? this.psiElement : super.getObject();
    }

    public void handleInsert(InsertionContext context) {
        File f = new File(filePath + fileName + ".php");
        try {
            boolean newFile = f.createNewFile();
            VirtualFile base = this.project.getBaseDir();
            if (base != null) {
                String relativePath = filePath.replace(base.getPath(), "");
                VirtualFile viewsPath = base.findFileByRelativePath(relativePath);
                if (viewsPath != null) {
                    viewsPath.refresh(false, true);
                    VirtualFile migrationFile = base.findFileByRelativePath(relativePath + fileName + ".php");
                    if (migrationFile != null) {
                        OpenFileDescriptor of = new OpenFileDescriptor(this.project, migrationFile);
                        if (of.canNavigate()) {
                            of.navigate(true);
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if (this.insertHandler != null) {
            this.insertHandler.handleInsert(context, this);
        }
    }

    public void renderElement(LookupElementPresentation presentation) {
        presentation.setItemText(getLookupString());
        presentation.setIcon(PlatformIcons.ADD_ICON);
        //String base=getPsiElement().getProject().getBasePath();
        presentation.setTypeText("create new view");//VfsUtil.getRelativePath(filePath, base, '/'));
        presentation.setTypeGrayed(false);
    }

}
