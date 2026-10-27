PRACTICAL 6 — MAPPER/REDUCER ANALYSIS
The course list says to analyse the impact of different numbers of mappers and reducers on the same definition as Practical 5 and prepare a conclusive report.

Mapper input splits:
- Hadoop creates input splits; generally each split is processed by a map task.
- Number of map tasks depends on input size, file/input format, block/split settings, and number of input files—not simply on a line count.
- For a small single-node lab dataset, changing split size or input file layout can change mapper count.

Reducer count:
- 1 reducer: all keys go to one reduce task and normally produce part-r-00000.
- 2 reducers: keys are partitioned across reducers; outputs can include part-r-00000 and part-r-00001.
- More reducers can increase parallelism for large data but add task/scheduling overhead. On a small single-node dataset, more reducers may be slower.
- A key is sent to one reducer by the partitioner; all values for the same key must meet at the same reducer.

Suggested experiment table to fill from actual job logs:
Reducers | Map input records | Map output records | Reduce input groups | Reduce output records | elapsed time | conclusion
1        | record actual     | record actual      | record actual       | record actual        | record actual | baseline
2        | record actual     | record actual      | record actual       | record actual        | record actual | compare
3        | record actual     | record actual      | record actual       | record actual        | record actual | compare

Do not invent timings. Record actual values from job console, JobHistory UI, or counters.
