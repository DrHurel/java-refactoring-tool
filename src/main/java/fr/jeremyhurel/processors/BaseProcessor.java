package fr.jeremyhurel.processors;

import spoon.Launcher;
import spoon.reflect.CtModel;

public abstract class BaseProcessor {

    protected final String projectPath;

    protected BaseProcessor(String projectPath) {
        this.projectPath = projectPath;
    }

    protected Launcher createLauncher() {
        Launcher launcher = new Launcher();
        launcher.addInputResource(projectPath);
        configureLauncher(launcher);
        return launcher;
    }

    protected void configureLauncher(Launcher launcher) {
        launcher.getEnvironment().setAutoImports(true);
        launcher.getEnvironment().setCommentEnabled(false);
        launcher.getEnvironment().setNoClasspath(true);
    }

    protected CtModel buildModel(Launcher launcher) {
        return launcher.buildModel();
    }

    public String getProjectPath() {
        return projectPath;
    }
}
