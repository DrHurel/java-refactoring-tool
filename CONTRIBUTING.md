# Contributing to Java Refactoring Tool

Thank you for your interest in contributing to the Java Refactoring Tool! This document provides guidelines and information for contributors.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Process](#development-process)
- [Coding Standards](#coding-standards)
- [Testing Guidelines](#testing-guidelines)
- [Pull Request Process](#pull-request-process)
- [Issue Reporting](#issue-reporting)

## Code of Conduct

By participating in this project, you agree to abide by our Code of Conduct:

- **Be respectful**: Treat everyone with respect and kindness
- **Be collaborative**: Work together towards common goals
- **Be inclusive**: Welcome contributors from all backgrounds
- **Be constructive**: Provide helpful feedback and suggestions

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Git
- IDE (IntelliJ IDEA, Eclipse, or VS Code recommended)

### Local Setup

1. **Fork the repository** on GitHub
2. **Clone your fork** locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/java-refactoring-tool.git
   cd java-refactoring-tool
   ```
3. **Add upstream remote**:
   ```bash
   git remote add upstream https://github.com/Drhurel/java-refactoring-tool.git
   ```
4. **Build the project**:
   ```bash
   mvn clean compile
   ```
5. **Run tests**:
   ```bash
   mvn test
   ```

## Development Process

### Branching Strategy

- `main`: Production-ready code
- `develop`: Integration branch for features
- `feature/*`: New features
- `bugfix/*`: Bug fixes
- `hotfix/*`: Critical production fixes

### Workflow

1. **Create a feature branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```
2. **Make your changes** following coding standards
3. **Add tests** for new functionality
4. **Run quality checks**:
   ```bash
   mvn checkstyle:check pmd:check spotbugs:check
   ```
5. **Run tests and verify coverage**:
   ```bash
   mvn test jacoco:report
   ```
6. **Commit your changes**:
   ```bash
   git add .
   git commit -m "feat: add new feature description"
   ```
7. **Push to your fork**:
   ```bash
   git push origin feature/your-feature-name
   ```
8. **Create a Pull Request** on GitHub

## Coding Standards

### Java Code Style

- Follow **Google Java Style Guide**
- Use **meaningful variable and method names**
- Add **Javadoc comments** for public methods and classes
- Keep methods **small and focused** (max 20 lines recommended)
- Use **proper exception handling**

### Code Quality Requirements

- **Test Coverage**: Minimum 70% line coverage
- **No Checkstyle violations** (Google checks)
- **No PMD violations** (high priority)
- **No SpotBugs violations** (high priority)
- **No security vulnerabilities** (OWASP check)

### Example Code Style

```java
/**
 * Analyzes Java source code to generate call graphs.
 * 
 * @author Your Name
 * @since 1.0.0
 */
public class CallGraphAnalyzer {
    
    private final String projectPath;
    
    /**
     * Creates a new analyzer for the specified project.
     * 
     * @param projectPath the path to the Java project
     * @throws IllegalArgumentException if path is null or empty
     */
    public CallGraphAnalyzer(String projectPath) {
        if (projectPath == null || projectPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Project path cannot be null or empty");
        }
        this.projectPath = projectPath.trim();
    }
    
    /**
     * Generates a call graph for the project.
     * 
     * @return the generated call graph
     * @throws AnalysisException if analysis fails
     */
    public CallGraph analyze() throws AnalysisException {
        // Implementation here
    }
}
```

## Testing Guidelines

### Test Structure

- **Unit Tests**: Test individual components in isolation
- **Integration Tests**: Test component interactions
- **End-to-End Tests**: Test complete workflows

### Test Naming Convention

```java
@Test
public void shouldGenerateCallGraphWhenValidProjectPathProvided() {
    // Arrange
    String validPath = "./src/main/java";
    CallGraphProcessor processor = new CallGraphProcessor(validPath);
    
    // Act
    CallGraph result = processor.generateCallGraph();
    
    // Assert
    assertNotNull(result);
    assertFalse(result.isEmpty());
}

@Test
public void shouldThrowExceptionWhenInvalidProjectPathProvided() {
    // Arrange & Act & Assert
    assertThrows(IllegalArgumentException.class, () -> {
        new CallGraphProcessor(null);
    });
}
```

### Test Coverage Goals

- **New Features**: 90%+ coverage required
- **Bug Fixes**: Include regression tests
- **Refactoring**: Maintain existing coverage

## Pull Request Process

### Before Submitting

1. **Sync with upstream**:
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```
2. **Run all quality checks**:
   ```bash
   mvn clean verify
   ```
3. **Verify integration tests pass**:
   ```bash
   mvn integration-test
   ```

### PR Requirements

- [ ] **Clear description** of changes
- [ ] **Reference to issue** (if applicable)
- [ ] **Tests included** for new functionality
- [ ] **Documentation updated** (if needed)
- [ ] **No merge conflicts**
- [ ] **All CI checks pass**

### PR Template

```markdown
## Description
Brief description of changes made.

## Type of Change
- [ ] Bug fix (non-breaking change that fixes an issue)
- [ ] New feature (non-breaking change that adds functionality)
- [ ] Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] Documentation update

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing performed

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review performed
- [ ] Comments added to hard-to-understand areas
- [ ] Documentation updated
- [ ] No new warnings introduced
```

### Review Process

1. **Automated checks** must pass
2. **Code review** by maintainer
3. **Integration testing** in CI/CD
4. **Final approval** and merge

## Issue Reporting

### Bug Reports

Use the bug report template and include:

- **Environment details** (OS, Java version, Maven version)
- **Steps to reproduce** the issue
- **Expected vs actual behavior**
- **Error messages** or stack traces
- **Sample code** or project (if possible)

### Feature Requests

Use the feature request template and include:

- **Clear description** of the feature
- **Use case** and motivation
- **Proposed implementation** (if you have ideas)
- **Alternative solutions** considered

### Issue Labels

- `bug`: Something isn't working
- `enhancement`: New feature or request
- `documentation`: Improvements or additions to documentation
- `good first issue`: Good for newcomers
- `help wanted`: Extra attention is needed
- `priority/high`: High priority issue
- `priority/low`: Low priority issue

## Release Process

### Version Numbering

We follow [Semantic Versioning](https://semver.org/):

- `MAJOR.MINOR.PATCH`
- `MAJOR`: Breaking changes
- `MINOR`: New features (backward compatible)
- `PATCH`: Bug fixes (backward compatible)

### Release Checklist

- [ ] All tests pass
- [ ] Documentation updated
- [ ] Version bumped in `pom.xml`
- [ ] Release notes prepared
- [ ] Tag created
- [ ] Artifacts published

## Getting Help

### Communication Channels

- **GitHub Issues**: Bug reports and feature requests
- **GitHub Discussions**: General questions and ideas
- **Code Reviews**: Feedback on pull requests

### Maintainers

- **@jeremyhurel**: Project maintainer

### Resources

- [Spoon Framework Documentation](https://spoon.gforge.inria.fr/)
- [Lanterna Documentation](https://github.com/mabe02/lanterna)
- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Maven Documentation](https://maven.apache.org/guides/)

## Recognition

Contributors will be recognized in:

- **README.md**: Contributors section
- **Release notes**: Notable contributions
- **GitHub**: Contributor graph and activity

Thank you for contributing to the Java Refactoring Tool! Your efforts help make this project better for everyone.