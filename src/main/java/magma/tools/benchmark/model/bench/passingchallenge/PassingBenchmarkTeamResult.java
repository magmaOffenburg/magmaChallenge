package magma.tools.benchmark.model.bench.passingchallenge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import magma.tools.benchmark.model.ISingleResult;
import magma.tools.benchmark.model.bench.TeamResult;

public class PassingBenchmarkTeamResult extends TeamResult
{
	private List<Double> scores;

	public PassingBenchmarkTeamResult(String name)
	{
		super(name);
	}

	@Override
	public double getScore()
	{
		scores = new ArrayList<>();
		for (ISingleResult result : results) {
			scores.add(result.getScore());
		}

		Collections.sort(scores);

		int end = PassingBenchmark.AVG_OUT_OF_BEST;
		if (scores.size() < end) {
			end = scores.size();
		}
		List<Double> subList = scores.subList(0, end);
		return subList.stream().mapToDouble(value -> value).summaryStatistics().getAverage();
	}

	public double getScore(int i)
	{
		if (scores.size() > i) {
			return scores.get(i);
		} else {
			return 0;
		}
	}
}
