# Quick Start Guide - Java Refactoring & Analysis Tool

## 🚀 Getting Started

### Launch the Application
```bash
# Using Maven
mvn exec:java -Dexec.mainClass="fr.jeremyhurel.Main"

# Or using the run script
./run.sh
```

---

## 📋 Main Features

### 📐 1. Class Diagram
**What it does:** Generates UML class diagrams from your Java code

**Use when you want to:**
- Visualize class relationships
- Understand inheritance hierarchies
- See composition and dependencies
- Export to PlantUML for documentation

**Quick steps:**
1. Select "📐 Class Diagram"
2. Enter project path (default: `./src/main/java`)
3. Choose export format (DOT or PlantUML)

---

### 🔗 2. Call Graph
**What it does:** Visualizes method call relationships

**Use when you want to:**
- Trace method call chains
- Identify call dependencies
- Understand program flow
- Find deeply nested calls

**Quick steps:**
1. Select "🔗 Call Graph"
2. Enter project path
3. Optionally specify a target class
4. Export to DOT format

---

### 🔄 3. Coupling Graph
**What it does:** Analyzes and measures class coupling strength

**Use when you want to:**
- Identify tightly coupled classes
- Measure coupling coefficients
- Find refactoring opportunities
- Create weighted dependency graphs

**Quick steps:**
1. Select "🔄 Coupling Graph"
2. Enter project path
3. View coupling matrix
4. Export results

---

### 📦 4. Module Extraction ⭐ (Most Popular)
**What it does:** Automatically identifies cohesive modules from your codebase

**Use when you want to:**
- Reorganize monolithic code
- Identify natural module boundaries
- Prepare for microservices migration
- Improve code organization

#### Choose Your Strategy:

**🤖 Automatic (Elbow Method)**
- Best for: First-time users, quick analysis
- How it works: Smart detection using statistical analysis
- No configuration needed

**🎯 Fixed Number of Modules**
- Best for: When you know desired module count
- How it works: Creates exactly N modules
- Example: `5` modules
- Recommended: 3-10 for small projects, 10-20 for large

**📊 Coupling Threshold**
- Best for: Quality-focused extraction
- How it works: Stops splitting at threshold
- Parameter guide:
  - `0.01-0.05`: Many small, loosely coupled modules
  - `0.05-0.10`: Balanced approach ⭐ Recommended
  - `0.10+`: Fewer, tightly coupled modules

**⚖️ Combined (Fixed + Threshold)** 🆕
- Best for: Controlled extraction with quality constraints
- How it works: Creates up to N modules, stops at threshold
- Example: Max 10 modules, threshold 0.05
- Perfect balance of control and quality

**Quick steps:**
1. Select "📦 Module Extraction"
2. Enter project path: `./src/main/java`
3. Choose scope: "🌐 All Packages" (recommended)
4. Select strategy (try Automatic first!)
5. View results with module breakdown
6. Export to text or PlantUML

---

### 📈 5. Statistics
**What it does:** Displays comprehensive code metrics

**Use when you want to:**
- Get overview of codebase size
- Count classes and methods
- View coupling statistics
- Analyze module cohesion

**Quick steps:**
1. Select "📈 Statistics"
2. Enter project path
3. View metrics summary

---

## 💡 Pro Tips

### For Best Results:

1. **Start with Clean Build**
   ```bash
   mvn clean compile
   ```

2. **Use Default Paths**
   - For most Java projects: `./src/main/java`
   - For Maven: `./src/main/java`
   - For Gradle: `./src/main/java`

3. **Module Extraction Strategy Selection**
   ```
   First time?           → Use 🤖 Automatic
   Know module count?    → Use 🎯 Fixed
   Quality focused?      → Use 📊 Threshold
   Want both?            → Use ⚖️ Combined
   ```

4. **Export Your Results**
   - Always export for documentation
   - PlantUML files can be visualized at: https://www.plantuml.com/plantuml/uml/
   - Save text files for comparison over time

---

## 🎯 Common Workflows

### Workflow 1: Quick Analysis
```
1. Launch app
2. Select 📈 Statistics
3. Enter: ./src/main/java
4. Review metrics
```

### Workflow 2: Module Refactoring
```
1. Select 📦 Module Extraction
2. Path: ./src/main/java
3. Scope: All Packages
4. Strategy: 🤖 Automatic
5. Export both formats
6. Review module breakdown
```

### Workflow 3: Detailed Study
```
1. Generate 🔄 Coupling Graph
2. Generate 📐 Class Diagram
3. Run 📦 Module Extraction (⚖️ Combined)
4. Export all results
5. Compare outputs
```

---

## 📊 Understanding Results

### Module Extraction Output
```
📦 Total Modules: 8
🔗 Average Cohesion: 0.0067

Module Distribution:
  • Module 1: 4 classes (cohesion: 0.010)
  • Module 2: 2 classes (cohesion: 0.003)
  ...
```

**What to look for:**
- **Module count**: Aim for 5-15 modules in medium projects
- **Cohesion**: Higher is better (>0.01 is good)
- **Class distribution**: Balanced sizes are ideal

### Cohesion Values Guide
```
> 0.05  - Excellent cohesion
0.02-0.05 - Good cohesion
0.01-0.02 - Acceptable
< 0.01  - Needs improvement
```

---

## 🔧 Troubleshooting

### Issue: No classes found
**Solution:** Check your path
```
✅ Correct: ./src/main/java
❌ Wrong: ./src
```

### Issue: Too many modules
**Solutions:**
- Use higher threshold (e.g., 0.1 instead of 0.05)
- Use Fixed strategy with lower count
- Use Combined: fewer max modules + higher threshold

### Issue: Too few modules
**Solutions:**
- Use lower threshold (e.g., 0.03 instead of 0.05)
- Use Fixed strategy with higher count
- Check if project is too small (<10 classes)

---

## 📚 Additional Resources

### Export Formats

**PlantUML (.puml)**
- Visualize at: https://www.plantuml.com/plantuml/uml/
- Can be integrated into docs
- Version control friendly

**DOT (.dot)**
- Visualize with Graphviz
- Install: `sudo apt-get install graphviz`
- View: `dot -Tpng file.dot -o output.png`

**Text (.txt)**
- Simple, human-readable
- Good for version control
- Easy to parse programmatically

---

## ⚡ Keyboard Shortcuts

```
Navigation:
  ↑/↓    - Navigate menu items
  Enter  - Select option
  Esc    - Cancel/Back
  Tab    - Next field

In Dialogs:
  Enter  - Confirm
  Esc    - Cancel
```

---

## 🎓 Learning Path

### Beginner
1. Start with Statistics
2. Try Automatic module extraction
3. Export to PlantUML and view online

### Intermediate
4. Compare different extraction strategies
5. Experiment with threshold values
6. Generate coupling graphs

### Advanced
7. Use Combined strategy for precise control
8. Analyze cohesion trends
9. Integrate exports into CI/CD

---

## 📝 Example Use Cases

### Use Case 1: Preparing for Microservices
```
Goal: Split monolith into microservices

1. Run Module Extraction (Automatic)
2. Analyze module boundaries
3. Use modules as service candidates
4. Export PlantUML for architecture docs
```

### Use Case 2: Code Review
```
Goal: Identify refactoring opportunities

1. Generate Coupling Graph
2. Find highly coupled classes
3. Run Module Extraction
4. Compare actual vs. ideal organization
```

### Use Case 3: Documentation
```
Goal: Create project documentation

1. Generate Class Diagram
2. Extract modules with descriptions
3. Export all to PlantUML
4. Include in project README
```

---

## 🆘 Getting Help

1. **In-App Help**: Press "❓ Help" in main menu
2. **Documentation**: See `UI_IMPROVEMENTS.md`
3. **Strategy Guide**: See `STRATEGY_MERGE.md`
4. **GitHub**: https://github.com/DrHurel/java-refactoring-tool

---

## 🎉 Quick Win

**Want to see results in 30 seconds?**

```bash
1. Launch: mvn exec:java -Dexec.mainClass="fr.jeremyhurel.Main"
2. Select: 📦 Module Extraction
3. Path: ./src/main/java
4. Scope: All Packages
5. Strategy: 🤖 Automatic
6. Export: 🎨 PlantUML
7. Open: https://www.plantuml.com/plantuml/uml/
8. Upload your .puml file
9. See your modules! 🎊
```

---

Made with ❤️ for better code organization
