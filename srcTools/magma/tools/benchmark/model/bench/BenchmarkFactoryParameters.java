package magma.tools.benchmark.model.bench;

import magma.monitor.general.impl.FactoryParameters;

public class BenchmarkFactoryParameters extends FactoryParameters
{
	private final int agentPort;

	private final String teamName;

	private final String teamPath;

	private final String startScriptName;

	private final int runtime;

	private final float dropHeight;

	public BenchmarkFactoryParameters(String serverIP, int agentPort, String teamName, String teamPath,
			String startScriptName, int runtime, float dropHeight)
	{
		super(null, serverIP);

		this.agentPort = agentPort;
		this.teamName = teamName;
		this.teamPath = teamPath;
		this.startScriptName = startScriptName;
		this.runtime = runtime;
		this.dropHeight = dropHeight;
	}

	public int getAgentPort()
	{
		return agentPort;
	}

	public String getTeamName()
	{
		return teamName;
	}

	public String getTeamPath()
	{
		return teamPath;
	}

	public String getStartScriptName()
	{
		return startScriptName;
	}

	public int getRuntime()
	{
		return runtime;
	}

	public float getDropHeight()
	{
		return dropHeight;
	}
}
