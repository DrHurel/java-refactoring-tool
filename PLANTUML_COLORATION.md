# PlantUML Class Diagram Coloration

## Overview

The PlantUML export now includes automatic color-coding to make class diagrams more visually appealing and easier to understand at a glance.

## Color Scheme

### Class Types

- **Interfaces** 🔵 `LightBlue`
  - Easy to identify interface contracts
  - Clearly distinguishable from concrete implementations

- **Abstract Classes** 🟡 `LightYellow`
  - Highlighted to show incomplete implementations
  - Shows classes that require extension

- **Concrete Classes** 🟢 `LightGreen`
  - Regular, instantiable classes
  - The most common type in most applications

### Packages

- **Package Background** ⚪ `WhiteSmoke`
  - Subtle background to group related classes
  - Gray border for clear separation
  - Bold font for package names

### Other Elements

- **Borders** ⚫ `Black`
  - Clear, consistent borders for all elements
  
- **Arrows** ⚫ `Black`
  - Relationship arrows remain clearly visible

## Implementation

### PlantUML Configuration

The exporter adds the following PlantUML skinparam configuration:

```plantuml
skinparam class {
  BackgroundColor<<interface>> LightBlue
  BackgroundColor<<abstract>> LightYellow
  BackgroundColor<<concrete>> LightGreen
  BorderColor Black
  ArrowColor Black
}
skinparam package {
  BackgroundColor WhiteSmoke
  BorderColor Gray
  FontStyle bold
}
```

### Stereotype Assignment

Each class is automatically tagged with the appropriate stereotype:

- `interface MyInterface <<interface>> { }`
- `abstract class MyAbstractClass <<abstract>> { }`
- `class MyConcreteClass <<concrete>> { }`

## Benefits

1. **Quick Recognition**: Instantly identify class types by color
2. **Better Readability**: Visual hierarchy makes complex diagrams easier to understand
3. **Professional Appearance**: Polished, publication-ready diagrams
4. **Consistency**: Uniform color scheme across all generated diagrams
5. **PlantUML Standard**: Uses standard PlantUML stereotypes and skinparam

## Example

### Before (No Color)
All classes appear with the same white background, making it difficult to distinguish types quickly.

### After (With Color)
```
- Interfaces stand out in light blue
- Abstract classes are highlighted in yellow
- Concrete classes appear in green
- Packages have a subtle gray background
```

## Customization

If you want to customize the colors, you can modify the `ClassDiagramExporter.exportToPlantUML()` method:

```java
writer.write("skinparam class {\n");
writer.write("  BackgroundColor<<interface>> YourColor\n");
writer.write("  BackgroundColor<<abstract>> YourColor\n");
writer.write("  BackgroundColor<<concrete>> YourColor\n");
// ... more customization
```

Available PlantUML colors include:
- Standard colors: Red, Blue, Green, Yellow, Orange, Purple, Pink, etc.
- Light variants: LightBlue, LightGreen, LightYellow, LightCoral, etc.
- Other: WhiteSmoke, AliceBlue, Lavender, Beige, etc.
- Hex codes: #RRGGBB format

## Viewing the Diagrams

To view the colored diagrams:

1. Generate a class diagram with PlantUML export
2. Open the `.puml` file in:
   - PlantUML online editor (https://www.plantuml.com/plantuml)
   - VS Code with PlantUML extension
   - IntelliJ IDEA with PlantUML plugin
   - Any PlantUML renderer

The colors will be automatically rendered according to the skinparam configuration.
