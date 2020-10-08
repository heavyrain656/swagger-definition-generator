package com.github.pavelsemenov.swaggerschemagenerator.swagger;

import com.github.pavelsemenov.swaggerschemagenerator.BasePhpFileTest;
import com.github.pavelsemenov.swaggerschemagenerator.psi.PhpClassExtractor;
import com.github.pavelsemenov.swaggerschemagenerator.psi.PhpFieldFilter;
import com.github.pavelsemenov.swaggerschemagenerator.psi.PhpFieldsExtractor;
import com.jetbrains.php.lang.psi.PhpFile;
import io.swagger.v3.oas.models.OpenAPI;

public class OpenApiFactoryTest extends BasePhpFileTest {
    PhpClassExtractor classExtractor;
    OpenApiFactory factory;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        classExtractor = new PhpClassExtractor(getIndex());
        PhpFieldFilter fieldFilter = new PhpFieldFilter();
        factory = new OpenApiFactory(
                classExtractor,
                new PhpFieldsExtractor(fieldFilter),
                new PhpPropertyMapper(classExtractor, fieldFilter)
        );
    }

    public void testCreate() {
        PhpFile phpFile = preparePhpFile("ClassTestDTO.php");
        preparePhpFile("RefTestDTO.php");
        OpenAPI components = factory.create(phpFile);
        System.out.println(components);
    }
}