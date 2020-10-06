package com.github.pavelsemenov.swaggerschemagenerator.psi;

import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.visitors.PhpRecursiveElementVisitor;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PhpClassExtractor {
    private final PhpIndex index;

    @Inject
    public PhpClassExtractor(PhpIndex index) {
        this.index = index;
    }

    public Optional<PhpClass> extract(PhpFile phpFile) {
        final List<Optional<PhpClass>> foundClass = new ArrayList<>();

        phpFile.accept(new PhpRecursiveElementVisitor() {
            @Override
            public void visitPhpClass(PhpClass clazz) {
                super.visitPhpClass(clazz);
                if (foundClass.isEmpty()) {
                    foundClass.add(Optional.of(clazz));
                }
            }
        });

        return foundClass.isEmpty() ? Optional.empty() : foundClass.get(0);
    }

    public Optional<PhpClass> extractFromIndex(String FQCN) {
        return index.getClassesByFQN(FQCN).stream().findAny();
    }
}
