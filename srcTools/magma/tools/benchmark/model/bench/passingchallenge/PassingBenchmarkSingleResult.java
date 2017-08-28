package magma.tools.benchmark.model.bench.passingchallenge;

import java.util.List;
import magma.tools.benchmark.model.bench.SingleResult;

public class PassingBenchmarkSingleResult extends SingleResult
{
	private List<Float> scores;

	private float time;

	public PassingBenchmarkSingleResult(
			boolean valid, boolean fallen, boolean penalty, String statusText, float time, List<Float> scores)
	{
		super(valid, fallen, penalty, statusText);
		this.time = time;
		this.scores = scores;
	}

	public List<Float> getScores()
	{
		return scores;
	}

	public float getTime()
	{
		return time;
	}
}
