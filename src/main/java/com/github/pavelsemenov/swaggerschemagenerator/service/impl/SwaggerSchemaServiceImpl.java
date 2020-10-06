package com.github.pavelsemenov.swaggerschemagenerator.service.impl;

import com.github.pavelsemenov.swaggerschemagenerator.ioc.DaggerApplicationComponent;
import com.github.pavelsemenov.swaggerschemagenerator.service.SwaggerSchemaService;
import com.github.pavelsemenov.swaggerschemagenerator.swagger.SwaggerComponentsFactory;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

@Service
public class SwaggerSchemaServiceImpl implements SwaggerSchemaService {
    private final SwaggerComponentsFactory componentsFactory;

    public SwaggerSchemaServiceImpl(Project project) {
        componentsFactory = DaggerApplicationComponent.factory().create(project).swagger();
    }
}
