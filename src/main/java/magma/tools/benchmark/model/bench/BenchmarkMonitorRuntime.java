package magma.tools.benchmark.model.bench;

import hso.autonomy.util.connection.ConnectionException;
import hso.autonomy.util.connection.impl.TCPConnection;
import magma.monitor.general.impl.MonitorParameters;
import magma.monitor.general.impl.MonitorRuntime;

public class BenchmarkMonitorRuntime extends MonitorRuntime
{
	private final boolean isGazebo;

	private boolean shutdown;

	public BenchmarkMonitorRuntime(MonitorParameters parameter, boolean isGazebo)
	{
		super(parameter);
		this.isGazebo = isGazebo;
	}

	@Override
	public void update(byte[] content)
	{
		if (isGazebo) {
			if (referee.decide()) {
				// stopReceiveLoop() doesn't work here, it exits only after the next
				// message is received
				((TCPConnection) connection).closeConnection();
				shutdown = true;
			}

			publishMonitorUpdated();
			return;
		}
		super.update(content);
	}

	@Override
	public void startMonitor() throws ConnectionException
	{
		if (isGazebo) {
			connection.establishConnection();
			while (!shutdown) {
				try {
					Thread.sleep(20);
					update(null);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return;
		}
		super.startMonitor();
	}
}
