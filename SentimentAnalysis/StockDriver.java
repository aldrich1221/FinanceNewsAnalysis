package analyze;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
 
import java.io.FileReader;
import java.util.Iterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StockDriver {
	private static final Log LOG = LogFactory.getLog(StockDriver.class);

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		
		Configuration conf = new Configuration();
	    Job job = Job.getInstance(conf, "NewsSentimentAnalysis");
	   
	    job.setJarByClass(StockDriver.class);
	    job.setMapperClass(StockMapper.class);
	    job.setCombinerClass(StockReducer.class);
	    job.setReducerClass(StockReducer.class);
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(IntWritable.class);
	    job.addFileToClassPath(new Path("/MyJAR/commons-lang3.jar"));
		job.addFileToClassPath(new Path("/MyJAR/json-simple-1.1.1.jar"));
		job.addFileToClassPath(new Path("/MyJAR/commons-logging-1.2.1.1.jar"));



		try{

			job.addCacheFile(new URI("/cache/negativewords.txt"));
			job.addCacheFile(new URI("/cache/positivewords.txt"));
			job.addCacheFile(new URI("/cache/uncertaintywords.txt"));

			}catch(Exception e)
			{
				System.out.println("file not added my dear");
				System.exit(1);
		}


	    FileInputFormat.addInputPath(job, new Path(args[0]));
	    FileOutputFormat.setOutputPath(job, new Path(args[1]));
	    System.exit(job.waitForCompletion(true) ? 0 : 1);

	};

	
}
