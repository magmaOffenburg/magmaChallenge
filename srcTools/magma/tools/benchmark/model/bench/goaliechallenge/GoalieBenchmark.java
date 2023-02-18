/* Copyright 2009 Hochschule Offenburg
 * Klaus Dorer, Mathias Ehret, Stefan Glaser, Thomas Huber,
 * Simon Raffeiner, Srinivasa Ragavan, Thomas Rinklin,
 * Joachim Schilling, Rajit Shahi
 *
 * This file is part of magmaOffenburg.
 *
 * magmaOffenburg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * magmaOffenburg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with magmaOffenburg. If not, see <http://www.gnu.org/licenses/>.
 */

package magma.tools.benchmark.model.bench.goaliechallenge;

import hso.autonomy.util.file.CSVFileUtil;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import magma.monitor.general.impl.MonitorComponentFactory;
import magma.monitor.referee.IReferee.RefereeState;
import magma.tools.benchmark.model.BenchmarkConfiguration;
import magma.tools.benchmark.model.ISingleResult;
import magma.tools.benchmark.model.TeamConfiguration;
import magma.tools.benchmark.model.bench.BenchmarkMain;
import magma.tools.benchmark.model.bench.RunInformation;
import magma.tools.benchmark.model.bench.TeamResult;

/**
 *
 * @author kdorer
 */
public class GoalieBenchmark extends BenchmarkMain
{
	/** how many attempts each team has */
	/** how many attempts each team has */
	static final int BENCHMARK_RUNS = 12;

	float[][] kicks;

	public GoalieBenchmark(String roboVizServer)
	{
		super(roboVizServer, false);
		allowPlayerBeaming = true;
		CSVFileUtil csvFileHandler = new CSVFileUtil(new File("config/goalie/kicks.csv"), ";");
		try {
			List<String[]> readCsvFile = csvFileHandler.readCsvFile();

			kicks = new float[readCsvFile.size()][5];
			int lineInd = 0;
			for (String[] line : readCsvFile) {
				if (line.length != 5) {
					String error = "Invalid config file format, expected 5 columns, but was: " + line.length +
								   "\nline: " + Arrays.toString(line);
					throw new IOException(error);
				}
				for (int i = 0; i < 5; i++) {
					kicks[lineInd][i] = Float.valueOf(line[i]);
				}
				/*System.err.println("Kick "+lineInd+": "
					   +kicks[lineInd][0]
					   +" "+kicks[lineInd][1]
					   +" "+kicks[lineInd][2]
					   +" "+kicks[lineInd][3]
					   +" "+kicks[lineInd][4]+"\n");
		*/
				lineInd++;
			}
		} catch (IOException e) {
			System.err.println("Caught IOException: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	protected ISingleResult benchmarkResults()
	{
		float avgScore = 0;
		boolean valid = false;
		if (monitor != null) {
			GoalieBenchmarkReferee referee = (GoalieBenchmarkReferee) monitor.getReferee();
			if (referee.getState() == RefereeState.STOPPED) {
				avgScore = (float) referee.getScore();
				valid = true;
			} else {
				statusText += referee.getStatusText();
			}
		}
		return new GoalieBenchmarkSingleResult(valid, statusText, avgScore);
	}

	@Override
	protected MonitorComponentFactory createMonitorFactory(
			BenchmarkConfiguration config, TeamConfiguration teamConfig, RunInformation runInfo, String roboVizServer)
	{
		return new GoalieBenchmarkMonitorComponentFactory(
				createFactoryParameter(config, teamConfig), runInfo, roboVizServer);
	}

	@Override
	protected TeamResult createTeamResult(TeamConfiguration currentTeamConfig)
	{
		return new TeamResult(currentTeamConfig.getName());
	}

	@Override
	protected RunInformation createRunInformation(Random rand, int runID)
	{
		int sampleIndex = rand.nextInt(kicks.length);
		double ballX = kicks[sampleIndex][0];
		double ballY = kicks[sampleIndex][1];
		double ballVelX = kicks[sampleIndex][2];
		double ballVelY = kicks[sampleIndex][3];
		double ballVelZ = kicks[sampleIndex][4];
		return new RunInformation(runID, ballX, ballY, ballVelX, ballVelY, ballVelZ);
	}

	protected double noise(Random rand, double noise)
	{
		return (rand.nextDouble() - 0.5) * 2 * noise;
	}

	/**
	 * The number of different runs or phases this benchmark has.
	 * @return 12 for goalie bench
	 */
	@Override
	protected int getBenchmarkRuns()
	{
		return BENCHMARK_RUNS;
	}
}
