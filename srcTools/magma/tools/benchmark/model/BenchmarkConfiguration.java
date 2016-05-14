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
	private final String serverIP;

	private final int agentPort;

	private final int trainerPort;

	private final int serverPort;

	private int averageOutRuns;

	private int runtime;

	private final boolean verbose;

	private boolean isTest;

	private final long randomSeed;

	private final String roboVizServer;

	/**
	 * @param runtime time to run in seconds
	 */
	public BenchmarkConfiguration(String serverIP, int serverPort, int agentPort,
			int trainerPort, int averageOutRuns, int runtime, boolean verbose,
			boolean isTest, long randomSeed, String roboVizServer)
	{
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		this.agentPort = agentPort;
		this.trainerPort = trainerPort;
		this.averageOutRuns = averageOutRuns;
		this.runtime = runtime;
		this.verbose = verbose;
		this.isTest = isTest;
		this.randomSeed = randomSeed;
		this.roboVizServer = roboVizServer;
	}

	public String getServerIP()
	{
		return serverIP;
	}

	public int getAgentPort()
	{
		return agentPort;
	}

	public int getTrainerPort()
	{
		return trainerPort;
	}

	public int getServerPort()
	{
		return serverPort;
	}

	public int getAverageOutRuns()
	{
		return averageOutRuns;
	}

	/**
	 * @return the runtime in seconds
	 */
	public int getRuntime()
	{
		return runtime;
	}

	/**
	 * @return true if debug information should be printed
	 */
	public boolean isVerbose()
	{
		return verbose;
	}

	/**
	 * @return true if this is a test run
	 */
	public boolean isTest()
	{
		return isTest;
	}

	/**
	 * @param value true if this is a test run
	 */
	public void setIsTest(boolean value)
	{
		isTest = value;
	}

	/**
	 * @param averageOutRuns the averageOutRuns to set
	 */
	public void setAverageOutRuns(int averageOutRuns)
	{
		this.averageOutRuns = averageOutRuns;
	}

	/**
	 * @param runtime the runtime to set
	 */
	public void setRuntime(int runtime)
	{
		this.runtime = runtime;
	}

	/**
	 * @return the random seed to use for random numbers
	 */
	public long getRandomSeed()
	{
		return randomSeed;
	}

	public String getRoboVizServer()
	{
		return roboVizServer;
	}
}
