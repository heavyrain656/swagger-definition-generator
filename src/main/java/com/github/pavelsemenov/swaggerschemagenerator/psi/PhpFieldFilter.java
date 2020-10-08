package com.github.pavelsemenov.swaggerschemagenerator.psi;

import com.jetbrains.php.lang.psi.resolve.types.PhpType;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

public class PhpFieldFilter {
    static final Set<String> BANNED_TYPES = new HashSet<>(
            Arrays.asList(PhpType._CLOSURE, PhpType._THROWABLE,
                    PhpType._CALLABLE, PhpType._EXCEPTION, PhpType._OBJECT,
                    PhpType._RESOURCE, PhpType._ARRAY, PhpType._NULL, PhpType._MIXED
            )
    );

    @Inject
    public PhpFieldFilter() {
    }

    public String getFirstType(PhpType type) {
        List<String> filtered = type.getTypes().stream()
                .filter(t -> !PhpType._NULL.contains(t))
                .collect(Collectors.toList());

        return filtered.size() == 1 ? filtered.get(0) : PhpType._NULL;
    }

    public boolean isBanned(String type) {
        return BANNED_TYPES.contains(type);
    }

    public boolean isBanned(PhpType type) {
        return isBanned(getFirstType(type));
    }
}
