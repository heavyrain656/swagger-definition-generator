package com.github.pavelsemenov.swaggerschemagenerator.swagger;

import com.github.pavelsemenov.swaggerschemagenerator.psi.PhpClassExtractor;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import io.swagger.v3.oas.models.media.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public class PhpPropertyMapper {
    private final PhpClassExtractor classExtractor;

    @Inject
    PhpPropertyMapper(PhpClassExtractor classExtractor) {
        this.classExtractor = classExtractor;
    }

    public Optional<Schema> createSchema(Field field) {
        return parseType(field, field.getType());
    }

    private Optional<Schema> parseType(Field field, PhpType type) {
        List<String> filteredTypes = type.getTypes().stream().filter(t -> !PhpType._NULL.contains(t))
                .collect(Collectors.toList());
        String firstType = filteredTypes.size() == 1 ? filteredTypes.get(0) : PhpType._NULL;

        Optional<Schema> schema = Optional.empty();

        switch (firstType) {
            case PhpType._NUMBER:
                schema = Optional.of(new NumberSchema());
                break;
            case PhpType._INTEGER:
            case PhpType._INT:
                schema = Optional.of(new IntegerSchema());
                break;
            case PhpType._FLOAT:
                schema = Optional.of(new NumberSchema().format("float"));
                break;
            case PhpType._DOUBLE:
                schema = Optional.of(new NumberSchema().format("double"));
                break;
            case PhpType._STRING:
                schema = Optional.of(new StringSchema());
                break;
            case PhpType._BOOLEAN:
            case PhpType._BOOL:
                schema = Optional.of(new BooleanSchema());
                break;
            case PhpType._CLOSURE:
            case PhpType._THROWABLE:
            case PhpType._CALLABLE:
            case PhpType._EXCEPTION:
            case PhpType._OBJECT:
            case PhpType._RESOURCE:
            case PhpType._ARRAY:
            case PhpType._NULL:
                break;
            default:
                schema = parseClass(field, firstType);
        }
        schema.ifPresent(s -> s.name(field.getName()).nullable(type.isNullable()));
        if (!schema.isPresent() && PhpType.isPluralType(firstType)) {
            schema = parseArray(field, type);
        }

        return schema;
    }

    private Optional<Schema> parseClass(Field field, String type) {
        Optional<PhpClass> refClass = classExtractor.extractFromIndex(type);

        return refClass.map(phpClass -> new ObjectSchema().name(field.getName()).$ref(phpClass.getName())
                .description(phpClass.getFQN()));
    }

    private Optional<Schema> parseArray(Field field, PhpType type) {
        PhpType single = type.unpluralize();
        Optional<Schema> itemSchema = parseType(field, single);

        return itemSchema.map(s -> new ArraySchema().items(s).name(field.getName()).nullable(type.isNullable()));
    }
}
