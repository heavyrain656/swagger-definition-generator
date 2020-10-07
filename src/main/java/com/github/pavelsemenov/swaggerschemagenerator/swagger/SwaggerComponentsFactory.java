package com.github.pavelsemenov.swaggerschemagenerator.swagger;

import com.github.pavelsemenov.swaggerschemagenerator.psi.PhpClassExtractor;
import com.github.pavelsemenov.swaggerschemagenerator.psi.PhpFieldsExtractor;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public class SwaggerComponentsFactory {
    private final PhpClassExtractor classExtractor;
    private final PhpFieldsExtractor fieldsExtractor;
    private final PhpPropertyMapper propertyMapper;

    @Inject
    public SwaggerComponentsFactory(
            PhpClassExtractor classExtractor,
            PhpFieldsExtractor fieldsExtractor,
            PhpPropertyMapper propertyMapper
    ) {
        this.classExtractor = classExtractor;
        this.fieldsExtractor = fieldsExtractor;
        this.propertyMapper = propertyMapper;
    }

    public Components create(PhpFile file) {
        Optional<PhpClass> phpClass = classExtractor.extract(file);
        Map<String, Schema> schemaMap = phpClass.map(this::createSchemas).orElse(Collections.emptyMap());

        return new Components().schemas(schemaMap);
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
                .collect(Collectors.toMap(Schema::getName, s -> s));

        rootSchema.name(phpClass.getName()).properties(properties);
        if (!properties.isEmpty()) {
            schemaMap.put(phpClass.getName(), rootSchema);
            properties.values().stream().filter(f -> {
                boolean isObject = f instanceof ObjectSchema;
                boolean isObjectsArray = false;
                if (f instanceof ArraySchema) {
                    ArraySchema arraySchema = (ArraySchema) f;
                    isObjectsArray = arraySchema.getItems() != null && arraySchema.getItems().get$ref() != null;
                }

                return isObject || isObjectsArray;
            }).forEach(f -> {
                Optional<PhpClass> refClass = classExtractor.extractFromIndex(f.getDescription());
                refClass.ifPresent(c -> createSchema(c, schemaMap));
                f.description(null);
            });
        }
    }
}
