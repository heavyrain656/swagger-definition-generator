package com.github.pavelsemenov.swaggerschemagenerator.action;

import com.github.pavelsemenov.swaggerschemagenerator.service.SwaggerService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.psi.PhpFile;

public class GenerateDefinitionsAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        PhpFile psiFile = (PhpFile) anActionEvent.getData(LangDataKeys.PSI_FILE);
        Project project = anActionEvent.getProject();
        if (editor == null || psiFile == null || project == null) {
            return;
        }
        SwaggerService service = project.getService(SwaggerService.class);
        service.parseDocumentation(psiFile);
    }


    @Override
    public void update(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        e.getPresentation().setEnabled(editor != null && psiFile instanceof PhpFile);
    }

}