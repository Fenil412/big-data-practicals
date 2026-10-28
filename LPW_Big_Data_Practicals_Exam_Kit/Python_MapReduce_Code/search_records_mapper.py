#!/usr/bin/env python3
"""Search records containing SEARCH_TERM (case-insensitive); emit record,1."""
import os, sys
term = os.environ.get("SEARCH_TERM", "")
if not term:
    print("ERROR: set SEARCH_TERM environment variable", file=sys.stderr)
    sys.exit(2)
for line in sys.stdin:
    record = line.rstrip("\n")
    if term.casefold() in record.casefold():
        print(f"{record}\t1")
