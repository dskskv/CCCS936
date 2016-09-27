package com.ds.kutchuni.WordCountDSKSKV;
import java.io.IOException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * @author chetan306 
 * @version 1.0.0
 * 
 * Description: This class WordCountReducer has written to aggregate key,value pairs produced by WordCountMapper in organized form. It will provide number of individual words occurrence in the provided file.   
 */
public class WordCountReducer extends Reducer<Text, IntWritable, Text,IntWritable> {
	 @Override
	 protected void reduce(Text key, Iterable<IntWritable> values,
	   Reducer<Text, IntWritable, Text, IntWritable>.Context context) throws IOException, InterruptedException {

	  int numberOfFrequency = 0;
	  for (IntWritable occurance : values) {
	   numberOfFrequency += occurance.get();
	  }
	  context.write(key,new IntWritable(numberOfFrequency));
	 }
}
