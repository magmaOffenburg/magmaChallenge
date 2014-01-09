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

package magma.tools.benchmark.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import magma.monitor.general.IMonitorRuntimeListener;
import magma.monitor.general.impl.FactoryParameter;
import magma.monitor.general.impl.MonitorComponentFactory;
import magma.monitor.general.impl.MonitorParameter;
import magma.monitor.general.impl.MonitorRuntime;
import magma.monitor.referee.IReferee.RefereeState;
import magma.monitor.referee.impl.BenchmarkReferee;
import magma.monitor.server.ServerController;
import magma.monitor.server.ServerException;
import magma.tools.SAProxy.impl.SimsparkAgentProxyServer.SimsparkAgentProxyServerParameter;
import magma.util.observer.IObserver;
import magma.util.observer.IPublishSubscribe;
import magma.util.observer.Subject;

/**
 * 
 * @author kdorer
 */
public class BenchmarkMain implements IMonitorRuntimeListener, IModelReadWrite
{
	/** observers of model */
	private final transient IPublishSubscribe<IModelReadOnly> observer;

	private BenchmarkAgentProxyServer proxy;

	private MonitorRuntime monitor;

	private ServerController server;

	private List<TeamResult> results;

	private RunThread runThread;

	private int currentTeam;

	private String statusText;

	public BenchmarkMain()
	{
		observer = new Subject<IModelReadOnly>();
		runThread = null;
		results = new ArrayList<TeamResult>();
		server = new ServerController(3100, 3200, false);
		statusText = "";
	}

	public void resetModel()
	{
		results = new ArrayList<TeamResult>();
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
				config.getTrainerPort(), false);

		runThread = new RunThread(config, teamConfig);
		runThread.start();
	}

	/**
	 * @return
	 */
	public boolean isRunning()
	{
		return runThread != null;
	}

	/**
	 * 
	 */
	private void collectResults()
	{
		float avgSpeed = 0;
		float bothLegsOffGround = 0;
		boolean fallen = false;
		boolean valid = false;
		if (monitor != null) {
			BenchmarkReferee referee = (BenchmarkReferee) monitor.getReferee();
			if (referee.getState() == RefereeState.STOPPED) {
				avgSpeed = referee.getAverageSpeed();
				bothLegsOffGround = proxy.getBothLegsOffGround() / (10 * 50f);
				fallen = referee.isHasFallen();
				valid = true;
			} else {
				statusText += referee.getStatusText();
			}
		}
		getCurrentResult().addResult(
				new SingleRunResult(valid, avgSpeed, bothLegsOffGround, fallen,
						statusText));
		observer.onStateChange(this);
	}

	/**
	 * @return
	 */
	private TeamResult getCurrentResult()
	{
		return results.get(currentTeam);
	}

	/**
	 * @param args
	 */
	private void startProxy(BenchmarkConfiguration config)
	{
		// start proxy to get force information
		SimsparkAgentProxyServerParameter parameterObject = new SimsparkAgentProxyServerParameter(
				config.getAgentPort(), config.getServerIP(),
				config.getServerPort(), false);
		proxy = new BenchmarkAgentProxyServer(parameterObject);
		proxy.start();
	}

	private boolean startTrainer(BenchmarkConfiguration config,
			TeamConfiguration teamConfig)
	{
		MonitorComponentFactory factory = new MonitorComponentFactory(
				new FactoryParameter(null, config.getServerIP(),
						config.getAgentPort(), teamConfig.getPath(), null,
						teamConfig.getLaunch(), null, config.getRuntime()));

		monitor = new MonitorRuntime(new MonitorParameter(config.getServerIP(),
				config.getTrainerPort(), Level.WARNING, 3, factory));

		monitor.addRuntimeListener(this);

		int tryCount = 0;
		boolean connected = false;
		while (!connected && tryCount < 10) {
			connected = monitor.startMonitor();
			if (!connected) {
				try {
					Thread.sleep(200);
					tryCount++;
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
	public List<TeamResult> getTeamResults()
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

		@Override
		public void run()
		{
			// start the proxy through which players should connect
			startProxy(config);

			int avgRuns = config.getAverageOutRuns();

			for (TeamConfiguration currentTeamConfig : teamConfig) {
				results.add(new TeamResult(currentTeamConfig.getName()));
				stoppedTeam = false;
				while (getCurrentResult().size() < avgRuns && !stopped
						&& !stoppedTeam) {
					try {
						server.startServer();

						boolean success = startTrainer(config, currentTeamConfig);
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

			proxy.shutdown();
			runThread = null;
			observer.onStateChange(BenchmarkMain.this);
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

	public List<TeamConfiguration> loadConfigFile(File file)
			throws InvalidConfigFileException
	{
		ConfigLoader loader = new ConfigLoader();
		return loader.loadConfigFile(file);
	}
}
