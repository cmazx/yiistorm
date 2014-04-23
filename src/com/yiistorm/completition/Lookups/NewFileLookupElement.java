package com.yiistorm.completition.lookups;

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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class NewFileLookupElement extends LookupElement {

    private String lookupString;
    private String insertString;
    private String fileName;
    private String filePath;
    private PsiElement psiElement = null;
    private Project project = null;
    private ArrayList<String> phpDocs = new ArrayList<String>();
    public String fileContent = "";
    public String createTitle = "create view file";

    @Nullable
    private InsertHandler<LookupElement> insertHandler = null;

    public NewFileLookupElement(String lookupString, String insertString, String fileName, String filePath, Project project,
                                ArrayList<String> params) {

        this.lookupString = lookupString.replaceFirst("^[/]{2}", "");
        this.insertString = insertString.replaceFirst("^[/]{2}", "");
        this.fileName = fileName;
        this.filePath = filePath;
        this.project = project;
        this.phpDocs = params;
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

    public void handleInsert(InsertionContext context) {
        File f = new File(filePath + fileName + ".php");
        this.writeNewFileHeader(f);

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
        try {
            BufferedWriter output;
            output = new BufferedWriter(new FileWriter(f));
            String text = "<?php\n/**\n *\n";
            if (this.phpDocs != null && this.phpDocs.size() > 0) {

                for (String doc : this.phpDocs) {
                    text += " * " + doc + "\n";
                }
            }
            text += " */";
            output.write(text);
            output.write(fileContent);
            output.close();
        } catch (IOException e) {
            System.out.println("File create failed");
        }
    }

    public void renderElement(LookupElementPresentation presentation) {
        presentation.setItemText(this.lookupString);
        presentation.setIcon(PlatformIcons.ADD_ICON);
        presentation.setTypeText(createTitle);
        presentation.setTailText(".php");
        presentation.setTypeGrayed(false);
    }

}
