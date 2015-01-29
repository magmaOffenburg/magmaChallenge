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

/**
 * 
 * @author kdorer
 */
public class BenchmarkAgentProxyServer extends SimsparkAgentProxyServer
{

	/**
	 * @param parameterObject
	 */
	public BenchmarkAgentProxyServer(
			SimsparkAgentProxyServerParameter parameterObject)
	{
		super(parameterObject);
	}

	@Override
	protected AgentProxy createAgentProxy(Socket clientSocket)
	{
		if (agentProxies.size() > 0) {
			// for benchmarks we only allow a single agent to connect
			System.out.println("Already one agent connected!");
			return null;
		}
		return new BenchmarkAgentProxy(clientSocket, ssHost, ssPort, showMessages);
	}

	/**
	 * @return the bothLegsOffGround
	 */
	public int getBothLegsOffGround()
	{
		if (agentProxies.isEmpty()) {
			return 0;
		}
		return ((BenchmarkAgentProxy) agentProxies.get(0)).getBothLegsOffGround();
	}

	/**
	 * @return the bothLegsOffGround
	 */
	public int getOneLegOffGround()
	{
		if (agentProxies.isEmpty()) {
			return 0;
		}
		return ((BenchmarkAgentProxy) agentProxies.get(0)).getOneLegOffGround();
	}

	/**
	 * @return the bothLegsOffGround
	 */
	public int getNoLegOffGround()
	{
		if (agentProxies.isEmpty()) {
			return 0;
		}
		return ((BenchmarkAgentProxy) agentProxies.get(0)).getNoLegOffGround();
	}

	/**
	 * @return the legOnGround
	 */
	public int getLegOnGround()
	{
		if (agentProxies.isEmpty()) {
			return 0;
		}
		return ((BenchmarkAgentProxy) agentProxies.get(0)).getLegOnGround();
	}
}
