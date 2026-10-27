import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class LinearRegression {

    // ---------------- MAPPER ----------------

    public static class RegressionMapper
            extends Mapper<LongWritable, Text, Text, Text> {

        private Text outputKey = new Text("regression");

        public void map(
                LongWritable key,
                Text value,
                Context context)
                throws IOException, InterruptedException {

            String line = value.toString().trim();

            // Skip CSV header
            if (line.startsWith("area")) {
                return;
            }

            String[] fields = line.split(",");

            if (fields.length != 2) {
                return;
            }

            try {

                double x = Double.parseDouble(fields[0]);
                double y = Double.parseDouble(fields[1]);

                double x2 = x * x;
                double xy = x * y;

                /*
                 * Send:
                 * n, x, y, x², xy
                 */

                String result =
                        "1," +
                        x + "," +
                        y + "," +
                        x2 + "," +
                        xy;

                context.write(
                        outputKey,
                        new Text(result)
                );

            } catch (NumberFormatException e) {
                // Ignore invalid records
            }
        }
    }


    // ---------------- REDUCER ----------------

    public static class RegressionReducer
            extends Reducer<Text, Text, Text, DoubleWritable> {

        public void reduce(
                Text key,
                Iterable<Text> values,
                Context context)
                throws IOException, InterruptedException {

            double n = 0;
            double sumX = 0;
            double sumY = 0;
            double sumX2 = 0;
            double sumXY = 0;

            // Aggregate mapper output

            for (Text value : values) {

                String[] data =
                        value.toString().split(",");

                n += Double.parseDouble(data[0]);
                sumX += Double.parseDouble(data[1]);
                sumY += Double.parseDouble(data[2]);
                sumX2 += Double.parseDouble(data[3]);
                sumXY += Double.parseDouble(data[4]);
            }

            // Calculate slope

            double denominator =
                    (n * sumX2) -
                    (sumX * sumX);

            double slope =
                    ((n * sumXY) -
                    (sumX * sumY))
                    / denominator;

            // Calculate intercept

            double intercept =
                    (sumY - slope * sumX) / n;

            // Prediction

            double testArea = 5000;

            double prediction =
                    slope * testArea + intercept;

            // Output

            context.write(
                    new Text("Number of records"),
                    new DoubleWritable(n)
            );

            context.write(
                    new Text("Slope"),
                    new DoubleWritable(slope)
            );

            context.write(
                    new Text("Intercept"),
                    new DoubleWritable(intercept)
            );

            context.write(
                    new Text("Prediction for area 5000"),
                    new DoubleWritable(prediction)
            );
        }
    }


    // ---------------- DRIVER ----------------

    public static void main(String[] args)
            throws Exception {

        if (args.length != 2) {

            System.err.println(
                    "Usage: LinearRegression <input> <output>"
            );

            System.exit(-1);
        }

        Configuration conf =
                new Configuration();

        Job job =
                Job.getInstance(
                        conf,
                        "Linear Regression"
                );

        job.setJarByClass(
                LinearRegression.class
        );

        job.setMapperClass(
                RegressionMapper.class
        );

        job.setReducerClass(
                RegressionReducer.class
        );

        job.setMapOutputKeyClass(
                Text.class
        );

        job.setMapOutputValueClass(
                Text.class
        );

        job.setOutputKeyClass(
                Text.class
        );

        job.setOutputValueClass(
                DoubleWritable.class
        );

        FileInputFormat.addInputPath(
                job,
                new Path(args[0])
        );

        FileOutputFormat.setOutputPath(
                job,
                new Path(args[1])
        );

        System.exit(
                job.waitForCompletion(true)
                        ? 0 : 1
        );
    }
}