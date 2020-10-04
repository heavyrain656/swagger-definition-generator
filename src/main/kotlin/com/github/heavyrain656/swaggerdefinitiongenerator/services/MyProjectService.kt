package com.github.heavyrain656.swaggerdefinitiongenerator.services

import com.intellij.openapi.project.Project
import com.github.heavyrain656.swaggerdefinitiongenerator.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
