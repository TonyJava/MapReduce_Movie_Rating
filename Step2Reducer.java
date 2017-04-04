

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;


public class Step2Reducer extends Reducer<Text,Text,Text, DoubleWritable>{
	private int thresh = 1;
	@Override
	public void reduce(Text key,Iterable<Text> listOfValues,Context context) throws IOException,InterruptedException
	{
		double sumdot=0;
		double rating1SquareSum = 0;
		double rating2SquareSum = 0;
		int count = 0;
		for(Text val:listOfValues)
		{
			count++;
			String ratingPair=val.toString().replace("(", "").replace(")", "");
			String ratings[]=ratingPair.split(",");
			double rating1 = Double.parseDouble(ratings[0]);
			double rating2 = Double.parseDouble(ratings[1]);
			double dot = rating1 * rating2;
			sumdot = sumdot + dot;
			rating1SquareSum = rating1SquareSum + rating1 * rating1;
			rating2SquareSum = rating2SquareSum + rating2 * rating2;
		}
		if (count > 1) {
			context.write(key,new DoubleWritable(sumdot/((Math.sqrt(rating1SquareSum)) * (Math.sqrt(rating2SquareSum)))));
		}
	}

}
