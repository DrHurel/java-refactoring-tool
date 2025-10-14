# Java Refactoring Tool

[![CI/CD Pipeline](https://github.com/jeremyhurel/java-refactoring-tool/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/jeremyhurel/java-refactoring-tool/actions/workflows/ci-cd.yml)
[![Code Quality & Security](https://github.com/jeremyhurel/java-refactoring-tool/actions/workflows/quality-security.yml/badge.svg)](https://github.com/jeremyhurel/java-refactoring-tool/actions/workflows/quality-security.yml)
[![codecov](https://codecov.io/gh/jeremyhurel/java-refactoring-tool/branch/main/graph/badge.svg)](https://codecov.io/gh/jeremyhurel/java-refactoring-tool)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=jeremyhurel_java-refactoring-tool&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=jeremyhurel_java-refactoring-tool)

A comprehensive Java analysis tool that generates call graphs, class diagrams, and project statistics using Spoon framework and terminal-based UI.

## Features

### 🔄 Call Graph Generation
- Analyze method call relationships across your Java project
- Export to **Graphviz DOT** format for visualization
- Export to **JSON** format for programmatic analysis
- Support for filtering by root class/method

### 📊 Class Diagram Generation
- Generate UML class diagrams from Java source code
- Export to **PlantUML** format for professional diagrams with color-coding
- Export to **JSON** format for data analysis
- Detect relationships: inheritance, implementation, composition, aggregation
- Analyze attributes, methods, and constructors
- **Package encapsulation**: Group classes by package structure
- **Color-coded output**: Interfaces (blue), Abstract classes (yellow), Concrete classes (green)

### � Coupling Graph Analysis
- Analyze coupling relationships between classes based on method calls
- Calculate normalized coupling using formula: `Couplage(A,B) = Calls(A→B) / Total Calls`
- Filter external libraries (Java stdlib, frameworks, third-party)
- Export to **JSON** and **DOT** formats
- **Hierarchical Cluster Tree**: Generate dendrograms showing class groupings
  - Agglomerative clustering with average linkage
  - Export to JSON, DOT, Text, and Newick formats
  - Identify natural module boundaries
  - Support refactoring decisions

### �📈 Project Statistics
- **13 comprehensive metrics** including:
  - Number of classes, methods, packages
  - Lines of code analysis
  - Average methods per class
  - Average lines of code per method
  - Top 10% classes by methods/attributes
  - Classes with high complexity
  - Maximum parameters per method
- Export to **TXT** format
- Display results in terminal UI

## Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Installation

1. Clone the repository:
```bash
git clone https://github.com/jeremyhurel/java-refactoring-tool.git
cd java-refactoring-tool
```

2. Build the project:
```bash
mvn clean package
```

3. Run the application:
```bash
java -jar target/java-refactoring-tool-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Usage

1. **Interactive Mode**: Run the JAR file and follow the terminal UI
2. **Command Line Examples**:
   ```bash
   # Generate call graph
   java -cp target/java-refactoring-tool-1.0-SNAPSHOT.jar fr.jeremyhurel.test.TestDotExport
   
   # Generate class diagram
   java -cp target/java-refactoring-tool-1.0-SNAPSHOT.jar fr.jeremyhurel.test.TestClassDiagramExport
   
   # Generate statistics
   java -cp target/java-refactoring-tool-1.0-SNAPSHOT.jar fr.jeremyhurel.test.TestStatisticsExport
   ```

## Output Formats

### Call Graphs
- **DOT Format**: Compatible with Graphviz for visualization
- **JSON Format**: Machine-readable data structure

### Class Diagrams
- **PlantUML Format**: Standard UML diagram syntax with automatic color-coding
  - Interfaces: Light blue background
  - Abstract classes: Light yellow background
  - Concrete classes: Light green background
  - Packages: WhiteSmoke background with gray borders
- **JSON Format**: Structured class information

### Statistics
- **TXT Format**: Human-readable statistics report
- **Terminal Display**: Interactive results viewing

## CI/CD Pipeline

Our project uses a comprehensive CI/CD pipeline with the following features:

### ✅ Continuous Integration
- **Multi-Java Version Testing**: Java 17 and 21
- **Automated Testing**: Unit tests with Maven Surefire
- **Code Coverage**: JaCoCo integration with 70% minimum threshold
- **Quality Gates**: Checkstyle, PMD, SpotBugs analysis

### 🔒 Security & Quality
- **OWASP Dependency Check**: Vulnerability scanning
- **Snyk Security Analysis**: Advanced security scanning
- **SonarCloud Integration**: Code quality analysis
- **Performance Testing**: Memory and execution time validation

### 🚀 Deployment
- **Automated Builds**: JAR artifacts with dependencies
- **Release Automation**: GitHub releases with artifacts
- **Integration Testing**: End-to-end functionality validation

### 🔄 Maintenance
- **Dependency Updates**: Weekly automated dependency updates
- **Security Monitoring**: Daily security scans
- **Quality Monitoring**: Continuous code quality tracking

## Development

### Local Development Setup

1. **Clone and Setup**:
```bash
git clone https://github.com/jeremyhurel/java-refactoring-tool.git
cd java-refactoring-tool
mvn clean compile
```

2. **Run Tests**:
```bash
mvn test
```

3. **Code Quality Checks**:
```bash
mvn checkstyle:check pmd:check spotbugs:check
```

4. **Generate Coverage Report**:
```bash
mvn jacoco:report
```

### Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Quality Standards

- **Test Coverage**: Minimum 70% line coverage
- **Code Style**: Google Java Style Guide
- **Security**: No high/critical vulnerabilities
- **Performance**: Memory usage under 256MB for standard analysis

## Architecture

### Core Components
- **Spoon Framework**: AST-based Java code analysis
- **Lanterna**: Terminal-based user interface
- **Visitor Pattern**: Systematic code traversal
- **Export System**: Multi-format output generation

### Project Structure
```
src/main/java/fr/jeremyhurel/
├── Main.java                 # Application entry point
├── models/                   # Data models
├── processors/               # Analysis processors
├── scanners/                 # Code scanners (visitors)
├── ui/                      # Terminal UI dialogs
├── utils/                   # Export utilities
└── test/                    # Test utilities
```

## Visualization Examples

### Call Graph (DOT)
```dot
digraph CallGraph {
    rankdir=TB;
    "Main.main" -> "Main.start";
    "Main.start" -> "GUI.show";
}
```

### Class Diagram (PlantUML)
```plantuml
@startuml
class Main {
  + void main(String[] args)
  + void start()
}

class CallGraphProcessor {
  - String projectPath
  + CallGraph generateCallGraph()
}

Main --> CallGraphProcessor
@enduml
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Spoon Framework](https://spoon.gforge.inria.fr/) for AST analysis
- [Lanterna](https://github.com/mabe02/lanterna) for terminal UI
- [PlantUML](https://plantuml.com/) for UML diagram format
- [Graphviz](https://graphviz.org/) for graph visualization

## Support

- 📧 Issues: [GitHub Issues](https://github.com/jeremyhurel/java-refactoring-tool/issues)
- 📚 Documentation: [Wiki](https://github.com/jeremyhurel/java-refactoring-tool/wiki)
- 💬 Discussions: [GitHub Discussions](https://github.com/jeremyhurel/java-refactoring-tool/discussions)