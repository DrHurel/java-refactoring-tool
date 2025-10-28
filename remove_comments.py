#!/usr/bin/env python3
import sys
import re
import os

def remove_comments(content):
    """Remove all comments from Java code."""
    
    # Remove multi-line JavaDoc comments (/** ... */)
    content = re.sub(r'/\*\*.*?\*/', '', content, flags=re.DOTALL)
    
    # Remove multi-line comments (/* ... */)
    content = re.sub(r'/\*.*?\*/', '', content, flags=re.DOTALL)
    
    # Remove single-line comments (// ...)
    content = re.sub(r'//.*?$', '', content, flags=re.MULTILINE)
    
    # Remove excess blank lines (keep max 1 consecutive blank line)
    content = re.sub(r'\n\s*\n\s*\n+', '\n\n', content)
    
    # Remove leading/trailing whitespace from lines
    lines = content.split('\n')
    lines = [line.rstrip() for line in lines]
    content = '\n'.join(lines)
    
    return content

def process_file(filepath):
    """Process a single Java file to remove comments."""
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            original_content = f.read()
        
        cleaned_content = remove_comments(original_content)
        
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(cleaned_content)
        
        print(f"  ✓ Cleaned: {filepath}")
        return True
    except Exception as e:
        print(f"  ✗ Error processing {filepath}: {e}")
        return False

def main():
    """Process all Java files in src/main/java."""
    java_files = []
    
    # Find all Java files
    for root, dirs, files in os.walk('src/main/java'):
        for file in files:
            if file.endswith('.java'):
                java_files.append(os.path.join(root, file))
    
    print(f"Found {len(java_files)} Java files to process.\n")
    
    success_count = 0
    for filepath in sorted(java_files):
        print(f"Processing: {filepath}")
        if process_file(filepath):
            success_count += 1
    
    print(f"\n✓ Successfully processed {success_count}/{len(java_files)} files.")

if __name__ == '__main__':
    main()
