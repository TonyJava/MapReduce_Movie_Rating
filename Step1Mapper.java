

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class Step1Mapper extends Mapper<LongWritable, Text, Text, Text> {
	
	public void map(LongWritable inputKey,Text inputVal,Context context) throws IOException,InterruptedException
	{
		String line = inputVal.toString();
		String[] splits = line.trim().split(",");
				context.write(new Text(splits[0]),new Text("("+splits[1]+","+splits[2]+")"));

	}

}
