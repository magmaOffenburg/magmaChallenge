package magma.tools.benchmark.model.bench.passingchallenge;

import java.util.ArrayList;
import java.util.Collections;

import magma.tools.benchmark.model.ISingleResult;
import magma.tools.benchmark.model.bench.TeamResult;

public class PassingBenchmarkTeamResult extends TeamResult{

	public PassingBenchmarkTeamResult(String name) {
		super(name);
		scores = new ArrayList<>();
	}
	
	public ArrayList<Float> scores;
	
	public void updateScores(ArrayList<Float> res)
	{
		scores = res;
	}

	@Override
	public float getAverageScore() {
		sortSingle();
		if(scores.size() <= PassingBenchmark.AVG_OUT_OF_BEST)
		{
			float avg = 0;
			for(float result : scores)
			{
				avg+=result;
			}
			return avg/scores.size();
		}
		else
		{
			float avg = 0;
			for(int i = 0; i < PassingBenchmark.AVG_OUT_OF_BEST; i++)
			{
				avg += scores.get(i);
			}
			return avg/PassingBenchmark.AVG_OUT_OF_BEST;
		}
	}
	
	public float getBestScore()
	{
		sortSingle();
		if(scores.size() > 0) return scores.get(0);
		else return 0;
	}
	
	public float getSecondBestScore()
	{
		sortSingle();
		if(scores.size() > 1) return scores.get(1);
		else return 0;
	}
	
	public float getThirdBestScore()
	{
		sortSingle();
		if(scores.size() > 2) return scores.get(2);
		else return 0;
	}
	
	public void sortSingle()
	{
		
		/*if(scores.size() < results.size())
		{
			scores.add(getLastTime());
		}*/
		int s = 0;
		for (ISingleResult result : results) {
			if (result instanceof PassingBenchmarkTeamResult) {
				if(((PassingBenchmarkTeamResult) result).scores.size() > s)
				{
					this.scores = ((PassingBenchmarkTeamResult) result).scores;
					s = ((PassingBenchmarkTeamResult) result).scores.size();
				}
				
				
			} 
			else if(result instanceof PassingBenchmarkSingleResult)
			{
				if(((PassingBenchmarkSingleResult) result).scores.size() > s)
				{
					this.scores = ((PassingBenchmarkSingleResult) result).scores;
					s = ((PassingBenchmarkSingleResult) result).scores.size();
				}
			}
		}
		
		Collections.sort(scores);
	}
	
	public float getLastTime()
	{
		if (results.isEmpty()) {
			return 0.0f;
		}
		
		ISingleResult result = results.get(results.size() - 1);
		if (result instanceof PassingBenchmarkTeamResult)
		{
			return ((PassingBenchmarkTeamResult) result).getAverageTime();
		}
		else
		{
			return ((PassingBenchmarkSingleResult) result).getTime();
		}
	}

	public float getAverageTime()
	{
		sortSingle();
		if (results.isEmpty()) {
			return 0;
		}
		float avg = 0;
		for (ISingleResult result : results) {
			if (result instanceof PassingBenchmarkTeamResult) {
				avg += ((PassingBenchmarkTeamResult) result).getAverageTime();
			} else {
				avg += ((PassingBenchmarkSingleResult) result).getTime();
			}
		}
		return avg / results.size();
	}
	
}
