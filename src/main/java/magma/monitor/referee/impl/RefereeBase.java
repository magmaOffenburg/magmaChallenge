package magma.monitor.referee.impl;

import hso.autonomy.util.misc.UnixCommandUtil;
import hso.autonomy.util.timing.AlarmTimer;
import hso.autonomy.util.timing.ITriggerReceiver;
import java.io.IOException;
import magma.monitor.command.IServerCommander;
import magma.monitor.referee.IReferee;
import magma.monitor.worldmodel.IMonitorWorldModel;

public abstract class RefereeBase implements IReferee, ITriggerReceiver
{
	protected IMonitorWorldModel worldModel;

	protected IServerCommander serverCommander;

	/** a watchdog for hanging server connections */
	protected AlarmTimer timer;

	/** the process id of the server, null if unknown */
	private String serverPid;

	protected RefereeState state;

	public RefereeBase(IMonitorWorldModel worldModel, IServerCommander serverCommander, String serverPid)
	{
		this.worldModel = worldModel;
		this.serverCommander = serverCommander;
		this.serverPid = serverPid;
		state = RefereeState.CREATED;
	}

	protected void setupTimer(long timeout)
	{
		stopTimer();
		timer = new AlarmTimer("Alarm-" + timeout, this, timeout);
	}

	protected void stopTimer()
	{
		if (timer != null) {
			timer.stopAlarm();
		}
	}

	@Override
	public boolean trigger(String name)
	{
		try {
			if (serverPid == null || serverPid.isEmpty()) {
				// we did not get a pid, so we assume it is the only server running
				System.err.println("Server hangs, killing all..." + name);
				Runtime.getRuntime().exec("killall -9 rcssserver3d");
			} else {
				// we got the pid from outside
				System.err.println("Server hangs at time " + worldModel.getTime() + ", killing it... " + serverPid +
								   " timer: " + name);
				UnixCommandUtil.killProcessConditional(serverPid, "rcssserver3d");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	public int getNumberOfPlayers()
	{
		return worldModel.getSoccerAgents().size();
	}

	@Override
	public RefereeState getState()
	{
		return state;
	}
}
