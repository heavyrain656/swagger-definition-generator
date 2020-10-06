package com.github.pavelsemenov.swaggerschemagenerator.psi;

import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;

import javax.inject.Inject;
import java.util.Collection;
import java.util.stream.Collectors;

public class PhpFieldsExtractor {
    @Inject
    public PhpFieldsExtractor() {
    }

    public Collection<Field> extract(PhpClass phpClass) {
        return phpClass.getFields().stream().filter(f -> !f.isConstant())
                .filter(f -> !f.getModifier().isStatic() && f.getModifier().isPublic())
                .filter(f -> !f.getType().isEmpty() && !f.getType().isUndefined())
                .collect(Collectors.toList());
    }
}
