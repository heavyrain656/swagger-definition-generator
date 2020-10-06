package com.github.pavelsemenov.swaggerschemagenerator.swagger;

import com.github.pavelsemenov.swaggerschemagenerator.BasePhpFileTest;
import com.github.pavelsemenov.swaggerschemagenerator.psi.PhpClassExtractor;
import com.github.pavelsemenov.swaggerschemagenerator.psi.PhpFieldsExtractor;
import com.jetbrains.php.lang.psi.PhpFile;
import io.swagger.v3.oas.models.Components;

public class SwaggerComponentsFactoryTest extends BasePhpFileTest {
    PhpClassExtractor classExtractor;
    SwaggerComponentsFactory factory;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        classExtractor = new PhpClassExtractor(getIndex());
        factory = new SwaggerComponentsFactory(
                classExtractor,
                new PhpFieldsExtractor(),
                new PhpPropertyMapper(classExtractor)
        );
    }

    public void testCreate() {
        PhpFile phpFile = preparePhpFile("ClassTestDTO.php");
        preparePhpFile("RefTestDTO.php");
        Components components = factory.create(phpFile);
        System.out.println(components);
    }
}