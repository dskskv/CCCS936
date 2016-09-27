package com.ds.kutchuni.WordCountDSKSKV;

import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * @author chetan306 
 * @version 1.0.0
 * 
 * Description: This class WordCountDriver has written to run mapreduce and to provide input and output
 * directory path, Provide Job Name, Provide Classname of Driver, Mapper class, Reducer class, output key and value class from Hadoop core API. 
 */
public class WordCountDriver 
{
    public static void main( String[] args )
    {
    	if (args.length < 2) {
    		   System.err.println("Input path or Ouput path is missing ! ");
    		  }

    		  try {
    		   Job job = Job.getInstance();
    		   job.setJobName("WordCount-DSLab-KSKV");

    		   // Set file input/output path
    		   FileInputFormat.addInputPath(job, new Path(args[0]));
    		   FileOutputFormat.setOutputPath(job, new Path(args[1]));

    		   // Set Driver Jar class name
    		   job.setJarByClass(WordCountDriver.class);

    		   // set Mapper and Reducer classs to driver job
    		   job.setMapperClass(WordCountMapper.class);
    		   job.setReducerClass(WordCountReducer.class);

    		   // set output key - value class name
    		   job.setOutputKeyClass(Text.class);
    		   job.setOutputValueClass(IntWritable.class);

    		   int returnValue = job.waitForCompletion(true) ? 0 : 1;
    		   System.out.println(job.isSuccessful());

    		   System.exit(returnValue);

    		  } catch (IOException | ClassNotFoundException | InterruptedException e) {
    		   e.printStackTrace();
    		  }
    }
}
