#!/bin/bash

# Script to remove comments from all Java files

find src/main/java -name "*.java" -type f | while read -r file; do
    echo "Processing: $file"
    
    # Create a temporary file
    temp_file="${file}.tmp"
    
    # Remove comments using sed:
    # 1. Remove single-line comments (//...)
    # 2. Remove multi-line comments (/* ... */)
    # 3. Remove JavaDoc comments (/** ... */)
    
    # Use a Python script for more reliable comment removal
    python3 << 'PYTHON_SCRIPT' "$file" "$temp_file"
import sys
import re

input_file = sys.argv[1]
output_file = sys.argv[2]

with open(input_file, 'r', encoding='utf-8') as f:
    content = f.read()

# Remove multi-line comments (/** ... */ and /* ... */)
content = re.sub(r'/\*\*.*?\*/', '', content, flags=re.DOTALL)
content = re.sub(r'/\*.*?\*/', '', content, flags=re.DOTALL)

# Remove single-line comments (// ...)
content = re.sub(r'//.*?$', '', content, flags=re.MULTILINE)

# Remove excess blank lines (keep max 2 consecutive blank lines)
content = re.sub(r'\n\s*\n\s*\n+', '\n\n', content)

with open(output_file, 'w', encoding='utf-8') as f:
    f.write(content)
PYTHON_SCRIPT

    # Replace original file with the cleaned version
    if [ -f "$temp_file" ]; then
        mv "$temp_file" "$file"
        echo "  ✓ Cleaned: $file"
    fi
done

echo ""
echo "All Java files have been cleaned of comments."
