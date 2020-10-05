package com.github.pavelsemenov.swaggerschemagenerator.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import com.jetbrains.php.lang.psi.visitors.PhpRecursiveElementVisitor;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Schema;

import java.util.Collection;

public class TestAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        PhpFile psiFile = (PhpFile) anActionEvent.getData(LangDataKeys.PSI_FILE);
        Project project = anActionEvent.getProject();
        if (editor == null || psiFile == null || project == null) {
            return;
        }

        final StringBuilder infoBuilder = new StringBuilder();
        infoBuilder.append("Here we go: ").append(psiFile.getMainNamespaceName()).append("\n");
        PhpIndex index = PhpIndex.getInstance(project);
        Components components = new Components();
        Schema schema = new Schema();
        schema.setType("object");
        components.addSchemas("hoba", schema);
        psiFile.accept(new PhpRecursiveElementVisitor() {
            @Override
            public void visitPhpClass(PhpClass clazz) {
                super.visitPhpClass(clazz);
                clazz.getFields().stream().filter(f -> PhpType.isPrimitiveType(f.getType().toString()))
                        .forEach(f -> {
                            Schema<String> propSchema = new Schema<>();
                            propSchema.setType(f.getType().toString());
                            propSchema.setName(f.getName());
                            schema.addProperties(f.getName(), propSchema);
                        });
                //parseClass(clazz, infoBuilder, index);
            }
        });
        components.addSchemas("hoba", schema);
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try {
            String yaml = mapper.writeValueAsString(schema);
            Messages.showMessageDialog(anActionEvent.getProject(), yaml, "PSI Info", null);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void parseClass(PhpClass cls, StringBuilder builder, PhpIndex index) {
        cls.getFields().forEach(f -> {
            builder.append(f.getType()).append(" -> ").append(f.getName());
            Collection<PhpClass> classes = index.getClassesByFQN(f.getType().toString());
            classes.forEach(c -> parseClass(c, builder, index));
            builder.append("\n");
        });
    }

    @Override
    public void update(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        e.getPresentation().setEnabled(editor != null && psiFile != null);
    }

}