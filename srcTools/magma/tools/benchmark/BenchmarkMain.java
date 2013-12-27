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

	public void start(String[] args)
	{
		startProxy(args);

		// start average out runs

		// start Trainer
		try {
			startTrainer(args);
			BenchmarkReferee referee = (BenchmarkReferee) monitor.getReferee();
			System.out.println("avgspeed: " + referee.getAverageSpeed());
			System.out.println("legs off ground: " + proxy.getBothLegsOffGround());
			System.out.println("percentage: " + proxy.getBothLegsOffGround()
					/ (float) proxy.getLegOnGround());

		} catch (RuntimeException e) {
			e.printStackTrace();
		}
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

		monitor.startMonitor();
	}

	/**
	 * 
	 */
	private void stop()
	{
		proxy.shutdown();
	}
}
