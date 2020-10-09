package com.github.pavelsemenov.swaggerschemagenerator.ioc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIndex;
import dagger.Module;
import dagger.Provides;
import io.swagger.v3.core.util.Yaml;

@Module
public class ApplicationModule {
    @Provides
    PhpIndex providePhpIndex(Project project) {
        return PhpIndex.getInstance(project);
    }

    @Provides
    ObjectMapper provideObjectMapper() {
        return Yaml.mapper();
    }
}
