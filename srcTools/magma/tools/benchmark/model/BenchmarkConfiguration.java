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

/**
 * 
 * @author kdorer
 */
public class BenchmarkConfiguration
{
	private String serverIP;

	private int agentPort;

	private int trainerPort;

	private int averageOutRuns;

	private int serverPort;

	/**
	 * 
	 */
	public BenchmarkConfiguration()
	{
		serverIP = "127.0.0.1";
		serverPort = 3100;
		agentPort = 3110;
		trainerPort = 3200;
		averageOutRuns = 3;
	}

	/**
	 * @param serverIP
	 * @param path
	 * @param launch
	 * @param agentPort
	 * @param trainerPort
	 * @param averageOutRuns
	 */
	public BenchmarkConfiguration(String serverIP, int serverPort,
			int agentPort, int trainerPort, int averageOutRuns)
	{
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		this.agentPort = agentPort;
		this.trainerPort = trainerPort;
		this.averageOutRuns = averageOutRuns;
	}

	/**
	 * @return the serverIP
	 */
	public String getServerIP()
	{
		return serverIP;
	}

	/**
	 * @return the agentPort
	 */
	public int getAgentPort()
	{
		return agentPort;
	}

	/**
	 * @return the trainerPort
	 */
	public int getTrainerPort()
	{
		return trainerPort;
	}

	/**
	 * @return the averageOutRuns
	 */
	public int getAverageOutRuns()
	{
		return averageOutRuns;
	}

	/**
	 * @return the serverPort
	 */
	public int getServerPort()
	{
		return serverPort;
	}
}
