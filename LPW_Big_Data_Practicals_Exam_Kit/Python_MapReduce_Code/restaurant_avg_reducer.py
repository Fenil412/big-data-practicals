#!/usr/bin/env python3
"""Calculates average restaurant rating per city; input sorted by city."""
import sys
city_now = None
rating_sum = 0.0
count = 0
for line in sys.stdin:
    line = line.rstrip("\n")
    if not line:
        continue
    city, rating = line.split("\t", 1)
    rating = float(rating)
    if city == city_now:
        rating_sum += rating
        count += 1
    else:
        if city_now is not None:
            print(f"{city_now}\t{rating_sum / count:.2f}")
        city_now, rating_sum, count = city, rating, 1
if city_now is not None:
    print(f"{city_now}\t{rating_sum / count:.2f}")
