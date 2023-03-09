package magma.monitor.messageparser;

import hso.autonomy.util.observer.IObserver;
import magma.util.scenegraph.impl.SceneGraph;

public interface IMonitorMessageParser extends IObserver<byte[]>
{
	ISimulationState getSimulationState();

	SceneGraph getSceneGraph();
}
