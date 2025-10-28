package fr.jeremyhurel.models;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleGraph {

    private final List<Module> modules;
    private final Map<String, Module> classToModuleMap;

    public ModuleGraph() {
        this.modules = new ArrayList<>();
        this.classToModuleMap = new HashMap<>();
    }

    public void addModule(Module module) {
        modules.add(module);

        for (String className : module.getClasses()) {
            classToModuleMap.put(className, module);
        }
    }

    public List<Module> getModules() {
        return new ArrayList<>(modules);
    }

    public int getModuleCount() {
        return modules.size();
    }

    public Module getModuleForClass(String className) {
        return classToModuleMap.get(className);
    }

    public Module getModuleById(int id) {
        for (Module module : modules) {
            if (module.getId() == id) {
                return module;
            }
        }
        return null;
    }

    public double getModuleCoupling(Module module1, Module module2, CouplingGraph couplingGraph) {
        if (module1 == null || module2 == null) {
            return 0.0;
        }

        double totalCoupling = 0.0;
        int pairCount = 0;

        for (String class1 : module1.getClasses()) {
            for (String class2 : module2.getClasses()) {
                double coupling = couplingGraph.getCouplingWeight(class1, class2);
                if (coupling > 0) {
                    totalCoupling += coupling;
                    pairCount++;
                }
            }
        }

        return pairCount > 0 ? totalCoupling / pairCount : 0.0;
    }

    public double getAverageCohesion() {
        if (modules.isEmpty()) {
            return 0.0;
        }

        double totalCohesion = 0.0;
        for (Module module : modules) {
            totalCohesion += module.getCohesion();
        }

        return totalCohesion / modules.size();
    }

    public void exportToFile(String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("========================================\n");
            writer.write("MODULE GRAPH EXPORT\n");
            writer.write("========================================\n\n");

            writer.write("Total modules: " + getModuleCount() + "\n");
            writer.write("Average cohesion: " + String.format("%.4f", getAverageCohesion()) + "\n\n");

            for (Module module : modules) {
                writer.write("  " + module.getName() + ":\n");
                writer.write("    - Size: " + module.getClasses().size() + " classes\n");
                writer.write("    - Cohesion: " + String.format("%.4f", module.getCohesion()) + "\n");
                writer.write("    - Classes:\n");

                for (String className : module.getClasses()) {
                    writer.write("        • " + className + "\n");
                }
                writer.write("\n");
            }

            writer.write("========================================\n");
            writer.write("END OF MODULE GRAPH\n");
            writer.write("========================================\n");
        }
    }

    @Override
    public String toString() {
        return "ModuleGraph{" +
                "modules=" + modules.size() +
                ", avgCohesion=" + String.format("%.4f", getAverageCohesion()) +
                '}';
    }
}
