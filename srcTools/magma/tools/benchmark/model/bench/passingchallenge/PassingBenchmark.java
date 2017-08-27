package magma.tools.benchmark.model.bench.passingchallenge;

import java.util.ArrayList;
import java.util.Random;

import magma.monitor.general.impl.MonitorComponentFactory;
import magma.monitor.referee.IReferee;
import magma.tools.benchmark.model.BenchmarkConfiguration;
import magma.tools.benchmark.model.ISingleResult;
import magma.tools.benchmark.model.ITeamResult;
import magma.tools.benchmark.model.TeamConfiguration;
import magma.tools.benchmark.model.bench.BenchmarkMain;
import magma.tools.benchmark.model.bench.RunInformation;
import magma.tools.benchmark.model.bench.TeamResult;
import magma.tools.benchmark.model.bench.keepawaychallenge.KeepAwayBenchmarkReferee;

public class PassingBenchmark extends BenchmarkMain{
	
	public static final int PLAYERS = 4;
	public static final int AVG_OUT_OF_BEST = 3;
	
	private ArrayList<Float> results;
	
	public PassingBenchmark(String roboVizServer) {
		super(roboVizServer, false);
		allowedPlayers = PLAYERS;
		allowPlayerBeaming = true;
		results = new ArrayList<>();
	}

	@Override
	protected ISingleResult benchmarkResults() {
		float time = 0;
		boolean valid = false;
		if (monitor != null) {
			PassingBenchmarkReferee referee = (PassingBenchmarkReferee) monitor.getReferee();
			if (referee.getState() == IReferee.RefereeState.STOPPED) {
				valid = true;
				time = referee.getTime();
				results.add(time);
			} else {
				statusText += referee.getStatusText();
			}
		}
		return new PassingBenchmarkSingleResult(valid, false, false, statusText, time, results);
	}
	
	@Override
	protected RunInformation createRunInformation(Random rand, int runID)
	{
		results.clear();
		return super.createRunInformation(rand, runID);
	}
	
	@Override
	public void collectResults(ITeamResult currentRunResult)
	{
		((PassingBenchmarkTeamResult) currentRunResult).updateScores(results);
		super.collectResults(currentRunResult);
	}

	@Override
	protected MonitorComponentFactory createMonitorFactory(BenchmarkConfiguration config, TeamConfiguration teamConfig,
			RunInformation runInfo, String roboVizServer) {
		return new PassingBenchmarkMonitorComponentFactory(createFactoryParameter(config, teamConfig), runInfo, roboVizServer);
	}

	@Override
	protected TeamResult createTeamResult(TeamConfiguration currentTeamConfig) {
		return new PassingBenchmarkTeamResult(currentTeamConfig.getName());
	}

}
