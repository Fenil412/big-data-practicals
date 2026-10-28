#!/usr/bin/env python3
"""Hadoop Streaming reducer: sum counts for each word."""
import sys
current = None
count = 0
for line in sys.stdin:
    line = line.rstrip("\n")
    if not line:
        continue
    word, n = line.split("\t", 1)
    n = int(n)
    if word == current:
        count += n
    else:
        if current is not None:
            print(f"{current}\t{count}")
        current, count = word, n
if current is not None:
    print(f"{current}\t{count}")
