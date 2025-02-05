package edu.ucr.cs.cs167.ashir025;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * Filter log file by response code
 */
public class Filter {
    public static void main(String[] args) throws Exception {
        String inputPath = args[0];
        String outputPath = args[1];
        String desiredResponse = args[2];

        Configuration conf = new Configuration();

        // TODO pass the desiredResponse code to the MapReduce program
        conf.set("resp_code",desiredResponse);

        Job job = Job.getInstance(conf, "filter");
        job.setJarByClass(Filter.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setNumReduceTasks(0);
        job.setInputFormatClass(TextInputFormat.class);
        Path input = new Path(inputPath);
        FileInputFormat.addInputPath(job, input);
        Path output = new Path(outputPath);
        FileOutputFormat.setOutputPath(job, output);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

    public static class TokenizerMapper extends
            Mapper<LongWritable, Text, NullWritable, Text> {

        protected String resp_code;

        @Override
        protected void setup(Context context)
                throws IOException, InterruptedException {
            super.setup(context);

            // TODO add additional setup to your map task, if needed.
            resp_code=context.getConfiguration().get("resp_code");
        }

        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            if (key.get() == 0) return; // Skip header line
            String[] parts = value.toString().split("\t");
            String responseCode = parts[5];
            // TODO Filter by response code
            //changed from "200"
            if (responseCode.equals(resp_code))
                context.write(NullWritable.get(), value);
        }
    }
}