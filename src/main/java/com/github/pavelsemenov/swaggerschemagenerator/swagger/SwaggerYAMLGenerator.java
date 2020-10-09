package com.github.pavelsemenov.swaggerschemagenerator.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jetbrains.php.lang.psi.PhpFile;
import io.swagger.v3.oas.models.OpenAPI;

import javax.inject.Inject;
import java.util.Optional;

public class SwaggerYAMLGenerator {
    private final OpenApiFactory documentationFactory;
    private final ObjectMapper objectMapper;

    @Inject
    public SwaggerYAMLGenerator(OpenApiFactory documentationFactory, ObjectMapper objectMapper) {
        this.documentationFactory = documentationFactory;
        this.objectMapper = objectMapper;
    }

    public Optional<String> generate(PhpFile file) {
        OpenAPI swagger = documentationFactory.create(file);
        Optional<String> result;
        try {
            result = Optional.of(objectMapper.writeValueAsString(swagger));
        } catch (JsonProcessingException ex) {
            result = Optional.empty();
        }

        return result;
    }
}
