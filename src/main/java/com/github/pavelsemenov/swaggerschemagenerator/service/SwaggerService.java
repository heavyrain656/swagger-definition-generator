package com.github.pavelsemenov.swaggerschemagenerator.service;

import com.github.pavelsemenov.swaggerschemagenerator.ioc.DaggerApplicationComponent;
import com.github.pavelsemenov.swaggerschemagenerator.swagger.SwaggerYAMLGenerator;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.testFramework.LightVirtualFile;
import com.jetbrains.php.lang.psi.PhpFile;

import java.util.Optional;

public class SwaggerService {
    private final Project project;

    public SwaggerService(Project project) {
        this.project = project;
    }

    public void parseDocumentation(PhpFile phpFile) {
        SwaggerYAMLGenerator swaggerGenerator = DaggerApplicationComponent.factory().create(project).swagger();
        Optional<String> swagger = swaggerGenerator.generate(phpFile);
        swagger.ifPresent(s -> {
            LightVirtualFile vf = new LightVirtualFile("swagger-schema.yaml", FileTypeManager.getInstance().getStdFileType("YAML"), s);
            FileEditorManager.getInstance(project).openTextEditor(
                    new OpenFileDescriptor(project, vf),
                    true
            );
        });
    }
}
