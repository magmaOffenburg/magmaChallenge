package magma.monitor.general;

import hso.autonomy.util.connection.IServerConnection;
import magma.monitor.command.IServerCommander;
import magma.monitor.messageparser.IMonitorMessageParser;
import magma.monitor.referee.IReferee;
import magma.monitor.worldmodel.IMonitorWorldModel;

public interface IMonitorRuntime
{
	IServerConnection getServerConnection();

	IMonitorMessageParser getMessageParser();

	IServerCommander getServerCommander();

	IMonitorWorldModel getWorldModel();

	IReferee getReferee();

	boolean addRuntimeListener(IMonitorRuntimeListener listener);

	boolean removeRuntimeListener(IMonitorRuntimeListener listener);
}
