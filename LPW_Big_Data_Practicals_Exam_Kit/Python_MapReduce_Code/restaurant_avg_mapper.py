#!/usr/bin/env python3
"""CSV mapper: input columns restaurant,city,rating; emits city,rating. Skips header."""
import csv, sys
reader = csv.reader(sys.stdin)
for row in reader:
    if len(row) < 3 or row[0].strip().lower() == "restaurant":
        continue
    try:
        city = row[1].strip()
        rating = float(row[2])
        print(f"{city}\t{rating}")
    except ValueError:
        continue
