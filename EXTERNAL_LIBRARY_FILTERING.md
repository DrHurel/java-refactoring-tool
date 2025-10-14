# External Library Filtering - Coupling Graph

## Overview

The coupling graph has been enhanced to automatically filter out external library classes, ensuring that only classes from your project are included in the analysis and visualization.

## What Gets Filtered Out

### 1. Java Standard Library
- `java.*` - Core Java classes
- `javax.*` - Java extensions
- `jdk.*` - JDK internal classes
- `sun.*` - Sun/Oracle internal classes
- `com.sun.*` - Sun/Oracle utilities

### 2. Common Third-Party Libraries
- `org.springframework.*` - Spring Framework
- `org.hibernate.*` - Hibernate ORM
- `org.apache.*` - Apache libraries (Commons, Log4j, etc.)
- `com.google.*` - Google libraries (Guava, etc.)
- `com.fasterxml.jackson.*` - Jackson JSON
- `org.junit.*` - JUnit testing
- `org.mockito.*` - Mockito mocking
- `org.slf4j.*` - SLF4J logging
- `ch.qos.logback.*` - Logback logging
- `org.eclipse.*` - Eclipse libraries
- `com.googlecode.lanterna.*` - Lanterna TUI
- `spoon.*` - Spoon code analysis

### 3. Primitive Types
- `int`, `long`, `double`, `float`, `boolean`, `char`, `byte`, `short`, `void`

### 4. Out-of-Package Classes (when root package is specified)
When you specify a root package (e.g., `fr.jeremyhurel`), only classes within that package hierarchy will be included. All other classes are considered external.

## Benefits

1. **Cleaner Visualizations**: Only shows coupling between your project classes
2. **Faster Analysis**: Skips processing of external library dependencies
3. **More Relevant Metrics**: Coupling counts and weights reflect only your code
4. **Better Refactoring Insights**: Focus on internal coupling that you can control

## Example

### Before Filtering
```
Classes found: 150
Coupling relationships: 800

Includes:
- fr.jeremyhurel.Main
- fr.jeremyhurel.models.CouplingGraph
- java.util.HashMap
- java.util.Map
- org.springframework.context.ApplicationContext
- com.googlecode.lanterna.gui2.BasicWindow
- spoon.reflect.CtModel
- ... and many more external classes
```

### After Filtering
```
Classes found: 25
Coupling relationships: 65

Includes only:
- fr.jeremyhurel.Main
- fr.jeremyhurel.models.CouplingGraph
- fr.jeremyhurel.models.CouplingNode
- fr.jeremyhurel.processors.CouplingGraphProcessor
- fr.jeremyhurel.scanners.CouplingGraphScanner
- ... only your project classes
```

## Technical Details

### Scanner Enhancement
The `CouplingGraphScanner` now includes:
- `isExternalLibraryClass()` - Comprehensive filter for external classes
- `isPrimitiveType()` - Separate check for primitive types
- Integration with `rootPackage` filtering

### Graph Cleanup
The `CouplingGraph` model includes:
- `removeOrphanedNodes()` - Removes nodes that were referenced but not analyzed
- Automatic cleanup in `CouplingGraphProcessor` after analysis

### How It Works

1. **During Scanning**: Each class reference is checked before adding to the coupling matrix
   ```java
   if (!isExternalLibraryClass(className)) {
       couplingGraph.addCoupling(from, to, weight);
   }
   ```

2. **After Processing**: Orphaned nodes (external classes that were referenced) are removed
   ```java
   couplingGraph.removeOrphanedNodes();
   ```

3. **Result**: Only internal project classes remain in the graph

## Customization

If you need to add more libraries to filter, you can modify `isExternalLibraryClass()` in `CouplingGraphScanner.java`:

```java
// Add your custom library filtering
if (typeName.startsWith("com.mycompany.external.") ||
    typeName.startsWith("org.mycustomlib.")) {
    return true;
}
```

## Testing

To verify the filtering works correctly:

1. Run the coupling graph analysis on your project
2. Check the node count - it should only include your project classes
3. Export to DOT format and visualize - you should see only internal dependencies
4. Compare with previous results - external library references should be gone

## Notes

- This filtering happens automatically - no configuration needed
- If you specify a root package, filtering becomes even more strict
- External library classes used in method signatures are still counted as coupling sources, but the external classes themselves don't appear as nodes
- This is the recommended mode for refactoring analysis
