package com.github.pavelsemenov.swaggerschemagenerator.psi;

import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class PhpFieldsExtractor {
    private final PhpFieldFilter fieldFilter;

    @Inject
    public PhpFieldsExtractor(PhpFieldFilter fieldFilter) {
        this.fieldFilter = fieldFilter;
    }

    public List<Field> extract(PhpClass phpClass) {
        return phpClass.getFields().stream().filter(f -> !f.isConstant())
                .filter(f -> !(f instanceof PhpDocProperty))
                .filter(f -> !f.getModifier().isStatic() && f.getModifier().isPublic())
                .filter(f -> !f.getType().isEmpty() && !f.getType().isUndefined()
                        && !fieldFilter.isBanned(f.getType()))
                .collect(Collectors.toList());
    }
}
