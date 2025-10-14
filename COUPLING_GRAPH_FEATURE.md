# Coupling Graph Feature - Implementation Summary

## Overview
A new coupling graph feature has been successfully added to the Java Refactoring Tool, following the same architecture pattern as the existing Call Graph and Class Diagram features.

## Components Implemented

### 1. Model Layer
- **`CouplingNode.java`**: New node class extending `Node` to represent classes in the coupling graph
- **`CouplingGraph.java`**: Enhanced model to store coupling relationships with weighted connections between classes

### 2. Scanner Layer
- **`CouplingGraphScanner.java`**: Spoon-based processor that analyzes:
  - Inheritance relationships (weight: 1.0)
  - Interface implementations (weight: 0.8)
  - Field declarations (weight: 0.6)
  - Generic type parameters (weight: 0.4)
  - Method return types, parameters, and invocations (weight: 0.3)
  - Constructor calls and dependencies

### 3. Processor Layer
- **`CouplingGraphProcessor.java`**: Orchestrates the coupling analysis using Spoon launcher
  - Supports full project analysis or filtered by root package
  - Follows the same pattern as CallGraphProcessor and ClassDiagramProcessor

### 4. Utility Layer
- **`CouplingGraphExporter.java`**: Export functionality supporting:
  - **JSON format**: Structured data with nodes and coupling relationships with weights
  - **DOT format**: Graphviz visualization with:
    - Color-coded edges (red/orange/blue/gray) based on coupling strength
    - Varying edge styles (bold/solid/dashed) and widths
    - Simple class names for readability

### 5. UI Layer
- **`CouplingGraphDialog.java`**: Terminal UI dialog providing:
  - Project path input
  - Optional root package filtering
  - Progress feedback
  - Export format selection (JSON, DOT, or both)
  - Error handling and user feedback

### 6. Integration
- **`Main.java`**: Updated main menu to include:
  - New menu item "3. Coupling Graph"
  - Updated help text
  - Handler method `showCouplingGraph()`

## Usage

1. Run the application:
   ```bash
   java -jar target/java-refactoring-tool-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

2. Select "3. Coupling Graph" from the main menu

3. Enter the project path (default: `./src/main/java`)

4. Choose analysis scope:
   - "All Packages": Analyze entire project
   - "Specify Root Package": Filter by package prefix

5. Review coupling statistics:
   - Number of classes analyzed
   - Number of coupling relationships found

6. Choose export format:
   - JSON: Machine-readable structured data
   - DOT: Visual graph for Graphviz rendering
   - Both: Export to both formats

## Coupling Weight System

The coupling graph uses a weighted system to represent different types of dependencies:

- **1.0**: Inheritance (strongest coupling)
- **0.8**: Interface implementation
- **0.6**: Field dependencies
- **0.4**: Generic type parameters
- **0.3**: Method-level dependencies (parameters, return types, invocations)

## Visualization

The DOT export creates a directed graph where:
- **Nodes**: Classes (shown with simple names)
- **Edges**: Coupling relationships with weights
- **Edge Colors**:
  - Red: weight ≥ 1.0 (inheritance)
  - Orange: weight ≥ 0.6 (strong coupling)
  - Blue: weight ≥ 0.3 (moderate coupling)
  - Gray: weight < 0.3 (weak coupling)
- **Edge Styles**:
  - Bold (penwidth 3): weight ≥ 1.0
  - Solid thick (penwidth 2): weight ≥ 0.6
  - Solid thin (penwidth 1): weight ≥ 0.3
  - Dashed: weight < 0.3

## Build Status

✅ Successfully compiled with Maven
✅ All components integrated
✅ Follows existing architecture patterns
✅ Ready for testing and use

## Example Output

### JSON Export
```json
{
  "couplingGraph": {
    "nodeCount": 15,
    "couplingCount": 42,
    "nodes": [...],
    "couplings": [
      {
        "from": "com.example.ClassA",
        "to": "com.example.ClassB",
        "weight": 0.9
      }
    ]
  }
}
```

### DOT Export
Can be visualized with Graphviz:
```bash
dot -Tpng couplinggraph.dot -o couplinggraph.png
```
