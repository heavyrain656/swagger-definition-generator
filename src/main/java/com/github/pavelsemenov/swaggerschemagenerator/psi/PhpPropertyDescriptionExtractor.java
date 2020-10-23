package com.github.pavelsemenov.swaggerschemagenerator.psi;

import com.jetbrains.php.lang.documentation.phpdoc.PhpDocUtil;
import com.jetbrains.php.lang.documentation.phpdoc.psi.PhpDocComment;
import com.jetbrains.php.lang.psi.elements.Field;

import javax.inject.Inject;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PhpPropertyDescriptionExtractor {
    @Inject
    PhpPropertyDescriptionExtractor() {
    }

    public Optional<String> extractDescription(Field field) {
        PhpDocComment comment = field.getDocComment();
        String description = comment != null ? PhpDocUtil.getDescription(comment) : "";
        if (!description.isEmpty()) {
            description = Stream.of(description.split("\n")).map(String::trim).collect(Collectors.joining("\n"))
                    .replaceAll("<.*?>", "");
        }
        return description.isEmpty() ? Optional.empty() : Optional.of(description);
    }
}
