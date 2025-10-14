# Coupling Graph - External Library Filtering Update

## Summary

Successfully updated the Coupling Graph feature to filter out external library classes, focusing the analysis on only your project's internal classes.

## Changes Made

### 1. Enhanced CouplingGraphScanner.java
- **Renamed Method**: `isPrimitiveOrJavaLang()` → delegates to `isExternalLibraryClass()`
- **New Method**: `isExternalLibraryClass(String typeName)`
  - Filters Java standard library (`java.*`, `javax.*`, `jdk.*`, `sun.*`, `com.sun.*`)
  - Filters common third-party libraries (Spring, Hibernate, Apache, Google, Jackson, JUnit, Mockito, SLF4J, Logback, Eclipse, Lanterna, Spoon)
  - Filters primitive types
  - Respects `rootPackage` filter when specified (only includes classes in that package)
- **New Method**: `isPrimitiveType(String typeName)`
  - Separate check for primitive types for better code organization

### 2. Enhanced CouplingGraph.java
- **New Method**: `removeOrphanedNodes()`
  - Removes nodes that were referenced but not part of the analyzed codebase
  - Cleans up external library classes that might have been added as nodes

### 3. Enhanced CouplingGraphProcessor.java
- Added call to `couplingGraph.removeOrphanedNodes()` after model processing
- Ensures final graph contains only internal project classes

## Benefits

✅ **Cleaner Visualizations** - Only your project classes appear in the graph  
✅ **More Accurate Metrics** - Coupling counts reflect internal dependencies only  
✅ **Better Performance** - Fewer nodes to process and export  
✅ **Focused Analysis** - See only the coupling you can refactor  
✅ **Automatic Filtering** - No configuration needed  

## Build Status

✅ **Compilation**: Successful  
✅ **Packaging**: Successful  
✅ **Version**: 1.1-SNAPSHOT  

## Testing

To test the filtering:

```bash
# Build the project
mvn package

# Run the application
java -jar target/java-refactoring-tool-1.1-SNAPSHOT-jar-with-dependencies.jar

# Select "3. Coupling Graph"
# Analyze your project (e.g., ./src/main/java)
# Optionally specify a root package (e.g., fr.jeremyhurel)
# Export to DOT or JSON format
# Verify only internal classes appear
```

## Example Output

### Before Filtering
```
Classes found: 150+
Includes: Your classes + java.util.* + org.springframework.* + spoon.* + etc.
```

### After Filtering
```
Classes found: ~25
Includes: Only fr.jeremyhurel.* classes
```

## Documentation

Created comprehensive documentation:
- `EXTERNAL_LIBRARY_FILTERING.md` - Detailed explanation of filtering mechanism
- `COUPLING_GRAPH_FEATURE.md` - Feature overview (if exists)
- `COUPLING_GRAPH_TESTING.md` - Testing guide (if exists)

## Notes

- The filtering is conservative - if unsure, a class is included rather than excluded
- When `rootPackage` is specified, filtering becomes even stricter
- You can customize the filter by modifying `isExternalLibraryClass()` in `CouplingGraphScanner.java`
- The implementation follows the same architectural patterns as other features in the tool

## Files Modified

1. `/src/main/java/fr/jeremyhurel/scanners/CouplingGraphScanner.java`
2. `/src/main/java/fr/jeremyhurel/models/CouplingGraph.java`
3. `/src/main/java/fr/jeremyhurel/processors/CouplingGraphProcessor.java`

## Files Created

1. `/EXTERNAL_LIBRARY_FILTERING.md`
