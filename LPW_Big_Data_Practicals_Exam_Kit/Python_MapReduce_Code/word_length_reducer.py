#!/usr/bin/env python3
"""Groups words of equal length into a comma-separated list."""
import sys
current = None
words = []
for line in sys.stdin:
    line = line.rstrip("\n")
    if not line:
        continue
    key, word = line.split("\t", 1)
    if key == current:
        words.append(word)
    else:
        if current is not None:
            print(f"{current}\t{', '.join(words)}")
        current, words = key, [word]
if current is not None:
    print(f"{current}\t{', '.join(words)}")
