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

package magma.tools.benchmark;

import java.util.logging.Level;

import magma.monitor.general.impl.FactoryParameter;
import magma.monitor.general.impl.MonitorComponentFactory;
import magma.monitor.general.impl.MonitorParameter;
import magma.monitor.general.impl.MonitorRuntime;
import magma.monitor.referee.impl.BenchmarkReferee;
import magma.monitor.server.ServerController;
import magma.tools.SAProxy.SAProxy;
import magma.tools.SAProxy.impl.SimsparkAgentProxyServer.SimsparkAgentProxyServerParameter;

/**
 * 
 * @author kdorer
 */
public class BenchmarkMain
{

	private BenchmarkAgentProxyServer proxy;

	private MonitorRuntime monitor;

	private ServerController server;

	private int resultCount;

	private float averageSpeed;

	private float averageOffGround;

	/**
	 * Instantiates and starts the Simspark agent proxy.
	 * 
	 * @param args Command line arguments <br>
	 *        <table>
	 *        <tr>
	 *        <td>--proxyport=</td>
	 *        <td>Proxy server port</td>
	 *        </tr>
	 *        <tr>
	 *        <td>--server=</td>
	 *        <td>Simspark server IP</td>
	 *        </tr>
	 *        <tr>
	 *        <td>--serverport=</td>
	 *        <td>Simspark server Port</td>
	 *        </tr>
	 *        <tr>
	 *        <td>--verbose</td>
	 *        <td>Shows the messages</td>
	 *        </tr>
	 *        </table>
	 */
	public static void main(String[] args)
	{
		BenchmarkMain starter = new BenchmarkMain();
		starter.start(args);
		starter.stop();
	}

	public BenchmarkMain()
	{
		server = new ServerController(3100, 3200, false);
		resultCount = 0;
		averageSpeed = 0;
	}

	public void start(String[] args)
	{
		// start the proxy through which players should connect
		startProxy(args);

		while (resultCount < 3) {
			try {
				server.startServer();

				startTrainer(args);

				collectResults();

			} catch (RuntimeException e) {
				e.printStackTrace();
			} finally {
				server.stopServer();
			}
		}

		System.out.println("Overall Average Speed: " + averageSpeed);
		System.out
				.println("Overall Average Feet off ground: " + averageOffGround);
	}

	/**
	 * 
	 */
	private void collectResults()
	{
		BenchmarkReferee referee = (BenchmarkReferee) monitor.getReferee();
		float avgSpeed = referee.getAverageSpeed();
		averageSpeed = (averageSpeed * resultCount + avgSpeed)
				/ (resultCount + 1);
		System.out.println("avgspeed: " + avgSpeed);

		int bothLegsOffGround = proxy.getBothLegsOffGround();
		System.out.println("legs off ground: " + bothLegsOffGround);
		int legOnGround = proxy.getLegOnGround();
		if (legOnGround > 0) {
			float avgOffGround = bothLegsOffGround / (float) legOnGround;
			averageOffGround = (averageOffGround * resultCount + avgOffGround)
					/ (resultCount + 1);
			System.out.println("percentage: " + avgOffGround);
		}
		System.out.println();
		resultCount++;
	}

	/**
	 * @param args
	 */
	private void startProxy(String[] args)
	{
		// start proxy to get force information
		SimsparkAgentProxyServerParameter parameterObject = SAProxy
				.parseParameters(args);
		proxy = new BenchmarkAgentProxyServer(parameterObject);
		proxy.start();
	}

	private void startTrainer(String[] args)
	{
		String serverIP = "127.0.0.1";
		String path = "/host/Data/Projekte/RoboCup/Konfigurationen/runChallenge/";
		String launch = "startPlayerRunning.sh";
		MonitorComponentFactory factory = new MonitorComponentFactory(
				new FactoryParameter(null, serverIP, 3110, path, null, launch,
						null, 1));

		monitor = new MonitorRuntime(new MonitorParameter(serverIP, 3200,
				Level.WARNING, 3, factory));

		int tryCount = 0;
		boolean connected = false;
		while (!connected && tryCount < 10) {
			((BenchmarkReferee) monitor.getReferee()).setProxy(proxy);
			connected = monitor.startMonitor();
			if (!connected) {
				try {
					System.out
							.println("connection not possible, sleeping..................");
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 
	 */
	private void stop()
	{
		proxy.shutdown();
	}
}
