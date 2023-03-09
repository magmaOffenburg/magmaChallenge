package magma.monitor.general.impl;

import magma.common.spark.PlayMode;

public class MonitorRuntimeTest extends MonitorRuntime
{
	private long counter;

	public MonitorRuntimeTest(MonitorParameters parameter)
	{
		super(parameter);
		counter = 0;
	}

	@Override
	public void update(byte[] content)
	{
		super.update(content);
		counter++;
		if (counter < 50) {
			return;
		}

		if (counter == 50) {
			commander.setPlaymode(PlayMode.PLAY_ON);
		}

		if ((counter + 250) % 350 == 0) {
			commander.beamBall(-2, 0.4f, 0, -18, 0, 6.35f);
			// commander.beamBall(-2, 0.5f,0,-22,0,0);
		} else if ((counter + 150) % 400 == 0) {
			commander.beamBall(0, 0);
			commander.setPlaymode(PlayMode.PLAY_ON);
		}
	}
}
