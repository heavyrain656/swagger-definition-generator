package com.github.pavelsemenov.swaggerschemagenerator.ioc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIndex;
import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    @Provides
    PhpIndex providePhpIndex(Project project) {
        return PhpIndex.getInstance(project);
    }

    @Provides
    ObjectMapper provideObjectMapper() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return mapper;
    }
}
