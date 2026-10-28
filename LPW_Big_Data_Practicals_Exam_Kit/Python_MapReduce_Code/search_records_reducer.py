#!/usr/bin/env python3
"""Counts identical matching records."""
import sys
current = None
count = 0
for line in sys.stdin:
    line = line.rstrip("\n")
    if not line:
        continue
    record, n = line.rsplit("\t", 1)
    n = int(n)
    if record == current:
        count += n
    else:
        if current is not None:
            print(f"{current}\t{count}")
        current, count = record, n
if current is not None:
    print(f"{current}\t{count}")
