package com.github.pavelsemenov.swaggerschemagenerator.psi;

import com.github.pavelsemenov.swaggerschemagenerator.BasePhpFileTest;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class PhpFieldsExtractorTest extends BasePhpFileTest {
    PhpClassExtractor classExtractor;
    PhpFieldsExtractor fieldsExtractor;
    @Mock
    PhpFieldFilter fieldFilter;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.openMocks(this);
        classExtractor = new PhpClassExtractor(getIndex());
        fieldsExtractor = new PhpFieldsExtractor(fieldFilter);
    }

    public void testValidClass() {
        PhpClass phpClass = prepare();
        when(fieldFilter.isBanned(any(PhpType.class))).thenReturn(false);
        Collection<Field> fields = fieldsExtractor.extract(phpClass);
        Set<String> bannedFields = new HashSet<>(
                Arrays.asList(
                        "virtualProperty", "CONSTANT", "mixedProperty",
                        "privateTypedProperty", "protectedTypedProperty"
                )
        );
        assertThat(fields).isNotEmpty().hasSize(13).noneMatch(f -> bannedFields.contains(f.getName()));
    }

    public void testBannedProperties() {
        PhpClass phpClass = prepare();
        when(fieldFilter.isBanned(any(PhpType.class))).thenReturn(true);
        Collection<Field> fields = fieldsExtractor.extract(phpClass);
        assertThat(fields).isEmpty();
    }

    private PhpClass prepare() {
        PhpFile phpFile = preparePhpFile("ClassTestDTO.php");
        PhpClass phpClass = classExtractor.extract(phpFile).orElse(null);
        assertThat(phpClass).isNotNull();
        return phpClass;
    }
}