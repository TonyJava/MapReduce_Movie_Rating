

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class Step1Reducer extends Reducer<Text,Text,Text, Text>{

	@Override
	public void reduce(Text key,Iterable<Text> listOfValues,Context context) throws IOException,InterruptedException
	{
	
		StringBuffer listOfMovies=new StringBuffer();
		for(Text val:listOfValues)
		{
			if(listOfMovies.length()==0)
				listOfMovies.append(val.toString());
			else
				listOfMovies.append("-"+val.toString());
		}
		context.write(key,new Text(listOfMovies.toString()));


	}

}
