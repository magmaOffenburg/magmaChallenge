package magma.tools.benchmark.model.bench.keepawaychallenge;

import magma.tools.benchmark.model.bench.SingleResult;

public class KeepAwayBenchmarkSingleResult extends SingleResult
{
	private final float time;

	public KeepAwayBenchmarkSingleResult(boolean valid, boolean fallen,
			boolean penalty, String statusText, float time)
	{
		super(valid, fallen, penalty, statusText);
		this.time = time;
	}

	public float getTime()
	{
		return time;
	}
}
