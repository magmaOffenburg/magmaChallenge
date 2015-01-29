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
import java.util.logging.Level;

import magma.monitor.general.IMonitorRuntimeListener;
import magma.monitor.general.impl.MonitorComponentFactory;
import magma.monitor.general.impl.MonitorParameter;
import magma.monitor.general.impl.MonitorRuntime;
import magma.monitor.referee.IReferee.RefereeState;
import magma.monitor.server.ServerController;
import magma.monitor.server.ServerException;
import magma.tools.SAProxy.impl.SimsparkAgentProxyServer.SimsparkAgentProxyServerParameter;
import magma.tools.benchmark.model.BenchmarkConfiguration;
import magma.tools.benchmark.model.IModelReadOnly;
import magma.tools.benchmark.model.IModelReadWrite;
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
public abstract class BenchmarkMain implements IMonitorRuntimeListener,
		IModelReadWrite
{
	/** observers of model */
	private final transient IPublishSubscribe<IModelReadOnly> observer;

	protected BenchmarkAgentProxyServer proxy;

	protected MonitorRuntime monitor;

	private ServerController server;

	private List<ITeamResult> results;

	private RunThread runThread;

	private int currentTeam;

	protected String statusText;

	private String scriptPath;

	public BenchmarkMain()
	{
		observer = new Subject<IModelReadOnly>();
		runThread = null;
		results = new ArrayList<ITeamResult>();

		URL resource = BenchmarkMain.class
				.getResource("/runChallenge/rcssserver3d.rb");
		if (resource != null) {
			scriptPath = resource.getPath();
		}

		if (scriptPath == null || scriptPath.contains("jar!")) {
			String absPath = getClass().getProtectionDomain().getCodeSource()
					.getLocation().getPath();
			scriptPath = absPath.substring(0, absPath.lastIndexOf(File.separator))
					+ "/rcssserver3d.rb";
		}

		server = new ServerController(3100, 3200, false, scriptPath);
		statusText = "";
	}

	@Override
	public void resetModel()
	{
		results = new ArrayList<ITeamResult>();
		currentTeam = 0;
		statusText = "";
	}

	@Override
	public void start(BenchmarkConfiguration config,
			List<TeamConfiguration> teamConfig)
	{
		if (isRunning()) {
			return;
		}

		resetModel();
		server = new ServerController(config.getServerPort(),
				config.getTrainerPort(), false, scriptPath);

		runThread = new RunThread(config, teamConfig);
		runThread.start();
	}

	/**
	 * @return
	 */
	@Override
	public boolean isRunning()
	{
		return runThread != null;
	}

	/**
	 * 
	 */
	private void collectResults()
	{
		benchmarkResults();
		observer.onStateChange(this);
	}

	protected abstract void benchmarkResults();

	/**
	 * @return
	 */
	public int getCurrentTeam()
	{
		return currentTeam;
	}

	/**
	 * @return
	 */
	protected ITeamResult getCurrentResult()
	{
		return results.get(getCurrentTeam());
	}

	/**
	 * @param args
	 */
	private void startProxy(BenchmarkConfiguration config)
	{
		// start proxy to get force information
		SimsparkAgentProxyServerParameter parameterObject = new SimsparkAgentProxyServerParameter(
				config.getAgentPort(), config.getServerIP(),
				config.getServerPort(), config.isVerbose());
		proxy = new BenchmarkAgentProxyServer(parameterObject);
		proxy.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private boolean startTrainer(BenchmarkConfiguration config,
			TeamConfiguration teamConfig, int currentRun)
	{
		MonitorComponentFactory factory = createMonitorFactory(config,
				teamConfig, currentRun);

		monitor = new MonitorRuntime(new MonitorParameter(config.getServerIP(),
				config.getTrainerPort(), Level.WARNING, 3, factory));

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
		if (tryCount >= 10) {
			return false;
		}
		return true;
	}

	protected abstract MonitorComponentFactory createMonitorFactory(
			BenchmarkConfiguration config, TeamConfiguration teamConfig,
			int currentRun);

	protected abstract TeamResult createTeamResult(
			TeamConfiguration currentTeamConfig);

	/**
	 * 
	 */
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
			BenchmarkAgentProxy benchmarkAgentProxy = (BenchmarkAgentProxy) proxy
					.getAgentProxies().get(0);
			if (monitor.getReferee().getState() == RefereeState.STARTED) {
				benchmarkAgentProxy.startCount();
			} else if (monitor.getReferee().getState() == RefereeState.STOPPED) {
				benchmarkAgentProxy.stopCount();
			} else if (monitor.getReferee().getState() == RefereeState.BEAMED) {
				benchmarkAgentProxy.noBeaming();
			}
		}

		int countMonitor = monitor.getWorldModel().getSoccerAgents().size();
		if (countMonitor > 1) {
			statusText += "More than one player on the field\n";
			monitor.stopMonitor();
			runThread.stopTeam();
			return;
		}
		int countProxy = proxy.getAgentProxies().size();
		if (countMonitor == 1 && countProxy < 1) {
			statusText += "Player did not connect through proxy\n";
			monitor.stopMonitor();
			runThread.stopTeam();
			return;
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
		private BenchmarkConfiguration config;

		private boolean stopped;

		private boolean stoppedTeam;

		private List<TeamConfiguration> teamConfig;

		/**
		 * @param config
		 */
		public RunThread(BenchmarkConfiguration config,
				List<TeamConfiguration> teamConfig)
		{
			this.config = config;
			this.teamConfig = teamConfig;
			stopped = false;
			stoppedTeam = false;
		}

		public void replace(String path, float dropheight)
		{
			try {
				// System.out.println(path);
				FileReader fr = new FileReader(path);
				BufferedReader br = new BufferedReader(fr);
				String line = null;
				ArrayList<String> myLines = new ArrayList<String>();

				while ((line = br.readLine()) != null) {
					// System.out.println(line);
					if (line.indexOf("AgentRadius") >= 0) {
						line = "addSoccerVar('AgentRadius', "
								+ String.valueOf(dropheight) + ")";
					}
					myLines.add(line);
				}
				br.close();
				FileWriter fw = new FileWriter(path);
				BufferedWriter bw = new BufferedWriter(fw);
				for (int i = 0; i < myLines.size(); i++) {
					// System.out.println(myLines.get(i));
					bw.write(myLines.get(i) + '\n');
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

			int benchmarkRuns = getBenchmarkRuns();
			for (int i = 0; i < benchmarkRuns; i++) {
				performAverageOutRuns(benchmarkRuns);
			}

			proxy.shutdown();
			runThread = null;
			observer.onStateChange(BenchmarkMain.this);
		}

		/**
		 * The number of different runs or phases this benchmark has.
		 * @return the number of runs, default 1
		 */
		protected int getBenchmarkRuns()
		{
			return 1;
		}

		/**
		 * Performs the specified number of runs to average out the result for
		 * this benchmark run.
		 * @param currentRun the current run/phase of the benchmark
		 */
		protected void performAverageOutRuns(int currentRun)
		{
			int avgRuns = config.getAverageOutRuns();

			for (TeamConfiguration currentTeamConfig : teamConfig) {
				results.add(createTeamResult(currentTeamConfig));
				stoppedTeam = false;
				while (getCurrentResult().size() < avgRuns && !stopped
						&& !stoppedTeam) {
					try {
						float dropHeight = currentTeamConfig.getDropHeight();
						// System.out.println("dropheight: " +
						// String.valueOf(dropHeight));
						replace(scriptPath, dropHeight);

						server.startServer();

						boolean success = startTrainer(config, currentTeamConfig,
								currentRun);
						if (success) {
							collectResults();
						}

					} catch (ServerException e) {
						statusText += e.getMessage();
						collectResults();

					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						server.stopServer();
					}
				}
				System.out.println("Overall Score: "
						+ getCurrentResult().getAverageScore());
				currentTeam++;
				if (stopped) {
					break;
				}
			}
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
		server.killAllServer();
	}

	@Override
	public List<TeamConfiguration> loadConfigFile(File file)
			throws InvalidConfigFileException
	{
		ConfigLoader loader = new ConfigLoader();
		return loader.loadConfigFile(file);
	}
}
