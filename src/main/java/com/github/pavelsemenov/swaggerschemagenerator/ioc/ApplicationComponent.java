package com.github.pavelsemenov.swaggerschemagenerator.ioc;

import com.github.pavelsemenov.swaggerschemagenerator.swagger.SwaggerDocumentationGenerator;
import com.intellij.openapi.project.Project;
import dagger.BindsInstance;
import dagger.Component;

@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {
    SwaggerDocumentationGenerator swagger();

    @Component.Factory
    interface Factory {
        ApplicationComponent create(@BindsInstance Project project);
    }
}
