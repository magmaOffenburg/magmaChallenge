package magma.tools.benchmark.model.bench.keepawaychallenge;

import magma.tools.benchmark.model.ISingleResult;
import magma.tools.benchmark.model.bench.TeamResult;

public class KeepAwayBenchmarkTeamResult extends TeamResult
{
	public KeepAwayBenchmarkTeamResult(String name)
	{
		super(name);
	}

	@Override
	public float getAverageScore()
	{
		return getAverageTime();
	}

	public float getAverageTime()
	{
		if (results.isEmpty()) {
			return 0;
		}
		float avg = 0;
		for (ISingleResult result : results) {
			if (result instanceof KeepAwayBenchmarkTeamResult) {
				avg += ((KeepAwayBenchmarkTeamResult) result).getAverageTime();
			} else {
				avg += ((KeepAwayBenchmarkSingleResult) result).getTime();
			}
		}
		return avg / results.size();
	}
}
