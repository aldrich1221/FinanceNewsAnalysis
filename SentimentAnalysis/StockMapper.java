package analyze;


import java.io.*;
import java.util.*;
import java.net.URI;
import java.util.Arrays;

import java.util.Arrays.*;
import static java.util.Arrays.*;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.apache.hadoop.io.IntWritable;

import org.apache.hadoop.io.IntWritable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StockMapper extends Mapper<Object, Text, Text, IntWritable>{

    JSONParser parser = null;
	Map<String,String> dictionary = null;
	ArrayList<String> positiveWords = null;
	ArrayList<String> negativeWords = null;
	ArrayList<String> uncertaintyWords = null;
	private static final Log LOG = LogFactory.getLog(StockMapper.class);
	
    private final static IntWritable one = new IntWritable(1);
    private Text SentimentCount=new Text();
    private Text negativeWord = new Text();
    private Text positiveWord = new Text();
    private Text uncertaintyWord = new Text();
    private Text litigiousWord = new Text();
    private Text strongModalWord = new Text();
    private Text weakModalWord = new Text();
    private Text constrainingWord = new Text();
    

    @Override
	protected void setup(Context context)throws IOException,InterruptedException
	{
		parser = new JSONParser();
		
		positiveWords = new ArrayList<String>();
		negativeWords = new ArrayList<String>();
		uncertaintyWords = new ArrayList<String>();

		URI[] cacheFiles = context.getCacheFiles();
		LOG.info("My message");
		if (cacheFiles != null && cacheFiles.length > 0)
		  {
		    try
		    {   
		    	String line ="";
		        FileSystem fs = FileSystem.get(context.getConfiguration());
		        Path negativepath = new Path(cacheFiles[3].toString());
		        Path positivepath = new Path(cacheFiles[4].toString());
		        Path uncertaintypath = new Path(cacheFiles[5].toString());
		        
		        LOG.info(cacheFiles);
		        LOG.info(positivepath);
		        LOG.info(uncertaintypath );

		        BufferedReader negativeReader = new BufferedReader(new InputStreamReader(fs.open(negativepath),"UTF-8"));
		        BufferedReader positiveReader = new BufferedReader(new InputStreamReader(fs.open(positivepath),"UTF-8"));
		        BufferedReader uncertaintyReader = new BufferedReader(new InputStreamReader(fs.open(uncertaintypath),"UTF-8"));
		    
		    	 while((line = negativeReader.readLine())!=null)
		        {
					
					LOG.info(line);
		        	negativeWords.add(line.toUpperCase());
		        }
		        while((line = positiveReader.readLine())!=null)
		        {
		        	
					LOG.info(line);
		        	
		        	positiveWords.add(line.toUpperCase());
		        }
		         while((line = uncertaintyReader.readLine())!=null)
		        {
		        	
					LOG.info(line);
		        	
		        	uncertaintyWords.add(line.toUpperCase());
		        }
		    
		    
		    
		    }catch(Exception e)
		    {
		    System.out.println("Unable to read the cached filed");
		    System.exit(1);
		    }
		  }
			

		
		


		}


 


    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

   	parser = new JSONParser();
	dictionary = new HashMap<String,String>();

	long sentiment_value = 0;
	JSONObject json = null; 
	Object articles=null;
	
	try{
			
		

			StringTokenizer itr = new StringTokenizer(value.toString());



		      while (itr.hasMoreTokens()) {
		      	String word = itr.nextToken().toUpperCase();
		      	
		      	if(positiveWords.contains(word)){

		        	SentimentCount.set("positive");
		        	context.write(SentimentCount, one);
		    	}
		    	if(negativeWords.contains(word)){

		        	SentimentCount.set("negative");
		        	context.write(SentimentCount, one);
		    	}
		    	if(uncertaintyWords.contains(word)){

		        	SentimentCount.set("uncertainty");
		        	context.write(SentimentCount, one);
		    	}
		    	
		    	SentimentCount.set("TotalCount");
		        context.write(SentimentCount, one);
		    	
		      }

		 
										
	    }
	    catch(Exception e)
		{
			e.printStackTrace();
		}

     
    }
  }



// public class StockMapper extends Mapper<LongWritable,Text,NullWritable,Text> {

// 	JSONParser parser = null;
// 	Map<String,String> dictionary = null;
	
// 	// @Override
// 	// protected void setup(Context context)throws IOException,InterruptedException
// 	// {
// 	// 	parser = new JSONParser();
// 	// 	dictionary = new HashMap<String,String>();
		
		
		
// 	// 		URI[] cacheFiles = context.getCacheFiles();
		
// 	// 		if (cacheFiles != null && cacheFiles.length > 0)
// 	// 		  {
// 	// 		    try
// 	// 		    {   
// 	// 		    	String line ="";
// 	// 		        FileSystem fs = FileSystem.get(context.getConfiguration());
// 	// 		        Path path = new Path(cacheFiles[0].toString());
// 	// 		        BufferedReader reader = new BufferedReader(new InputStreamReader(fs.open(path)));
			    
// 	// 		        while((line = reader.readLine())!=null)
// 	// 		        {
// 	// 		        	String []tokens = line.split("\t");
// 	// 		        	dictionary.put(tokens[0], tokens[1]);
// 	// 		        }
			    
			    
			    
// 	// 		    }catch(Exception e)
// 	// 		    {
// 	// 		    System.out.println("Unable to read the cached filed");
// 	// 		    System.exit(1);
// 	// 		    }
// 	// 		  }
			
// 	// 	}
		
		
		
	
	
// 	@Override
// 	public void map(LongWritable key, Text value,Context context) throws IOException,InterruptedException
// 	{
// 		System.out.println(value);
// 		// parser = new JSONParser();
// 		// dictionary = new HashMap<String,String>();

// 		// long sentiment_value = 0;
// 		// JSONObject json = null; 
// 		try{
			
// 			// json = (JSONObject) parser.parse(value.toString());
// 		 //    JSONObject object = (JSONObject)json.get("articles");
// 		 //    System.out.println(object)
// 		    //object[0].get("title")
			
		    
			   
								
// 	    }
// 	    catch(ParseException e)
// 		{
// 			e.printStackTrace();
// 		}
			
// 	}	
	
// }
