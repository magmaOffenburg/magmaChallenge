package magma.monitor.referee.impl;

import magma.monitor.command.IServerCommander;
import magma.monitor.worldmodel.IMonitorWorldModel;

public class DummyReferee extends RefereeBase
{
	public DummyReferee(IMonitorWorldModel mWorldModel, IServerCommander serverCommander)
	{
		super(mWorldModel, serverCommander, null);
	}

	@Override
	public boolean decide()
	{
		// Dummy Referee does nothing
		return false;
	}
}
