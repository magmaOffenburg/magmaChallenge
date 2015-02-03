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

package magma.tools.benchmark.model.proxy;

import java.net.Socket;

import magma.tools.SAProxy.impl.AgentProxy;
import magma.tools.SAProxy.impl.SimsparkAgentProxyServer;
import magma.tools.benchmark.model.bench.RunInformation;

/**
 * 
 * @author kdorer
 */
public class BenchmarkAgentProxyServer extends SimsparkAgentProxyServer
{
	private RunInformation runInfo;

	/**
	 * @param parameterObject
	 */
	public BenchmarkAgentProxyServer(
			SimsparkAgentProxyServerParameter parameterObject)
	{
		super(parameterObject);
		runInfo = new RunInformation();
	}

	@Override
	protected AgentProxy createAgentProxy(Socket clientSocket)
	{
		if (agentProxies.size() > 0) {
			// for benchmarks we only allow a single agent to connect
			System.out.println("Already one agent connected!");
			return null;
		}
		BenchmarkAgentProxy benchmarkAgentProxy = new BenchmarkAgentProxy(
				clientSocket, ssHost, ssPort, showMessages);
		benchmarkAgentProxy.updateProxy(runInfo);
		return benchmarkAgentProxy;
	}

	public void updateProxy(RunInformation runInfo)
	{
		this.runInfo = runInfo;
		BenchmarkAgentProxy agentProxy = getAgentProxy();
		if (agentProxy != null) {
			agentProxy.updateProxy(runInfo);
		}
	}

	/**
	 * @return the bothLegsOffGround
	 */
	public int getBothLegsOffGround()
	{
		if (agentProxies.isEmpty()) {
			return 0;
		}
		return getAgentProxy().getBothLegsOffGround();
	}

	/**
	 * @return the bothLegsOffGround
	 */
	public int getOneLegOffGround()
	{
		if (agentProxies.isEmpty()) {
			return 0;
		}
		return getAgentProxy().getOneLegOffGround();
	}

	/**
	 * @return the bothLegsOffGround
	 */
	public int getNoLegOffGround()
	{
		if (agentProxies.isEmpty()) {
			return 0;
		}
		return getAgentProxy().getNoLegOffGround();
	}

	/**
	 * @return the legOnGround
	 */
	public int getLegOnGround()
	{
		if (agentProxies.isEmpty()) {
			return 0;
		}
		return getAgentProxy().getLegOnGround();
	}

	protected BenchmarkAgentProxy getAgentProxy()
	{
		if (agentProxies.isEmpty()) {
			return null;
		}

		return (BenchmarkAgentProxy) agentProxies.get(0);
	}
}
