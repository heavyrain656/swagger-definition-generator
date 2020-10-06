package com.github.pavelsemenov.swaggerschemagenerator;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.PhpFile;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BasePhpFileTest extends BasePlatformTestCase {
    public PhpFile preparePhpFile(String fileName) {
        PsiFile psiFile = preparePsiFile(fileName);
        assertThat(psiFile).isInstanceOf(PhpFile.class);

        return (PhpFile) psiFile;
    }

    public PsiFile preparePsiFile(String fileName) {
        VirtualFile file = myFixture.copyFileToProject(fileName);
        PsiManager psiManager = PsiManager.getInstance(getProject());
        PsiFile psiFile = psiManager.findFile(file);
        assertThat(psiFile).isNotNull();

        return psiFile;
    }

    public PhpIndex getIndex() {
        return PhpIndex.getInstance(getProject());
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }
}
