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

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import magma.tools.SAProxy.impl.AgentProxy;
import magma.tools.SAProxy.impl.SimsparkAgentProxyServer;
import magma.tools.benchmark.model.bench.RunInformation;

/**
 *
 * @author kdorer
 */
public class BenchmarkAgentProxyServer extends SimsparkAgentProxyServer
{
	private final int allowedPlayers;

	private final boolean allowPlayerBeaming;

	private final boolean isGazebo;

	private RunInformation runInfo;

	public BenchmarkAgentProxyServer(SimsparkAgentProxyServerParameter parameterObject, int allowedPlayers,
			boolean allowPlayerBeaming, boolean isGazebo)
	{
		super(parameterObject);
		this.allowedPlayers = allowedPlayers;
		this.allowPlayerBeaming = allowPlayerBeaming;
		this.isGazebo = isGazebo;
		runInfo = new RunInformation();
	}

	@Override
	protected AgentProxy createAgentProxy(Socket clientSocket)
	{
		if (agentProxies.size() > allowedPlayers) {
			System.out.println("Already " + allowedPlayers + " agent(s) connected!");
			return null;
		}
		BenchmarkAgentProxy benchmarkAgentProxy =
				new BenchmarkAgentProxy(clientSocket, ssHost, ssPort, showMessages, allowPlayerBeaming, isGazebo);
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

	public int getBothLegsOffGround()
	{
		if (agentProxies.isEmpty()) {
			return 0;
		}
		return getAgentProxy().getBothLegsOffGround();
	}

	public int getOneLegOffGround()
	{
		if (agentProxies.isEmpty()) {
			return 0;
		}
		return getAgentProxy().getOneLegOffGround();
	}

	public int getNoLegOffGround()
	{
		if (agentProxies.isEmpty()) {
			return 0;
		}
		return getAgentProxy().getNoLegOffGround();
	}

	public int getLegOnGround()
	{
		if (agentProxies.isEmpty()) {
			return 0;
		}
		return getAgentProxy().getLegOnGround();
	}

	public Vector3D getGroundTruthPosition()
	{
		if (agentProxies.isEmpty()) {
			return Vector3D.ZERO;
		}
		return getAgentProxy().getGroundTruthPosition();
	}

	public float getTime()
	{
		if (agentProxies.isEmpty()) {
			return 0;
		}
		return getAgentProxy().getTime();
	}

	protected BenchmarkAgentProxy getAgentProxy()
	{
		if (agentProxies.isEmpty()) {
			return null;
		}

		return (BenchmarkAgentProxy) agentProxies.get(0);
	}
}
