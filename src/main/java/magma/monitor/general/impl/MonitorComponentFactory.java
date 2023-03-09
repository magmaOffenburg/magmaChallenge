package magma.monitor.general.impl;

import hso.autonomy.util.connection.IServerConnection;
import hso.autonomy.util.connection.impl.ServerConnection;
import magma.monitor.command.IServerCommander;
import magma.monitor.command.impl.ServerCommander;
import magma.monitor.messageparser.IMonitorMessageParser;
import magma.monitor.messageparser.impl.MonitorMessageParser;
import magma.monitor.referee.IReferee;
import magma.monitor.referee.impl.DummyReferee;
import magma.monitor.worldmodel.IMonitorWorldModel;
import magma.monitor.worldmodel.impl.MonitorWorldModel;

public class MonitorComponentFactory
{
	protected FactoryParameters params;

	public MonitorComponentFactory(FactoryParameters params)
	{
		this.params = params;
	}

	/**
	 * Create a ServerConnection
	 *
	 * @param host Host name/IP
	 * @param port Server port
	 * @return New Server connection
	 */
	public IServerConnection createConnection(String host, int port)
	{
		return new ServerConnection(host, port);
	}

	public IMonitorMessageParser createMessageParser()
	{
		return new MonitorMessageParser();
	}

	/**
	 * Create a ServerCommander
	 *
	 * @param connection - the server connection
	 * @return New ServerCommander
	 */
	public IServerCommander createServerCommander(IServerConnection connection)
	{
		return new ServerCommander(connection);
	}

	public IMonitorWorldModel createWorldModel()
	{
		return new MonitorWorldModel();
	}

	/**
	 * Create a Referee
	 *
	 * @param worldModel - the world model of the monitor
	 * @param serverCommander - the command interface to send server commands
	 * @return New Referee
	 */
	public IReferee createReferee(IMonitorWorldModel worldModel, IServerCommander serverCommander)
	{
		return new DummyReferee(worldModel, serverCommander);
	}
}
