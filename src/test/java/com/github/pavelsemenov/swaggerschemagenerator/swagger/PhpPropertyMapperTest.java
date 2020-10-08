package com.github.pavelsemenov.swaggerschemagenerator.swagger;

import com.github.pavelsemenov.swaggerschemagenerator.psi.PhpClassExtractor;
import com.github.pavelsemenov.swaggerschemagenerator.psi.PhpFieldFilter;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import io.swagger.v3.oas.models.media.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("rawtypes")
public class PhpPropertyMapperTest {
    @Mock
    PhpClassExtractor classExtractor;
    @Mock
    PhpFieldFilter fieldFilter;
    PhpPropertyMapper propertyMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        propertyMapper = new PhpPropertyMapper(classExtractor, fieldFilter);
    }

    @ParameterizedTest
    @ValueSource(strings = {PhpType._INTEGER, PhpType._INT})
    public void testIntegerTypes(String type) {
        testType(type, IntegerSchema.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {PhpType._NUMBER, PhpType._FLOAT, PhpType._DOUBLE})
    public void testNumberTypes(String type) {
        NumberSchema schema = testType(type, NumberSchema.class);
        if (!type.equals(PhpType._NUMBER)) {
            assertThat(schema.getFormat()).isNotNull();
            assertThat(type).contains(schema.getFormat());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {PhpType._BOOL, PhpType._BOOLEAN})
    public void testBooleanTypes(String type) {
        testType(type, BooleanSchema.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {PhpType._STRING})
    public void testStringTypes(String type) {
        testType(type, StringSchema.class);
    }

    @Test
    public void testNullableType() {
        PhpType type = PhpType.builder().add(PhpType._STRING).add(PhpType._NULL).build();
        when(fieldFilter.getFirstType(type)).thenReturn(PhpType._STRING);
        Optional<Schema> schema = propertyMapper.createSchema(mockField(type));
        assertThat(schema).isPresent();
        assertThat(schema.get().getNullable()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {PhpType._NULL, PhpType._ARRAY, PhpType._CALLABLE, PhpType._EXCEPTION})
    public void testSomeBannedTypes(String type) {
        testBannedType(type);
    }

    @ParameterizedTest
    @ValueSource(strings = {"\\tests\\SomeClass", "AnotherClass"})
    public void testClasses(String type) {
        Optional<PhpClass> phpClassOpt = mockPhpClass(type);
        PhpClass phpClass = phpClassOpt.get();
        when(classExtractor.extractFromIndex(type)).thenReturn(phpClassOpt);
        ObjectSchema schemaType = testType(type, ObjectSchema.class);
        assertThat(schemaType.get$ref()).contains(phpClass.getName());
        assertThat(schemaType.getDescription()).isEqualTo(phpClass.getFQN());
    }

    @ParameterizedTest
    @ValueSource(strings = {"\\tests\\SomeClass", "AnotherClass"})
    public void testNotFoundClasses(String type) {
        Optional<PhpClass> phpClassOpt = Optional.empty();
        when(classExtractor.extractFromIndex(type)).thenReturn(phpClassOpt);
        testBannedType(type);
    }

    @ParameterizedTest
    @ValueSource(strings = {"\\int[]", "\\string[]"})
    public void testScalarArrays(String type) {
        when(fieldFilter.getFirstType(PhpType.INT)).thenReturn(PhpType._INT);
        when(fieldFilter.getFirstType(PhpType.STRING)).thenReturn(PhpType._STRING);
        ArraySchema arraySchema = testType(type, ArraySchema.class);
        Class schemaClass = type.contains("int") ? IntegerSchema.class : StringSchema.class;
        assertThat(arraySchema.getItems()).isNotNull().isInstanceOf(schemaClass);
    }

    @ParameterizedTest
    @ValueSource(strings = {"\\tests\\SomeClass[]", "AnotherClass[]"})
    public void testClassArrays(String type) {
        String singularType = type.replace("[]", "");
        when(fieldFilter.getFirstType(PhpType.builder().add(type).build())).thenReturn(type);
        when(fieldFilter.getFirstType(PhpType.builder().add(singularType).build())).thenReturn(singularType);
        Optional<PhpClass> phpClassOpt = mockPhpClass(singularType);
        when(classExtractor.extractFromIndex(singularType)).thenReturn(phpClassOpt);
        ArraySchema arraySchema = testType(type, ArraySchema.class);
        assertThat(arraySchema.getItems()).isNotNull().isInstanceOf(ObjectSchema.class);
        assertThat(arraySchema.getItems().get$ref()).isNotNull().contains(singularType);
    }

    private Optional<PhpClass> mockPhpClass(String type) {
        PhpClass phpClass = mock(PhpClass.class);
        when(phpClass.getName()).thenReturn(type);
        when(phpClass.getFQN()).thenReturn(type);

        return Optional.of(phpClass);
    }

    private <T extends Schema> T testType(String type, Class<T> schemaClass) {
        Optional<Schema> schema = getSchema(type);
        assertThat(schema).isPresent().get().isInstanceOf(schemaClass);

        return (T) schema.get();
    }

    private Optional<Schema> getSchema(String type) {
        PhpType phpType = PhpType.builder().add(type).build();
        when(fieldFilter.getFirstType(phpType)).thenReturn(type);

        return propertyMapper.createSchema(mockField(phpType));
    }

    private void testBannedType(String type) {
        Optional<Schema> schema = getSchema(type);
        assertThat(schema).isEmpty();
    }

    private Field mockField(PhpType type) {
        Field field = mock(Field.class);
        when(field.getType()).thenReturn(type);
        when(field.getName()).thenReturn(type.toString());

        return field;
    }
}