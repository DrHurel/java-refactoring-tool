#!/bin/bash
# Wrapper script to run the Java Refactoring Tool
exec "$(dirname "$0")/Devtools/run.sh" "$@"
