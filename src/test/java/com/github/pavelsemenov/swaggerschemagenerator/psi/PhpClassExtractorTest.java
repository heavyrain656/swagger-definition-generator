package com.github.pavelsemenov.swaggerschemagenerator.psi;

import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.PhpClass;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class PhpClassExtractorTest extends BasePhpFileTest {
    PhpClassExtractor extractor = new PhpClassExtractor();

    public void testValidPHPClass() {
        PhpFile phpFile = preparePhpFile("ExampleDTO.php");
        Optional<PhpClass> extracted = extractor.extract(phpFile);
        simpleDTOAssertions(extracted);
    }

    private void simpleDTOAssertions(Optional<PhpClass> result) {
        assertThat(result.isPresent()).isTrue();
        PhpClass phpClass = result.get();
        assertThat(phpClass.getName()).isEqualTo("ExampleDTO");
    }

    public void testPhpFileWithoutClass() {
        PhpFile phpFile = preparePhpFile("SimplePhpFile.php");
        Optional<PhpClass> extracted = extractor.extract(phpFile);
        assertThat(extracted.isPresent()).isFalse();
    }

    public void testFromIndex() {
        preparePhpFile("ExampleDTO.php");
        Optional<PhpClass> extracted = extractor.extractFromIndex(getIndex(), "\\tests\\ExampleDTO");
        simpleDTOAssertions(extracted);
    }
}