package magma.tools.benchmark.model.bench.keepawaychallenge;

import magma.monitor.command.IServerCommander;
import magma.monitor.general.impl.FactoryParameter;
import magma.monitor.general.impl.MonitorComponentFactory;
import magma.monitor.referee.IReferee;
import magma.monitor.worldmodel.IMonitorWorldModel;

public class KeepAwayMonitorComponentFactory extends MonitorComponentFactory
{
	public KeepAwayMonitorComponentFactory(FactoryParameter parameterObject)
	{
		super(parameterObject);
	}

	@Override
	public IReferee createReferee(IMonitorWorldModel worldModel,
			IServerCommander serverCommander, int refereeID)
	{
		return super.createReferee(worldModel, serverCommander, refereeID);
	}
}
