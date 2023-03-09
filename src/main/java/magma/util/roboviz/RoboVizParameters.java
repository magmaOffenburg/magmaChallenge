package magma.util.roboviz;

public class RoboVizParameters
{
	private final boolean enabled;

	private final String server;

	private final int port;

	private final int agentNum;

	public RoboVizParameters(boolean enabled, int agentNum)
	{
		this(enabled, RoboVizDraw.DEFAULT_HOST, RoboVizDraw.DEFAULT_PORT, agentNum);
	}

	public RoboVizParameters(boolean enabled, String server, int port, int agentNum)
	{
		this.enabled = enabled;
		this.server = server;
		this.port = port;
		this.agentNum = agentNum;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public String getServer()
	{
		return server;
	}

	public int getPort()
	{
		return port;
	}

	public int getAgentNum()
	{
		return agentNum;
	}
}
