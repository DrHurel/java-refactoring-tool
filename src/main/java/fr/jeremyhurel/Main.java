package fr.jeremyhurel;

import java.io.IOException;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.ActionListBox;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import fr.jeremyhurel.ui.CalleeGraphDialog;
import fr.jeremyhurel.ui.ClassDiagramDialog;
import fr.jeremyhurel.ui.CouplingGraphDialog;
import fr.jeremyhurel.ui.ModuleExtractionDialog;
import fr.jeremyhurel.ui.StatsDialog;

public class Main {

    public static void main(String[] args) {
        try {
            new Main().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = terminalFactory.createScreen();
        screen.startScreen();

        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace());

        BasicWindow window = new BasicWindow("+============================================+");
        window.setHints(java.util.Arrays.asList(com.googlecode.lanterna.gui2.Window.Hint.CENTERED));
        
        Panel panel = new Panel(new GridLayout(1));

        panel.addComponent(new Label("|   Java Refactoring & Analysis Tool      |"));
        panel.addComponent(new Label("|              Version 1.1                 |"));
        panel.addComponent(new Label("+============================================+"));
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Label("Analysis & Visualization Tools:"));
        panel.addComponent(new EmptySpace());

        ActionListBox menu = new ActionListBox(new TerminalSize(50, 12));

        menu.addItem("[1] Class Diagram         - Visualize class relationships", () -> showClassDiagram(gui));
        menu.addItem("[2] Call Graph            - Analyze method dependencies", () -> showCalledGraph(gui));
        menu.addItem("[3] Coupling Graph        - Study class coupling", () -> showCouplingGraph(gui));
        menu.addItem("[4] Module Extraction     - Extract cohesive modules", () -> showModuleExtraction(gui));
        menu.addItem("[5] Statistics            - View code metrics", () -> showStats(gui));
        menu.addItem("[6] Help                  - Show documentation", () -> showHelp(gui));
        menu.addItem("[7] Exit                  - Quit application", window::close);

        panel.addComponent(menu);
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Label("------------------------------------------------"));
        panel.addComponent(new Label("Use UP/DOWN arrows to navigate, ENTER to select"));

        window.setComponent(panel);

        gui.addWindowAndWait(window);
        screen.stopScreen();
    }

    private void showClassDiagram(MultiWindowTextGUI gui) {
        ClassDiagramDialog dialog = new ClassDiagramDialog(gui);
        dialog.show();
    }

    private void showCalledGraph(MultiWindowTextGUI gui) {
        CalleeGraphDialog dialog = new CalleeGraphDialog(gui);
        dialog.show();
    }

    private void showCouplingGraph(MultiWindowTextGUI gui) {
        CouplingGraphDialog dialog = new CouplingGraphDialog(gui);
        dialog.show();
    }

    private void showModuleExtraction(MultiWindowTextGUI gui) {
        ModuleExtractionDialog dialog = new ModuleExtractionDialog(gui);
        dialog.show();
    }

    private void showStats(MultiWindowTextGUI gui) {
        StatsDialog dialog = new StatsDialog(gui);
        dialog.show();
    }

    private void showHelp(MultiWindowTextGUI gui) {
        new MessageDialogBuilder()
                .setTitle("Help & Documentation")
                .setText("""
                        +==================================================+
                        |  Java Refactoring & Analysis Tool - Help        |
                        +==================================================+
                        
                        [1] CLASS DIAGRAM
                           * Analyzes Java source code structure
                           * Generates UML class diagrams
                           * Exports to PlantUML format
                           * Shows inheritance, composition, dependencies
                        
                        [2] CALL GRAPH
                           * Visualizes method call relationships
                           * Identifies call chains and dependencies
                           * Helps understand method interactions
                           * Exports to DOT format for Graphviz
                        
                        [3] COUPLING GRAPH
                           * Measures class coupling strength
                           * Calculates coupling coefficients
                           * Identifies highly coupled classes
                           * Weighted graph representation
                        
                        [4] MODULE EXTRACTION
                           * Automatically identifies cohesive modules
                           * 4 extraction strategies available:
                             - AUTO: Elbow method detection
                             - FIXED: Specify exact module count
                             - THRESHOLD: Minimum coupling threshold
                             - COMBINED: Fixed count + threshold
                           * Exports module diagrams to PlantUML
                        
                        [5] STATISTICS
                           * Code metrics and analysis
                           * Class and method counts
                           * Coupling statistics
                           * Module cohesion metrics
                        
                        --------------------------------------------------
                        For more information, visit:
                        https://github.com/DrHurel/java-refactoring-tool
                        """)
                .build()
                .showDialog(gui);
    }
}