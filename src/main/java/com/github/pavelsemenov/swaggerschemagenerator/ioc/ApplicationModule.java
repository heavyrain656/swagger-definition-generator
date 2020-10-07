package com.github.pavelsemenov.swaggerschemagenerator.ioc;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.PhpIndex;
import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    @Provides
    PhpIndex providePhpIndex(Project project) {
        return PhpIndex.getInstance(project);
    }
}
