import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
 
public class PhraseFrequency extends Configured implements Tool {
 
    // Mapper Class (unchanged from Practical 6)
    // Emits (word length, word) for every word in the line
    public static class LengthMapper
            extends Mapper<Object, Text, IntWritable, Text> {
 
        private IntWritable lengthKey = new IntWritable();
        private Text word = new Text();
 
        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {
 
            String[] tokens = value.toString().trim().split("\\s+");
 
            for (String token : tokens) {
                if (token.length() > 0) {
                    lengthKey.set(token.length());
                    word.set(token);
                    context.write(lengthKey, word);
                }
            }
        }
    }
 
    // Combiner / Reducer Class (shared logic)
    // Joins every word that shares a length into one comma-separated value.
    // Safe as a Combiner because string concatenation is associative:
    // partial groups formed on each Mapper can be safely merged further
    // at the Reducer without changing which words end up under which key.
    public static class LengthReducer
            extends Reducer<IntWritable, Text, IntWritable, Text> {
 
        public void reduce(IntWritable key, Iterable<Text> values,
                Context context)
                throws IOException, InterruptedException {
 
            StringBuilder grouped = new StringBuilder();
            boolean first = true;
 
            for (Text val : values) {
                if (!first) {
                    grouped.append(", ");
                }
                grouped.append(val.toString());
                first = false;
            }
 
            context.write(key, new Text(grouped.toString()));
        }
    }
 
    // Custom Partitioner
    // Routes every word-length key to a reducer using key mod numPartitions,
    // so the same length always lands on the same reducer no matter how
    // many reducers are configured for the job.
    public static class LengthPartitioner extends Partitioner<IntWritable, Text> {
 
        @Override
        public int getPartition(IntWritable key, Text value, int numPartitions) {
            if (numPartitions <= 1) {
                return 0;
            }
            return Math.abs(key.get()) % numPartitions;
        }
    }
 
    // Driver Code (Tool/ToolRunner lets -D options set reducer count etc.
    // from the command line, e.g. -D mapreduce.job.reduces=2)
    @Override
    public int run(String[] args) throws Exception {
 
        Configuration conf = getConf();
        Job job = Job.getInstance(conf, "word length grouping with combiner and partitioner");
 
        job.setJarByClass(PhraseFrequency.class);
        job.setMapperClass(LengthMapper.class);
        job.setCombinerClass(LengthReducer.class);
        job.setPartitionerClass(LengthPartitioner.class);
        job.setReducerClass(LengthReducer.class);
        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Text.class);
 
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
 
        return job.waitForCompletion(true) ? 0 : 1;
    }
 
    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new Configuration(), new PhraseFrequency(), args);
        System.exit(exitCode);
    }
}

