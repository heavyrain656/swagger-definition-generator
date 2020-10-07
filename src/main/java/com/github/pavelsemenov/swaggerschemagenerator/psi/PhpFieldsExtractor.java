package com.github.pavelsemenov.swaggerschemagenerator.psi;

import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocProperty;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class PhpFieldsExtractor {
    public static final Set<String> BANNED_TYPES = new HashSet<>(
            Arrays.asList(PhpType._CLOSURE, PhpType._THROWABLE,
                    PhpType._CALLABLE, PhpType._EXCEPTION, PhpType._OBJECT,
                    PhpType._RESOURCE, PhpType._ARRAY, PhpType._NULL, PhpType._MIXED
            )
    );

    @Inject
    public PhpFieldsExtractor() {
    }

    public static String getFirstType(PhpType type) {
        List<String> filtered = type.getTypes().stream().filter(t -> !PhpType._NULL.contains(t))
                .collect(Collectors.toList());

        return filtered.size() == 1 ? filtered.get(0) : PhpType._NULL;
    }

    public Collection<Field> extract(PhpClass phpClass) {
        return phpClass.getFields().stream().filter(f -> !f.isConstant())
                .filter(f -> !(f instanceof PhpDocProperty))
                .filter(f -> !f.getModifier().isStatic() && f.getModifier().isPublic())
                .filter(f -> !f.getType().isEmpty() && !f.getType().isUndefined()
                        && !BANNED_TYPES.contains(getFirstType(f.getType())))
                .collect(Collectors.toList());
    }
}
