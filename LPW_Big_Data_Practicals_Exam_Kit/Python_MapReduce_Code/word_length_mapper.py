#!/usr/bin/env python3
"""Equivalent to supplied PhraseFrequency.java: group words by length."""
import sys
for line in sys.stdin:
    for word in line.strip().split():
        print(f"{len(word)}\t{word}")
