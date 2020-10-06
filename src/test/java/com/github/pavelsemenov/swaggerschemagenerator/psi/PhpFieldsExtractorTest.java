package com.github.pavelsemenov.swaggerschemagenerator.psi;

import com.github.pavelsemenov.swaggerschemagenerator.BasePhpFileTest;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class PhpFieldsExtractorTest extends BasePhpFileTest {
    PhpClassExtractor classExtractor;
    PhpFieldsExtractor fieldsExtractor;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        classExtractor = new PhpClassExtractor(getIndex());
        fieldsExtractor = new PhpFieldsExtractor();
    }

    public void testValidClass() {
        PhpFile phpFile = preparePhpFile("ClassTestDTO.php");
        PhpClass phpClass = classExtractor.extract(phpFile).orElse(null);
        assertThat(phpClass).isNotNull();
        Collection<Field> fields = fieldsExtractor.extract(phpClass);
        Set<String> validFieldNames = new HashSet<>(
                Arrays.asList(
                        "stringProperty", "intProperty", "typedArrayProperty",
                        "arrayProperty", "typedProperty", "objectProperty", "typedObjectProperty"
                )
        );
        assertThat(fields).isNotEmpty().hasSameSizeAs(validFieldNames).allMatch(f -> validFieldNames.contains(f.getName()), "Incorrect fields are filtered out");
    }
}