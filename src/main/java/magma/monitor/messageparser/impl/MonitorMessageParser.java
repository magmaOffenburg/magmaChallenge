package magma.monitor.messageparser.impl;

import hso.autonomy.util.symboltreeparser.SymbolTreeParser;
import java.io.UnsupportedEncodingException;
import java.util.List;
import magma.monitor.messageparser.IMonitorMessageParser;
import magma.monitor.messageparser.ISimulationState;
import magma.util.scenegraph.impl.BaseNode;
import magma.util.scenegraph.impl.SceneGraph;
import magma.util.scenegraph.impl.SceneGraphHeader;

public class MonitorMessageParser implements IMonitorMessageParser
{
	private SimulationState simulationState;

	private SceneGraph sceneGraph;

	private SymbolTreeParser treeParser;

	private RSGConverter rsgConverter;

	public MonitorMessageParser()
	{
		treeParser = new SymbolTreeParser();
		rsgConverter = new RSGConverter();
	}

	@Override
	public ISimulationState getSimulationState()
	{
		return simulationState;
	}

	@Override
	public SceneGraph getSceneGraph()
	{
		return sceneGraph;
	}

	@Override
	public void update(byte[] message)
	{
		try {
			update(new String(message, 0, message.length, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void update(String content)
	{
		simulationState = null;
		sceneGraph = null;

		try {
			List<Object> root = treeParser.parse(content);

			// We expect three top level children:
			// 1. the simulation state
			// 2. the scene graph header
			// 3. the scene graph
			SimulationState currentState = rsgConverter.convertSimulationState((List<Object>) root.get(0));
			SceneGraphHeader graphHeader = rsgConverter.convertSceneGraphHeader((List<Object>) root.get(1));
			BaseNode graphRoot = rsgConverter.convertSceneGraphNode((List<Object>) root.get(2));

			simulationState = currentState;
			sceneGraph = new SceneGraph(graphHeader, graphRoot);

			// System.out.println(sceneGraph);
			// System.out.println(content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
