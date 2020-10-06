package com.github.pavelsemenov.swaggerschemagenerator.swagger;

import com.github.pavelsemenov.swaggerschemagenerator.psi.PhpClassExtractor;
import com.github.pavelsemenov.swaggerschemagenerator.psi.PhpFieldsExtractor;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
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
        Collection<Field> fields = phpClass.isPresent() ? fieldsExtractor.extract(phpClass.get()) : Collections.emptyList();
        Optional<ObjectSchema> schema = fields.isEmpty() ? Optional.empty() : createSchema(phpClass.get(), fields);
        Components components = new Components();

        return schema.isPresent() ? components.addSchemas(schema.get().getName(), schema.get()) : components;
    }

    private Optional<ObjectSchema> createSchema(PhpClass phpClass, Collection<Field> fields) {
        ObjectSchema rootSchema = new ObjectSchema();
        Map<String, Schema> properties = fields.stream().map(propertyMapper::createSchema)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(Schema::getName, s -> s));

        rootSchema.name(phpClass.getName()).properties(properties);

        return properties.isEmpty() ? Optional.empty() : Optional.of(rootSchema);
    }
}
