

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class Step2Mapper extends Mapper<LongWritable, Text, Text, Text> {
	
	public void map(LongWritable inputKey,Text inputVal,Context context) throws IOException,InterruptedException
	{
		String line = inputVal.toString();
		String[] splits = line.trim().split("\\t");
		String previousMovie=null;
		String previousRating=null;
		String currentMovie=null;
		String currentRating=null;
		
		String[] movieRatingSplits=splits[1].split("-");
	
		for(int i=0;i<movieRatingSplits.length-1;i++)
		{
			if(movieRatingSplits[i].length()>0)
			{
				String[] RatingSplits=movieRatingSplits[i].split(",");
				previousMovie=RatingSplits[0].replace("(","");
				previousRating=RatingSplits[1].replace(")","");
				
				for(int j=i+1;j<movieRatingSplits.length;j++)
				{
					if(movieRatingSplits[j].length()>0)
					{
						String[] RatingSplits2=movieRatingSplits[j].split(",");
						currentMovie=RatingSplits2[0].replace("(","");
						currentRating=RatingSplits2[1].replace(")","");
						if(new Integer(previousMovie)<new Integer(currentMovie))
							context.write(new Text(new String("("+previousMovie+","+currentMovie+")")),new Text(new String("("+previousRating+","+currentRating+")")));
						else
							context.write(new Text(new String("("+currentMovie+","+previousMovie+")")),new Text(new String("("+currentRating+","+previousRating+")")));
					}
				}
			}
		}
	}
}
