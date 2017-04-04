

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class step1Driver extends Configured implements Tool {

	public static void main(String[] args) throws Exception
	{
		ToolRunner.run(new step1Driver(),args);
	}

	@Override
	public int run(String[] args) throws Exception {

		Job job1 = new Job(getConf(),"step1");
		job1.setJarByClass(step1Driver.class);
		
		job1.setMapperClass(Step1Mapper.class);
		job1.setReducerClass(Step1Reducer.class);

		job1.setInputFormatClass(TextInputFormat.class);

		job1.setMapOutputKeyClass(Text.class);
		job1.setMapOutputValueClass(Text.class);

		job1.setOutputKeyClass(Text.class);
		job1.setOutputValueClass(Text.class);

		FileInputFormat.addInputPath(job1, new Path(args[0]));
		FileOutputFormat.setOutputPath(job1, new Path(args[1]));

		job1.waitForCompletion(true);

		return 0;
	}
}
