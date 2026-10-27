import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * Practical 12 — MapReduce-Style Processing on Cassandra Restaurant Data
 *
 * Workflow:
 *   1. Export Cassandra table to a CSV file (see README / Run_P12.txt).
 *   2. Upload that file to HDFS.
 *   3. Run this MapReduce job on HDFS.
 *
 * Expected input line format (CSV exported from Cassandra COPY command):
 *   Ahmedabad,R001,Sample Dhaba,Gujarati,4.2
 *   Ahmedabad,R002,Sample Cafe,Cafe,4.0
 *   Surat,R003,Sample Thali House,Gujarati,4.4
 *
 * Columns: city, restaurant_id, restaurant_name, cuisine, rating
 *
 * Mapper key   = cuisine
 * Mapper value = 1
 * Reducer      = sum of counts per cuisine
 *
 * Expected output:
 *   Cafe       1
 *   Gujarati   2
 */
public class CassandraRestaurantMapReduce {

    // ------------------------------------------------------------------ //
    //  MAPPER                                                              //
    // ------------------------------------------------------------------ //
    public static class CuisineMapper
            extends Mapper<Object, Text, Text, IntWritable> {

        private static final IntWritable ONE = new IntWritable(1);
        private final Text cuisine = new Text();

        @Override
        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            String line = value.toString().trim();

            // Skip empty lines or header rows
            if (line.isEmpty() || line.startsWith("city")
                    || line.startsWith("#")) {
                return;
            }

            // CSV columns: city, restaurant_id, restaurant_name, cuisine, rating
            String[] fields = line.split(",");
            if (fields.length >= 4) {
                String cuisineValue = fields[3].trim();
                cuisine.set(cuisineValue);
                context.write(cuisine, ONE);
            }
        }
    }

    // ------------------------------------------------------------------ //
    //  REDUCER                                                             //
    // ------------------------------------------------------------------ //
    public static class CuisineReducer
            extends Reducer<Text, IntWritable, Text, IntWritable> {

        private final IntWritable result = new IntWritable();

        @Override
        public void reduce(Text key, Iterable<IntWritable> values,
                Context context)
                throws IOException, InterruptedException {

            int total = 0;
            for (IntWritable val : values) {
                total += val.get();
            }
            result.set(total);
            context.write(key, result);
        }
    }

    // ------------------------------------------------------------------ //
    //  DRIVER                                                              //
    // ------------------------------------------------------------------ //
    public static void main(String[] args) throws Exception {

        if (args.length < 2) {
            System.err.println(
                "Usage: CassandraRestaurantMapReduce <input_path> <output_path>");
            System.exit(1);
        }

        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf, "Cassandra Restaurant Cuisine Count");
        job.setJarByClass(CassandraRestaurantMapReduce.class);

        job.setMapperClass(CuisineMapper.class);
        job.setReducerClass(CuisineReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
