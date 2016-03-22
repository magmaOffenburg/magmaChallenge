package magma.tools.benchmark.model.bench.keepawaychallenge;

import magma.monitor.command.IServerCommander;
import magma.monitor.worldmodel.IMonitorWorldModel;
import magma.tools.benchmark.model.bench.BenchmarkRefereeBase;
import magma.tools.benchmark.model.bench.RunInformation;
import magma.tools.benchmark.model.bench.SinglePlayerLauncher;

public class KeepAwayBenchmarkReferee extends BenchmarkRefereeBase
{
	public KeepAwayBenchmarkReferee(IMonitorWorldModel mWorldModel,
			IServerCommander serverCommander, String serverPid,
			SinglePlayerLauncher launcher, float runTime, float dropHeight,
			RunInformation runInfo, String roboVizServer)
	{
		super(mWorldModel, serverCommander, serverPid, launcher, runTime,
				dropHeight, runInfo);
	}

	@Override
	protected boolean onStartBenchmark()
	{
		return false;
	}

	@Override
	protected boolean onDuringBenchmark()
	{
		return false;
	}

	@Override
	protected void onStopBenchmark()
	{

	}
}
