PRACTICAL 7 — COMBINER AND CUSTOM PARTITIONER
This folder's PhraseFrequency.java is copied from the supplied pract-7.java. It contains:
- LengthMapper: emits (word length, word).
- LengthReducer: groups values by length and comma-joins them; also used as the Combiner.
- LengthPartitioner: chooses reducer using abs(key) % numberOfPartitions, with reducer 0 when only one reducer is configured.
- Tool/ToolRunner driver to parse generic Hadoop options.

Combiner:
- Performs local aggregation on mapper output before shuffle, reducing intermediate data transferred.
- Hadoop is allowed to run a combiner zero, one, or multiple times; it is not a guaranteed replacement for the reducer.
- Combiner logic must be safe for partial aggregation. For a production-safe associative representation, joining strings can be tricky if the reducer treats commas as delimiters and a word itself may contain commas. This lab's whitespace token input assumes tokens do not contain commas.

Partitioner:
- Decides which reducer receives a key.
- Same key must consistently route to the same reducer.
- With this custom rule, word length 2 goes to reducer (2 mod N), length 4 to (4 mod N), etc. Actual output file assignment depends on N and key.
