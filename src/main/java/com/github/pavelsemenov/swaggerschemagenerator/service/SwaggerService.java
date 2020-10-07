package com.github.pavelsemenov.swaggerschemagenerator.service;

import com.github.pavelsemenov.swaggerschemagenerator.ioc.DaggerApplicationComponent;
import com.github.pavelsemenov.swaggerschemagenerator.swagger.SwaggerDocumentationParser;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.jetbrains.php.lang.psi.PhpFile;

import java.util.Optional;

public class SwaggerService {
    private final SwaggerDocumentationParser componentsFactory;
    private final Project project;

    public SwaggerService(Project project) {
        componentsFactory = DaggerApplicationComponent.factory().create(project).swagger();
        this.project = project;
    }

    public void parseDocumentation(PhpFile phpFile) {
        Optional<String> swagger = componentsFactory.parseDocumentation(phpFile);
        swagger.ifPresent(s -> {
            PsiFile psiFile = PsiFileFactory.getInstance(project)
                    .createFileFromText("swagger-schema.yaml", FileTypes.PLAIN_TEXT, s);
            PsiDirectory psiDirectory = PsiDirectoryFactory.getInstance(project).createDirectory(project.getBaseDir());
            psiDirectory.add(psiFile);
            FileEditorManager.getInstance(project).openTextEditor(
                    new OpenFileDescriptor(project, psiFile.getVirtualFile()),
                    true
            );
        });
    }
}
