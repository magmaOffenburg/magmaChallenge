package magma.tools.benchmark.model.bench.passingchallenge;

import java.util.ArrayList;

import magma.tools.benchmark.model.bench.SingleResult;

public class PassingBenchmarkSingleResult extends SingleResult
{
	public ArrayList<Float> scores;

	private float time;

	public PassingBenchmarkSingleResult(
			boolean valid, boolean fallen, boolean penalty, String statusText, float time, ArrayList<Float> res)
	{
		super(valid, fallen, penalty, statusText);
		this.time = time;
		scores = res;
	}

	public float getTime()
	{
		return time;
	}
}
