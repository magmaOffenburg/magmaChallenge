package magma.monitor.general.impl;

import magma.common.spark.IConnectionConstants;

public class MonitorParameters
{
	private String host;

	private int port;

	private MonitorComponentFactory factory;

	public MonitorParameters(MonitorComponentFactory factory)
	{
		this(IConnectionConstants.SERVER_IP, IConnectionConstants.MONITOR_PORT, factory);
	}

	public MonitorParameters(String host, int port, MonitorComponentFactory factory)
	{
		this.host = host;
		this.port = port;
		this.factory = factory;
	}

	public String getHost()
	{
		return host;
	}

	public int getPort()
	{
		return port;
	}

	public MonitorComponentFactory getFactory()
	{
		return factory;
	}
}
