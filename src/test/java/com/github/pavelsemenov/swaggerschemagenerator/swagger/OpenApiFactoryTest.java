package com.github.pavelsemenov.swaggerschemagenerator.swagger;

import com.github.pavelsemenov.swaggerschemagenerator.psi.PhpClassExtractor;
import com.github.pavelsemenov.swaggerschemagenerator.psi.PhpFieldsExtractor;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class OpenApiFactoryTest {
    @Mock
    PhpClassExtractor classExtractor;
    @Mock
    PhpFieldsExtractor fieldsExtractor;
    @Mock
    PhpPropertyMapper propertyMapper;
    OpenApiFactory factory;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        factory = new OpenApiFactory(classExtractor, fieldsExtractor, propertyMapper);
    }

    @Test
    void testScalarFields() {
        List<Field> fields = mockFields();
        PhpClass phpClass = mockClass(fields);
        PhpFile file = mockFile(phpClass);
        prepareFields(fields);
        OpenAPI api = factory.create(file);
        assertThat(api.getComponents().getSchemas()).isNotEmpty()
                .containsKey(phpClass.getName());
        testObjectSchema(fields, phpClass, api.getComponents().getSchemas());
    }

    @Test
    void testClassNotFound() {
        PhpFile file = mockEmptyFile();
        OpenAPI api = factory.create(file);
        assertThat(api.getComponents().getSchemas()).isEmpty();
    }

    @Test
    void testNoFieldsInClass() {
        PhpClass phpClass = mockClass(Collections.emptyList());
        PhpFile file = mockFile(phpClass);
        OpenAPI api = factory.create(file);
        assertThat(api.getComponents().getSchemas()).isEmpty();
    }

    @Test
    void testPropertyMapperFails() {
        List<Field> fields = mockFields();
        PhpClass phpClass = mockClass(fields);
        PhpFile file = mockFile(phpClass);
        fields.forEach(f -> when(propertyMapper.createSchema(f)).thenReturn(Optional.empty()));
        OpenAPI api = factory.create(file);
        assertThat(api.getComponents().getSchemas()).isEmpty();
    }

    @Test
    void testObjectRef() {
        defaultObjectRefTest(mockFields(1));
    }

    @Test
    void testNoDuplicateCalls() {
        defaultObjectRefTest(mockFields(2));
        verify(fieldsExtractor, times(2)).extract(any(PhpClass.class));
    }

    void defaultObjectRefTest(List<Field> fields) {
        List<Field> refFields = mockFields();
        PhpClass refClass = mockClass(refFields);
        doTestObjectRef(fields, refFields, refClass, f -> {
            ObjectSchema schema = mock(ObjectSchema.class);
            String name = f.getName();
            when(schema.getName()).thenReturn(name);
            String refName = refClass.getName();
            when(schema.get$ref()).thenReturn(refName);
            when(schema.getDescription()).thenReturn(refName);
            when(classExtractor.extractFromIndex(refName)).thenReturn(Optional.of(refClass));
            when(propertyMapper.createSchema(f)).thenReturn(Optional.of(schema));
        });
        verify(fieldsExtractor, times(2)).extract(any(PhpClass.class));
    }

    void doTestObjectRef(List<Field> fields, List<Field> refFields, PhpClass refClass, Consumer<Field> fieldHandler) {
        PhpClass phpClass = mockClass(fields);
        PhpFile file = mockFile(phpClass);
        fields.forEach(fieldHandler);
        prepareFields(refFields);
        OpenAPI api = factory.create(file);
        assertThat(api.getComponents().getSchemas()).isNotEmpty()
                .hasSize(2)
                .containsKey(phpClass.getName())
                .containsKey(refClass.getName());
        testObjectSchema(fields, phpClass, api.getComponents().getSchemas());
        testObjectSchema(refFields, refClass, api.getComponents().getSchemas());
    }


    @Test
    void testArrayRef() {
        List<Field> refFields = mockFields();
        PhpClass refClass = mockClass(refFields);
        doTestObjectRef(mockFields(1), refFields, refClass, field -> {
            ArraySchema schema = mock(ArraySchema.class);
            String name = field.getName();
            when(schema.getName()).thenReturn(name);
            String refName = refClass.getName();
            ObjectSchema itemsSchema = mock(ObjectSchema.class);
            when(itemsSchema.get$ref()).thenReturn(refName);
            when(itemsSchema.getDescription()).thenReturn(refName);
            when(schema.getItems()).thenReturn((Schema) itemsSchema);
            when(classExtractor.extractFromIndex(refName)).thenReturn(Optional.of(refClass));
            when(propertyMapper.createSchema(field)).thenReturn(Optional.of(schema));
        });
    }

    private void prepareFields(Collection<Field> fields) {
        fields.forEach(f -> {
            NumberSchema schema = mock(NumberSchema.class);
            String name = f.getName();
            when(schema.getName()).thenReturn(name);
            when(propertyMapper.createSchema(f)).thenReturn(Optional.of(schema));
        });
    }

    private void testObjectSchema(Collection<Field> fields, PhpClass phpClass, Map<String, Schema> schemaMap) {
        assertThat(schemaMap.get(phpClass.getName())).isInstanceOf(ObjectSchema.class);
        ObjectSchema schema = (ObjectSchema) schemaMap.get(phpClass.getName());
        String[] keys = fields.stream().map(Field::getName).toArray(String[]::new);
        assertThat(schema.getProperties()).hasSize(fields.size()).containsKeys(keys);
    }

    List<Field> mockFields(int number) {
        return IntStream.range(0, number).mapToObj(i -> {
            Field field = mock(Field.class);
            when(field.getName()).thenReturn(String.valueOf(i));
            return field;
        }).collect(Collectors.toList());
    }

    List<Field> mockFields() {
        return mockFields(10);
    }

    PhpClass mockClass(List<Field> fields) {
        PhpClass phpClass = mock(PhpClass.class);
        when(fieldsExtractor.extract(phpClass)).thenReturn(fields);
        when(phpClass.getName()).thenReturn("TestClass" + fields.size());

        return phpClass;
    }

    PhpFile mockFile(PhpClass phpClass) {
        PhpFile file = mock(PhpFile.class);
        when(classExtractor.extract(file)).thenReturn(Optional.of(phpClass));

        return file;
    }

    PhpFile mockEmptyFile() {
        return mock(PhpFile.class);
    }
}