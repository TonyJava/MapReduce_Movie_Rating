

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class step2Driver extends Configured implements Tool {

	public static void main(String[] args) throws Exception
	{
		ToolRunner.run(new step2Driver(),args);
	}

	@Override
	public int run(String[] args) throws Exception {
		
		Job job2 = new Job(getConf(),"step2");
		job2.setJarByClass(step2Driver.class);
		
		job2.setMapperClass(ComputeSimilarityStep2Mapper.class);
		job2.setReducerClass(ComputeSimilarityStep2Reducer.class);

		job2.setInputFormatClass(TextInputFormat.class);

		job2.setMapOutputKeyClass(Text.class);
		job2.setMapOutputValueClass(Text.class);

		job2.setOutputKeyClass(Text.class);
		job2.setOutputValueClass(DoubleWritable.class);

		FileInputFormat.addInputPath(job2, new Path(args[0]));
		FileOutputFormat.setOutputPath(job2, new Path(args[1]));

		job2.waitForCompletion(true);



		return 0;
	}
}
