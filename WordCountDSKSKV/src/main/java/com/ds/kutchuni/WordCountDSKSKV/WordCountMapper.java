package com.ds.kutchuni.WordCountDSKSKV;

import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
/**
 * @author chetan306 
 * @version 1.0.0
 * 
 * Description: This class WordCountMapper has written for tokenize every words from the file and organize by individual first occurence value as a 1. 
 */
public class WordCountMapper extends Mapper<Object, Text, Text, IntWritable> {

	@Override
	 protected void map(Object key, Text value, Mapper<Object, Text, Text, IntWritable>.Context context)
	   throws IOException, InterruptedException {

	  // getting the string tokens from the value from text file.
	  StringTokenizer stringTokens = new StringTokenizer(value.toString());

	  // Iterating with string tokenizer object
	  while (stringTokens.hasMoreTokens()) {

	   // The word available at input file provided
	   String word = stringTokens.nextToken();

	   // Writing the individual word into context as a first occurrence 
	   // For Example: Apple 1, Mango 1, Banana 1
	   context.write(new Text(word), new IntWritable(1));
	  }

	 }
	 @Override
	 protected void setup(Mapper<Object, Text, Text, IntWritable>.Context context)
	   throws IOException, InterruptedException {
	  System.out.println("Executed once at start.");
	 }
	 @Override
	 protected void cleanup(Mapper<Object, Text, Text, IntWritable>.Context context)
	   throws IOException, InterruptedException {
	  System.out.println("Executed once at end.");
	 }
	
}
