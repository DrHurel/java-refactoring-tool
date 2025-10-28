#!/bin/bash

# Java Refactoring Tool - Run Script
# This script builds and runs the Java Refactoring Tool

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Project root directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# Navigate to project root
cd "$PROJECT_ROOT"

# Function to print colored output
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if Maven is installed
check_maven() {
    if ! command -v mvn &> /dev/null; then
        print_error "Maven is not installed. Please install Maven first."
        exit 1
    fi
    print_success "Maven found: $(mvn --version | head -n 1)"
}

# Function to check if Java is installed
check_java() {
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed. Please install Java 17 or higher."
        exit 1
    fi
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 17 ]; then
        print_error "Java 17 or higher is required. Current version: $JAVA_VERSION"
        exit 1
    fi
    print_success "Java found: $(java -version 2>&1 | head -n 1)"
}

# Function to clean the project
clean_project() {
    print_info "Cleaning project..."
    mvn clean -q
    print_success "Project cleaned"
}

# Function to build the project
build_project() {
    print_info "Building project..."
    if mvn package -q -DskipTests; then
        print_success "Build successful"
    else
        print_error "Build failed"
        exit 1
    fi
}

# Function to run the application
run_application() {
    print_info "Starting Java Refactoring Tool..."
    echo ""
    
    JAR_FILE="target/java-refactoring-tool-1.1-SNAPSHOT-jar-with-dependencies.jar"
    
    if [ ! -f "$JAR_FILE" ]; then
        print_error "JAR file not found: $JAR_FILE"
        print_info "Building project first..."
        build_project
    fi
    
    java -jar "$JAR_FILE"
}

# Function to run tests
run_tests() {
    print_info "Running tests..."
    if mvn test; then
        print_success "All tests passed"
    else
        print_error "Some tests failed"
        exit 1
    fi
}

# Function to generate test exports
generate_test_exports() {
    print_info "Generating test exports..."
    
    # Run test export classes
    CLASSPATH="target/classes:$(mvn dependency:build-classpath -q -Dmdep.outputFile=/dev/stdout)"
    
    echo ""
    print_info "Running Class Diagram Export Test..."
    java -cp "$CLASSPATH" fr.jeremyhurel.test.TestClassDiagramExport
    
    echo ""
    print_info "Running Cluster Tree Export Test..."
    java -cp "$CLASSPATH" fr.jeremyhurel.test.TestClusterTreeExport
    
    echo ""
    print_info "Running Statistics Export Test..."
    java -cp "$CLASSPATH" fr.jeremyhurel.test.TestStatisticsExport
    
    print_success "Test exports generated successfully"
}

# Function to visualize outputs
visualize_outputs() {
    if ! command -v dot &> /dev/null; then
        print_warning "Graphviz not installed. Skipping visualization."
        print_info "Install with: sudo apt install graphviz"
        return
    fi
    
    print_info "Generating visualizations..."
    
    if [ -f "clustertree.dot" ]; then
        dot -Tpng clustertree.dot -o clustertree.png
        dot -Tsvg clustertree.dot -o clustertree.svg
        print_success "Cluster tree visualizations created (PNG & SVG)"
    fi
    
    if [ -f "couplinggraph.dot" ]; then
        dot -Tpng couplinggraph.dot -o couplinggraph.png
        dot -Tsvg couplinggraph.dot -o couplinggraph.svg
        print_success "Coupling graph visualizations created (PNG & SVG)"
    fi
    
    if [ -f "callgraph.dot" ]; then
        dot -Tpng callgraph.dot -o callgraph.png
        dot -Tsvg callgraph.dot -o callgraph.svg
        print_success "Call graph visualizations created (PNG & SVG)"
    fi
}

# Function to show help
show_help() {
    cat << EOF
${BLUE}Java Refactoring Tool - Run Script${NC}

Usage: $0 [OPTION]

Options:
    run         Build (if needed) and run the application (default)
    build       Clean and build the project
    clean       Clean the project
    test        Run unit tests
    export      Generate test exports (diagrams, trees, stats)
    visualize   Generate PNG/SVG from DOT files (requires Graphviz)
    full        Clean, build, run tests, generate exports, and visualize
    help        Show this help message

Examples:
    $0              # Run the application
    $0 run          # Run the application
    $0 build        # Build the project
    $0 full         # Complete workflow
    $0 export       # Generate all test exports

EOF
}

# Main script logic
main() {
    # Check prerequisites
    check_java
    check_maven
    
    echo ""
    
    # Parse command line arguments
    case "${1:-run}" in
        run)
            run_application
            ;;
        build)
            clean_project
            build_project
            ;;
        clean)
            clean_project
            ;;
        test)
            build_project
            run_tests
            ;;
        export)
            build_project
            generate_test_exports
            ;;
        visualize)
            visualize_outputs
            ;;
        full)
            clean_project
            build_project
            run_tests
            generate_test_exports
            visualize_outputs
            print_success "Complete workflow finished!"
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            print_error "Unknown option: $1"
            echo ""
            show_help
            exit 1
            ;;
    esac
}

# Run main function
main "$@"
