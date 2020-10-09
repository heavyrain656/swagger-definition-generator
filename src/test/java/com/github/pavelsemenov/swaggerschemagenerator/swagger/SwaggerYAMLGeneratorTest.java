package com.github.pavelsemenov.swaggerschemagenerator.swagger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jetbrains.php.lang.psi.PhpFile;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SwaggerYAMLGeneratorTest {
    @Mock
    OpenApiFactory openApiFactory;
    @Mock
    ObjectMapper mapper;
    SwaggerYAMLGenerator yamlGenerator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.yamlGenerator = new SwaggerYAMLGenerator(openApiFactory, mapper);
    }

    @Test
    void testYAMLGenerated() throws Exception {
        OpenAPI openAPI = mock(OpenAPI.class);
        PhpFile file = mock(PhpFile.class);
        when(openApiFactory.create(file)).thenReturn(openAPI);
        when(mapper.writeValueAsString(openAPI)).thenReturn("My yaml");
        Optional<String> result = yamlGenerator.generate(file);
        assertThat(result).isPresent();
    }

    @Test
    void testGenerationFailed() throws Exception {
        OpenAPI openAPI = mock(OpenAPI.class);
        PhpFile file = mock(PhpFile.class);
        when(openApiFactory.create(file)).thenReturn(openAPI);
        when(mapper.writeValueAsString(openAPI)).thenThrow(JsonProcessingException.class);
        Optional<String> result = yamlGenerator.generate(file);
        assertThat(result).isEmpty();
    }
}