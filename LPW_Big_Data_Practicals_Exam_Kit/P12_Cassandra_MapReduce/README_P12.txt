PRACTICAL 12 — MAPREDUCE-STYLE PROCESSING WITH CASSANDRA RESTAURANT DATA
Cassandra CQL does not provide a native `MAPREDUCE` statement. A practical implementation typically reads Cassandra rows using Spark's Cassandra connector (or exports rows to HDFS and runs Hadoop MapReduce), then performs distributed grouping.

MapReduce idea for restaurant cuisine counts:
- Mapper: for each restaurant row, emit (cuisine, 1).
- Reducer: sum the counts for each cuisine.
- Output: cuisine -> number of restaurants.

CQL table and sample data are in ../Cassandra_Queries/Cassandra_Queries.cql.

Exam explanation:
1. Cassandra stores rows in partitioned tables; it is a database, not a MapReduce execution engine.
2. A Spark Cassandra connector can read the table into a Spark DataFrame/RDD.
3. Group by cuisine and count, or export the data to CSV and use the Hadoop MapReduce WordCount-style pattern.
4. Mention that the connector version must match Cassandra/Spark/Scala versions.

Illustrative PySpark logic after a Spark Cassandra connector has loaded the table into DataFrame `restaurants`:
    restaurants.groupBy("cuisine").count().show()

The exact SparkSession connector configuration depends on the lab's installed connector version; do not copy an arbitrary connector package into a different Spark/Scala version.
