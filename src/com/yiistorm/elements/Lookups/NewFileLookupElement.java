package com.yiistorm.elements.Lookups;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class NewFileLookupElement extends LookupElement {

    private String lookupString;
    private String fileName;
    private String filePath;
    private PsiElement psiElement = null;
    private Project project = null;
    private ArrayList<String> translatingParams = new ArrayList<String>();

    @Nullable
    private InsertHandler<LookupElement> insertHandler = null;

    public NewFileLookupElement(String lookupString, String fileName, String filePath, Project project, ArrayList<String> params) {

        this.lookupString = lookupString;
        this.fileName = fileName;
        this.filePath = filePath;
        this.project = project;
        this.translatingParams = params;
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
        return lookupString;
    }

    @NotNull
    public Object getObject() {
        return this.psiElement != null ? this.psiElement : super.getObject();
    }

    public void handleInsert(InsertionContext context) {
        File f = new File(filePath + fileName + ".php");

        try {
            boolean newFile = f.createNewFile();
        } catch (IOException e) {
            return;
        }


        PsiFileFactory pf = PsiFileFactory.getInstance(project);
        String text = "";
        if (this.translatingParams.size() > 0) {
            this.writeNewFileHeader(f);
        }
        VirtualFile base = this.project.getBaseDir();

        if (base != null) {
            final String relativePath = filePath.replace(base.getPath(), "");
            final Project project = this.project;

            final VirtualFile viewsPath = base.findFileByRelativePath(relativePath);

            if (viewsPath != null) {

                viewsPath.refresh(false, false, new Runnable() {
                    @Override
                    public void run() {
                        VirtualFile newCreatedFile = viewsPath.findFileByRelativePath(fileName + ".php");

                        if (newCreatedFile != null) {

                            OpenFileDescriptor of = new OpenFileDescriptor(project, newCreatedFile);
                            of.navigate(true);
                        }
                    }
                });
                if (this.insertHandler != null) {
                    this.insertHandler.handleInsert(context, this);
                }
            }
        }
    }

    private void writeNewFileHeader(File f) {
        BufferedWriter output = null;
        try {
            output = new BufferedWriter(new FileWriter(f));
            String text = "<?php\n/**\n *\n";
            for (String varname : this.translatingParams) {
                text += " * @var " + varname + "\n";
            }
            text += " */";
            output.write(text);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void renderElement(LookupElementPresentation presentation) {
        presentation.setItemText(getLookupString());
        presentation.setIcon(PlatformIcons.ADD_ICON);
        presentation.setTypeText("create new view");
        presentation.setTypeGrayed(false);
    }

}
