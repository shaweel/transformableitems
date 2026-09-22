#!/bin/bash

for branch in $(git for-each-ref --format='%(refname:short)' refs/heads/); do
    if git grep -q '297' "$branch" -- '**/ModKeybinds.java' 2>/dev/null; then
        echo "$branch"
    fi
done
