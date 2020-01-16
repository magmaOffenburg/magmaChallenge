package magma.tools.benchmark.model.bench.passingchallenge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import magma.tools.benchmark.model.ISingleResult;
import magma.tools.benchmark.model.bench.TeamResult;

public class PassingBenchmarkTeamResult extends TeamResult
{
	private List<Float> scores;

	public PassingBenchmarkTeamResult(String name)
	{
		super(name);
		scores = new ArrayList<>();
	}

	public void updateScores(ArrayList<Float> scores)
	{
		this.scores = scores;
	}

	@Override
	public float getAverageScore()
	{
		sortSingle();
		if (scores.size() <= PassingBenchmark.AVG_OUT_OF_BEST) {
			float avg = 0;
			for (float result : scores) {
				avg += result;
			}
			return avg / scores.size();
		} else {
			float avg = 0;
			for (int i = 0; i < PassingBenchmark.AVG_OUT_OF_BEST; i++) {
				avg += scores.get(i);
			}
			return avg / PassingBenchmark.AVG_OUT_OF_BEST;
		}
	}

	public float getBestScore()
	{
		sortSingle();
		if (scores.size() > 0) {
			return scores.get(0);
		} else {
			return 0;
		}
	}

	public float getSecondBestScore()
	{
		sortSingle();
		if (scores.size() > 1) {
			return scores.get(1);
		} else {
			return 0;
		}
	}

	public float getThirdBestScore()
	{
		sortSingle();
		if (scores.size() > 2) {
			return scores.get(2);
		} else {
			return 0;
		}
	}

	public void sortSingle()
	{
		int size = 0;
		for (ISingleResult result : results) {
			if (result instanceof PassingBenchmarkTeamResult) {
				PassingBenchmarkTeamResult teamResult = (PassingBenchmarkTeamResult) result;
				if (teamResult.scores.size() > size) {
					this.scores = teamResult.scores;
					size = teamResult.scores.size();
				}
			} else if (result instanceof PassingBenchmarkSingleResult) {
				PassingBenchmarkSingleResult singleResult = (PassingBenchmarkSingleResult) result;
				if (singleResult.getScores().size() > size) {
					this.scores = singleResult.getScores();
					size = singleResult.getScores().size();
				}
			}
		}

		Collections.sort(scores);
	}
}
