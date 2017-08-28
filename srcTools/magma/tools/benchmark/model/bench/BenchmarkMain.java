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

package magma.tools.benchmark.model.bench;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import magma.monitor.general.IMonitorRuntimeListener;
import magma.monitor.general.impl.FactoryParameter;
import magma.monitor.general.impl.MonitorComponentFactory;
import magma.monitor.general.impl.MonitorParameter;
import magma.monitor.general.impl.MonitorRuntime;
import magma.monitor.referee.IReferee.RefereeState;
import magma.monitor.server.ServerController;
import magma.monitor.server.ServerException;
import magma.tools.proxy.impl.SimsparkAgentProxyServer.SimsparkAgentProxyServerParameter;
import magma.tools.benchmark.model.BenchmarkConfiguration;
import magma.tools.benchmark.model.IModelReadOnly;
import magma.tools.benchmark.model.IModelReadWrite;
import magma.tools.benchmark.model.ISingleResult;
import magma.tools.benchmark.model.ITeamResult;
import magma.tools.benchmark.model.InvalidConfigFileException;
import magma.tools.benchmark.model.TeamConfiguration;
import magma.tools.benchmark.model.proxy.BenchmarkAgentProxy;
import magma.tools.benchmark.model.proxy.BenchmarkAgentProxyServer;
import magma.util.connection.ConnectionException;
import magma.util.observer.IObserver;
import magma.util.observer.IPublishSubscribe;
import magma.util.observer.Subject;

/**
 *
 * @author kdorer
 */
public abstract class BenchmarkMain implements IMonitorRuntimeListener, IModelReadWrite
{
	public static final String START_SCRIPT_NAME = "startChallengePlayer.sh";

	/** observers of model */
	private final transient IPublishSubscribe<IModelReadOnly> observer;

	private final String roboVizServer;

	protected BenchmarkAgentProxyServer proxy;

	protected MonitorRuntime monitor;

	private ServerController server;

	private List<ITeamResult> results;

	private RunThread runThread;

	private int currentTeam;

	protected String statusText;

	private String scriptPath;

	protected int allowedPlayers = 1;

	protected boolean allowPlayerBeaming = false;

	protected boolean isGazebo;

	public BenchmarkMain(String roboVizServer, boolean isGazebo)
	{
		this.roboVizServer = roboVizServer;
		this.isGazebo = isGazebo;

		observer = new Subject<>();
		runThread = null;
		results = new ArrayList<>();

		// build environment
		URL resource = BenchmarkMain.class.getResource("/rcssserver3d.rb");
		if (resource != null) {
			scriptPath = resource.getPath();
		}

		// release environment / jar
		if (scriptPath == null || scriptPath.contains("jar!")) {
			String absPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
			String libPath = absPath.substring(0, absPath.lastIndexOf(File.separator));
			scriptPath = libPath.substring(0, libPath.lastIndexOf(File.separator)) + "/config/rcssserver3d.rb";
		}

		server = new ServerController(3100, 3200, false, scriptPath, 0);
		statusText = "";
	}

	@Override
	public void resetModel()
	{
		results = new ArrayList<>();
		currentTeam = 0;
		statusText = "";
	}

	@Override
	public void start(BenchmarkConfiguration config, List<TeamConfiguration> teamConfig)
	{
		if (isRunning()) {
			return;
		}

		resetModel();
		server = new ServerController(config.getServerPort(), config.getTrainerPort(), false, scriptPath, 0);

		runThread = new RunThread(config, teamConfig);
		runThread.start();
	}

	@Override
	public boolean isRunning()
	{
		return runThread != null;
	}

	public void collectResults(ITeamResult currentRunResult)
	{
		ISingleResult result = benchmarkResults();
		currentRunResult.addResult(result);
		observer.onStateChange(this);
	}

	protected abstract ISingleResult benchmarkResults();

	public int getCurrentTeam()
	{
		return currentTeam;
	}

	protected ITeamResult getCurrentTeamResult()
	{
		return results.get(getCurrentTeam());
	}

	private void startProxy(BenchmarkConfiguration config)
	{
		// start proxy to get force information
		SimsparkAgentProxyServerParameter parameterObject = new SimsparkAgentProxyServerParameter(
				config.getAgentPort(), config.getServerIP(), config.getServerPort(), config.isVerbose());
		proxy = new BenchmarkAgentProxyServer(parameterObject, allowedPlayers, allowPlayerBeaming, isGazebo);
		proxy.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private boolean startTrainer(
			BenchmarkConfiguration config, TeamConfiguration teamConfig, RunInformation runInfo, String roboVizServer)
	{
		MonitorComponentFactory factory = createMonitorFactory(config, teamConfig, runInfo, roboVizServer);

		monitor = new BenchmarkMonitorRuntime(
				new MonitorParameter(config.getServerIP(), config.getTrainerPort(), Level.WARNING, 3, factory),
				isGazebo);

		monitor.addRuntimeListener(this);

		int tryCount = 0;
		boolean connected = false;
		while (!connected && tryCount < 10) {
			try {
				monitor.startMonitor();
				connected = true;
			} catch (ConnectionException e1) {
				tryCount++;
				connected = false;
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return tryCount < 10;
	}

	protected abstract MonitorComponentFactory createMonitorFactory(
			BenchmarkConfiguration config, TeamConfiguration teamConfig, RunInformation runInfo, String roboVizServer);

	protected FactoryParameter createFactoryParameter(BenchmarkConfiguration config, TeamConfiguration teamConfig)
	{
		return new FactoryParameter(null, config.getServerIP(), config.getAgentPort(), teamConfig.getName(),
				teamConfig.getPath(), START_SCRIPT_NAME, null, config.getRuntime(), teamConfig.getDropHeight());
	}

	protected abstract TeamResult createTeamResult(TeamConfiguration currentTeamConfig);

	protected RunInformation createRunInformation(Random rand, int runID)
	{
		return new RunInformation(runID, -13.5, 0, 14.5, 0);
	}

	/**
	 * The number of different runs or phases this benchmark has.
	 * @return the number of runs, default 1
	 */
	protected int getBenchmarkRuns()
	{
		return 1;
	}

	@Override
	public void stop()
	{
		if (isRunning()) {
			runThread.stopAll();
		}
	}

	@Override
	public void monitorUpdated()
	{
		if (!proxy.getAgentProxies().isEmpty()) {
			BenchmarkAgentProxy benchmarkAgentProxy = (BenchmarkAgentProxy) proxy.getAgentProxies().get(0);
			if (monitor.getReferee().getState() == RefereeState.STARTED) {
				benchmarkAgentProxy.startCount();
			} else if (monitor.getReferee().getState() == RefereeState.STOPPED) {
				benchmarkAgentProxy.stopCount();
			} else if (monitor.getReferee().getState() == RefereeState.BEAMED) {
				benchmarkAgentProxy.noBeaming();
			}
		}

		int countMonitor = monitor.getWorldModel().getSoccerAgents().size();
		if (countMonitor > allowedPlayers) {
			statusText += "More than " + allowedPlayers + " player(s) on the field\n";
			monitor.stopMonitor();
			runThread.stopTeam();
			return;
		}
		int countProxy = proxy.getAgentProxies().size();
		if (countMonitor == 1 && countProxy < 1) {
			statusText += "Player did not connect through proxy\n";
			monitor.stopMonitor();
			runThread.stopTeam();
		}
	}

	@Override
	public List<ITeamResult> getTeamResults()
	{
		return Collections.unmodifiableList(results);
	}

	@Override
	public void attach(IObserver<IModelReadOnly> newObserver)
	{
		observer.attach(newObserver);
	}

	@Override
	public boolean detach(IObserver<IModelReadOnly> oldObserver)
	{
		return observer.detach(oldObserver);
	}

	private class RunThread extends Thread
	{
		private final BenchmarkConfiguration config;

		private boolean stopped;

		private boolean stoppedTeam;

		private final List<TeamConfiguration> teamConfig;

		public RunThread(BenchmarkConfiguration config, List<TeamConfiguration> teamConfig)
		{
			this.config = config;
			this.teamConfig = teamConfig;
			stopped = false;
			stoppedTeam = false;
		}

		public void replace(String path, float dropHeight)
		{
			try {
				FileReader fr = new FileReader(path);
				BufferedReader br = new BufferedReader(fr);
				String line;
				ArrayList<String> lines = new ArrayList<>();

				while ((line = br.readLine()) != null) {
					if (line.contains("AgentRadius")) {
						line = "addSoccerVar('AgentRadius', " + String.valueOf(dropHeight) + ")";
					}
					lines.add(line);
				}
				br.close();
				FileWriter fw = new FileWriter(path);
				BufferedWriter bw = new BufferedWriter(fw);
				for (String l : lines) {
					bw.write(l + '\n');
				}
				bw.close();
			} catch (Exception ioe) {
				ioe.printStackTrace();
			}
		}

		@Override
		public void run()
		{
			// start the proxy through which players should connect
			startProxy(config);

			boolean stopped = false;
			// loop for all teams in the competition
			for (TeamConfiguration currentTeamConfig : teamConfig) {
				// note that all results of all benchmark runs are stored here
				results.add(createTeamResult(currentTeamConfig));

				int benchmarkRuns = getBenchmarkRuns();
				if (config.isTest()) {
					benchmarkRuns = 1;
				}
				Random rand = new Random(config.getRandomSeed());

				for (int i = 0; i < benchmarkRuns; i++) {
					// reset status text for each run
					statusText = "";

					RunInformation info = createRunInformation(rand, i);
					proxy.updateProxy(info);
					stopped = performAverageOutRuns(currentTeamConfig, info);
					if (stopped) {
						break;
					}
				}
				currentTeam++;
				if (stopped) {
					break;
				}
			}
			proxy.shutdown();
			runThread = null;
			observer.onStateChange(BenchmarkMain.this);
		}

		/**
		 * Performs the specified number of runs to average out the result for
		 * this benchmark run.
		 * @param currentTeamConfig the config for the currently running team
		 * @return true if stopped
		 */
		protected boolean performAverageOutRuns(TeamConfiguration currentTeamConfig, RunInformation runInfo)
		{
			int avgRuns = config.getAverageOutRuns();

			stoppedTeam = false;
			ITeamResult currentRunResult = createTeamResult(currentTeamConfig);
			getCurrentTeamResult().addResult(currentRunResult);
			while (currentRunResult.size() < avgRuns && !stopped && !stoppedTeam) {
				try {
					float dropHeight = currentTeamConfig.getDropHeight();
					replace(scriptPath, dropHeight);

					if (!isGazebo) {
						server.startServer(0);
					}

					boolean success = startTrainer(config, currentTeamConfig, runInfo, roboVizServer);
					if (success) {
						collectResults(currentRunResult);
					}
				} catch (ServerException e) {
					statusText += e.getMessage();
					collectResults(currentRunResult);
				} catch (RuntimeException e) {
					e.printStackTrace();
				} finally {
					stopServer();
				}
			}
			System.out.println("Average Score: " + currentRunResult.getAverageScore());
			return stopped;
		}

		public void stopAll()
		{
			stopped = true;
		}

		public void stopTeam()
		{
			stoppedTeam = true;
		}
	}

	@Override
	public void stopServer()
	{
		if (!isGazebo) {
			server.killAllServer();
		}
	}

	@Override
	public List<TeamConfiguration> loadConfigFile(File file) throws InvalidConfigFileException
	{
		ConfigLoader loader = new ConfigLoader();
		return loader.loadConfigFile(file);
	}
}
