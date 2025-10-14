# Coupling Graph Feature - Testing Guide

## Quick Test

To test the new Coupling Graph feature:

### 1. Build the project
```bash
mvn clean package
```

### 2. Run the application
```bash
java -jar target/java-refactoring-tool-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### 3. In the TUI menu
1. Select **"3. Coupling Graph"**
2. Enter project path: `./src/main/java`
3. Choose **"All Packages"** or specify `fr.jeremyhurel` as root package
4. Review the coupling statistics displayed
5. Choose export format (JSON, DOT, or Both)
6. Specify output filename (e.g., `./output/coupling.json` or `./output/coupling.dot`)

### 4. Verify the output

#### For JSON export:
```bash
cat ./output/coupling.json
```

Expected output structure:
```json
{
  "couplingGraph": {
    "nodeCount": XX,
    "couplingCount": YY,
    "nodes": [...],
    "couplings": [
      {
        "from": "fr.jeremyhurel.processors.CouplingGraphProcessor",
        "to": "fr.jeremyhurel.models.CouplingGraph",
        "weight": 0.6
      }
    ]
  }
}
```

#### For DOT export:
```bash
# View the DOT file
cat ./output/coupling.dot

# Generate a visualization (requires Graphviz)
dot -Tpng ./output/coupling.dot -o ./output/coupling.png
dot -Tsvg ./output/coupling.dot -o ./output/coupling.svg
```

### 5. Interpret the results

The coupling graph will show:
- All classes in the analyzed codebase
- Dependencies between classes with weights:
  - **Red edges (1.0+)**: Inheritance relationships
  - **Orange edges (0.6-0.99)**: Strong coupling (field dependencies)
  - **Blue edges (0.3-0.59)**: Moderate coupling (method-level dependencies)
  - **Gray edges (<0.3)**: Weak coupling

### Example: Testing with the tool itself

The tool's own codebase provides a good test case:

```
Expected classes to be found:
- fr.jeremyhurel.Main
- fr.jeremyhurel.models.* (CouplingGraph, CouplingNode, etc.)
- fr.jeremyhurel.processors.* (CouplingGraphProcessor, etc.)
- fr.jeremyhurel.scanners.* (CouplingGraphScanner, etc.)
- fr.jeremyhurel.ui.* (CouplingGraphDialog, etc.)
- fr.jeremyhurel.utils.* (CouplingGraphExporter, etc.)

Expected coupling relationships:
- Main → CouplingGraphDialog
- CouplingGraphDialog → CouplingGraphProcessor
- CouplingGraphProcessor → CouplingGraph
- CouplingGraphProcessor → CouplingGraphScanner
- CouplingGraphScanner → CouplingGraph
- CouplingGraphDialog → CouplingGraphExporter
- And many more...
```

## Automated Testing

You can also test programmatically:

```java
import fr.jeremyhurel.processors.CouplingGraphProcessor;
import fr.jeremyhurel.models.CouplingGraph;
import fr.jeremyhurel.utils.CouplingGraphExporter;

public class CouplingGraphTest {
    public static void main(String[] args) throws Exception {
        // Create processor
        CouplingGraphProcessor processor = new CouplingGraphProcessor("./src/main/java");
        
        // Generate coupling graph
        CouplingGraph graph = processor.generateCouplingGraph();
        
        // Verify results
        System.out.println("Nodes found: " + graph.getNodeCount());
        System.out.println("Couplings found: " + graph.getCouplingCount());
        
        // Export to both formats
        CouplingGraphExporter.exportToJson(graph, "./coupling-test.json");
        CouplingGraphExporter.exportToDot(graph, "./coupling-test.dot");
        
        System.out.println("Test completed successfully!");
    }
}
```

## Troubleshooting

### Issue: "No coupling relationships found"
- Verify the project path is correct
- Check that the root package filter matches your codebase
- Ensure the target directory contains compiled .java files

### Issue: Export fails
- Verify you have write permissions to the output directory
- Check that the directory exists (create it if needed)
- Ensure no file locks on existing files

### Issue: Graph visualization unclear
- Use different layout algorithms: `dot`, `neato`, `fdp`, `circo`
- Filter by specific package using root package option
- Export with different formats and adjust Graphviz settings

## Integration with Graphviz

Install Graphviz for visualization:

```bash
# Ubuntu/Debian
sudo apt-get install graphviz

# macOS
brew install graphviz

# Windows (via Chocolatey)
choco install graphviz
```

Generate various output formats:
```bash
dot -Tpng coupling.dot -o coupling.png    # PNG image
dot -Tsvg coupling.dot -o coupling.svg    # SVG vector
dot -Tpdf coupling.dot -o coupling.pdf    # PDF document
neato -Tpng coupling.dot -o coupling.png  # Alternative layout
```
