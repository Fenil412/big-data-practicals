PRACTICAL 5 — MAPREDUCE ANALYTICS
Course-list options: (a) find phrase frequency from a dataset, or (b) search records matching a criterion.
The supplied PhraseFrequency.java is word-length grouping, not literal phrase counting. Use SearchRecords.java for criterion-based record search. If examiner specifically asks phrase frequency, use WordCount.java for word frequency or ask whether phrase means exact multiword phrase.

PhraseFrequency.java logic
Mapper: split each line on whitespace; emit (token.length(), token).
Shuffle and sort: all values with the same integer word-length key are grouped.
Reducer: join words for a length into a comma-separated value.

Sample input:
my name is sart

Sample output (ordering of values may vary):
2    my, is
4    name, sart

Run instructions: see ../Hadoop_Commands_and_Setup/Windows_Hadoop_Java_Run_Guide.txt.
