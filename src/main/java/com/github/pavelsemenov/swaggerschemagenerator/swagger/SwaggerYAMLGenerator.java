package com.github.pavelsemenov.swaggerschemagenerator.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jetbrains.php.lang.psi.PhpFile;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;

import javax.inject.Inject;
import java.util.Optional;

public class SwaggerYAMLGenerator {
    private final OpenApiFactory documentationFactory;

    @Inject
    public SwaggerYAMLGenerator(OpenApiFactory documentationFactory) {
        this.documentationFactory = documentationFactory;
    }

    public Optional<String> generate(PhpFile file) {
        OpenAPI swagger = documentationFactory.create(file);
        Optional<String> result;
        try {
            result = Optional.of(Yaml.mapper().writeValueAsString(swagger));
        } catch (JsonProcessingException ex) {
            result = Optional.empty();
        }

        return result;
    }
}
