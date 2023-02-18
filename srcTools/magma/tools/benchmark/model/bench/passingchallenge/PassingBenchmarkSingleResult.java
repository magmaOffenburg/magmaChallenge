package magma.tools.benchmark.model.bench.passingchallenge;

import magma.tools.benchmark.model.bench.SingleResult;

public class PassingBenchmarkSingleResult extends SingleResult
{
	private float time;

	public PassingBenchmarkSingleResult(
			boolean valid, boolean fallen, boolean penalty, String statusText, float time)
	{
		super(valid, fallen, penalty, statusText);
		this.time = time;
	}

	public float getTime()
	{
		return time;
	}

	@Override
	public double getScore()
	{
		return time;
	}
}
