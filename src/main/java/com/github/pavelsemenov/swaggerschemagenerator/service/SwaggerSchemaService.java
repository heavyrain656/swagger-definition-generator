package com.github.pavelsemenov.swaggerschemagenerator.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public interface SwaggerSchemaService {
    static SwaggerSchemaService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, SwaggerSchemaService.class);
    }
}
