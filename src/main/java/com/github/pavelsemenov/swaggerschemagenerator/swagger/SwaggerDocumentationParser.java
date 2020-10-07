package com.github.pavelsemenov.swaggerschemagenerator.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jetbrains.php.lang.psi.PhpFile;
import io.swagger.v3.oas.models.Components;

import javax.inject.Inject;
import java.util.Optional;

public class SwaggerDocumentationParser {
    private final SwaggerComponentsFactory componentsFactory;
    private final ObjectMapper objectMapper;

    @Inject
    SwaggerDocumentationParser(SwaggerComponentsFactory componentsFactory, ObjectMapper objectMapper) {
        this.componentsFactory = componentsFactory;
        this.objectMapper = objectMapper;
    }

    public Optional<String> parseDocumentation(PhpFile file) {
        Components components = componentsFactory.create(file);
        Optional<String> result;
        try {
            result = Optional.of(objectMapper.writeValueAsString(components));
        } catch (JsonProcessingException ex) {
            result = Optional.empty();
        }

        return result;
    }
}
