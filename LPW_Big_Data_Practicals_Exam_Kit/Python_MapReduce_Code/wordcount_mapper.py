#!/usr/bin/env python3
"""Hadoop Streaming mapper: emit (word, 1)."""
import sys
import re
for line in sys.stdin:
    for word in re.findall(r"[A-Za-z0-9_]+", line.lower()):
        print(f"{word}\t1")
