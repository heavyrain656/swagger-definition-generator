package com.github.pavelsemenov.swaggerschemagenerator.psi;

import com.github.pavelsemenov.swaggerschemagenerator.BasePhpFileTest;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class PhpPropertyDescriptionExtractorTest extends BasePhpFileTest {
    PhpClassExtractor extractor;
    PhpFieldsExtractor fieldsExtractor;
    PhpPropertyDescriptionExtractor descriptionExtractor;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        extractor = new PhpClassExtractor(getIndex());
        fieldsExtractor = new PhpFieldsExtractor(new PhpFieldFilter());
        descriptionExtractor = new PhpPropertyDescriptionExtractor();
    }

    public void testProperties() {
        List<String> expected = Arrays.asList(
                "simple comment",
                "simple comment with tags",
                "simple multiline comment\nwith second line\nand third line",
                "html comment",
                "html multiline comment\nwith second line\nand third line"
        );
        PhpFile file = preparePhpFile("DocCommentDTO.php");
        PhpClass phpClass = extractor.extract(file).get();
        List<Field> fields = fieldsExtractor.extract(phpClass);
        fields.stream().filter(f -> !f.getName().startsWith("failed")).forEach(f -> {
            Optional<String> description = descriptionExtractor.extractDescription(f);
            assertThat(description).isPresent();
            assertThat(description.get()).isIn(expected);
        });
        fields.stream().filter(f -> f.getName().startsWith("failed")).forEach(f -> {
            Optional<String> description = descriptionExtractor.extractDescription(f);
            assertThat(description).isEmpty();
        });
    }
}