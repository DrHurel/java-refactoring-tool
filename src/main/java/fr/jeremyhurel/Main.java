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

        // Create GUI and panels
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace());

        BasicWindow window = new BasicWindow("Main Menu");
        Panel panel = new Panel(new GridLayout(1));

        panel.addComponent(new Label("Java Refactoring Tool - Select an option:"));

        ActionListBox menu = new ActionListBox(new TerminalSize(40, 10));

        menu.addItem("1. Class Diagram", () -> showClassDiagram(gui));
        menu.addItem("2. Called Graph", () -> showCalledGraph(gui));
        menu.addItem("3. Coupling Graph", () -> showCouplingGraph(gui));
        menu.addItem("4. Stats", () -> showStats(gui));
        menu.addItem("5. Help", () -> showHelp(gui));
        menu.addItem("6. Exit", window::close);

        panel.addComponent(menu);

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

    private void showStats(MultiWindowTextGUI gui) {
        StatsDialog dialog = new StatsDialog(gui);
        dialog.show();
    }

    private void showHelp(MultiWindowTextGUI gui) {
        new MessageDialogBuilder()
                .setTitle("Help")
                .setText("""
                        Java Refactoring Tool Help

                        1. Class Diagram - Analyze and visualize class relationships
                        2. Called Graph - Show method call dependencies
                        3. Coupling Graph - Analyze class coupling and dependencies
                        4. Stats - Display code metrics and statistics
                        5. Help - Show this help information
                        6. Exit - Close the application""")
                .build()
                .showDialog(gui);
    }
}