package com.github.pavelsemenov.swaggerschemagenerator.swagger;

import com.github.pavelsemenov.swaggerschemagenerator.psi.PhpClassExtractor;
import com.github.pavelsemenov.swaggerschemagenerator.psi.PhpFieldsExtractor;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public class OpenApiFactory {
    private final PhpClassExtractor classExtractor;
    private final PhpFieldsExtractor fieldsExtractor;
    private final PhpPropertyMapper propertyMapper;

    @Inject
    public OpenApiFactory(
            PhpClassExtractor classExtractor,
            PhpFieldsExtractor fieldsExtractor,
            PhpPropertyMapper propertyMapper
    ) {
        this.classExtractor = classExtractor;
        this.fieldsExtractor = fieldsExtractor;
        this.propertyMapper = propertyMapper;
    }

    public OpenAPI create(PhpFile file) {
        Optional<PhpClass> phpClass = classExtractor.extract(file);
        Map<String, Schema> schemaMap = phpClass.map(this::createSchemas).orElse(Collections.emptyMap());
        Components components = new Components().schemas(schemaMap);

        return new OpenAPI().components(components);
    }

    private Map<String, Schema> createSchemas(PhpClass phpClass) {
        Map<String, Schema> schemaMap = new HashMap<>();
        createSchema(phpClass, schemaMap);

        return schemaMap;
    }

    private void createSchema(PhpClass phpClass, Map<String, Schema> schemaMap) {
        ObjectSchema rootSchema = new ObjectSchema();
        Collection<Field> fields = fieldsExtractor.extract(phpClass);
        Map<String, Schema> properties = fields.stream()
                .map(propertyMapper::createSchema)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparing(Schema::getName))
                .collect(Collectors.toMap(Schema::getName, s -> s, (v1, v2) -> v1, TreeMap::new));

        if (!properties.isEmpty()) {
            rootSchema.name(phpClass.getName()).properties(properties);
            schemaMap.put(phpClass.getName(), rootSchema);
            properties.values().stream().filter(f -> {
                boolean isObject = f instanceof ObjectSchema && !f.get$ref().isEmpty();
                boolean isObjectsArray = false;
                if (f instanceof ArraySchema) {
                    ArraySchema arraySchema = (ArraySchema) f;
                    isObjectsArray = arraySchema.getItems() != null && arraySchema.getItems().get$ref() != null;
                }

                return isObject || isObjectsArray;
            }).forEach(f -> {
                String FQN = f instanceof ArraySchema ? ((ArraySchema) f).getItems().getPattern() : f.getPattern();
                classExtractor.extractFromIndex(FQN).ifPresent(c -> {
                    if (!schemaMap.containsKey(c.getName())) {
                        createSchema(c, schemaMap);
                    }
                });
                f.pattern(null);
                if (f instanceof ArraySchema) {
                    ((ArraySchema) f).getItems().pattern(null);
                }
            });
        }
    }
}
