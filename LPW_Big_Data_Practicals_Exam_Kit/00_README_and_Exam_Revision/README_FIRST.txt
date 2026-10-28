BIG DATA SYSTEMS / LPW PRACTICAL EXAM KIT
Prepared from the supplied course practical list, Java files, HDFS command notes, and practical reports.

WHAT IS INCLUDED
- Practical 5: MapReduce phrase/word-length grouping and record search.
- Practical 6: Mapper/Reducer flow, output, counters, and how to compare mapper/reducer counts.
- Practical 7: Combiner + custom Partitioner version of the word-length grouping program.
- Practical 8: Cassandra setup/query/CRUD cheat sheet.
- Practical 10: PySpark MLlib linear regression example.
- Practical 11: MongoDB MapReduce example plus modern aggregation alternative.
- Practical 12: Cassandra dataset processing / MapReduce-style aggregation notes.
- Separate MongoDB query file, Cassandra CQL file, Hadoop commands, and Windows run guide.
- WordCount, SearchRecords, and LinearRegression Java examples.
- Original supplied reference files in reference_material.

IMPORTANT
1. Java Hadoop examples are source files. They are not executed by this ZIP; actual output depends on your Hadoop installation, input data, and configuration. Expected/sample outputs are labeled as examples.
2. The supplied course outline lists Practical 5 as phrase frequency or record search; the supplied PhraseFrequency.java actually groups words by word length. Both supplied implementations are retained and explained.
3. MongoDB's legacy db.collection.mapReduce() was deprecated in MongoDB 5.0. The file includes the legacy practical syntax and a modern aggregation-pipeline alternative.
4. Cassandra does not have a built-in CQL MAPREDUCE command. Practical 12 is commonly demonstrated using Spark/Hadoop integration or application-side aggregation over Cassandra data. Check the instructor's expected environment.
5. Windows paths in examples use D:\hadoop-3.3.6 and D:\BIG DATA ANALYSIS as example paths. Replace them with your actual paths.
6. For the most likely oral questions, revise: Mapper, Reducer, key/value pairs, shuffle and sort, Combiner, Partitioner, number of reducers, HDFS vs local FS, NameNode/DataNode, YARN, replication, MongoDB CRUD, Cassandra primary key/partition key, and Spark MLlib.

QUICK ORDER FOR EXAM
A. Start Hadoop → check jps → upload input to HDFS.
B. Compile Java using `hadoop classpath` → package JAR → run `hadoop jar`.
C. Read output using `hdfs dfs -cat`.
D. Explain Mapper output → shuffle/sort → Reducer output.
E. For P6/P7, compare jobs with 1, 2, and 3 reducers; use Hadoop counters/UI logs rather than claiming more reducers always make a single-node job faster.
