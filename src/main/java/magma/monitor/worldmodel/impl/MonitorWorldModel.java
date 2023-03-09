package magma.monitor.worldmodel.impl;

import java.util.ArrayList;
import java.util.List;
import magma.common.spark.Foul;
import magma.common.spark.PlayMode;
import magma.monitor.messageparser.IMonitorMessageParser;
import magma.monitor.messageparser.ISimulationState;
import magma.monitor.worldmodel.IMonitorWorldModel;
import magma.monitor.worldmodel.ISoccerAgent;
import magma.monitor.worldmodel.ISoccerBall;
import magma.util.scenegraph.IBaseNode;
import magma.util.scenegraph.IMeshNode;
import magma.util.scenegraph.ISceneGraph;
import magma.util.scenegraph.impl.SceneGraph;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class MonitorWorldModel implements IMonitorWorldModel
{
	protected SceneGraph sceneGraph;

	protected boolean graphStructureChanged;

	protected Vector3D fieldDimensions;

	protected Vector3D goalDimensions;

	protected float time;

	protected float deltaT;

	protected String leftTeam;

	protected String rightTeam;

	protected int scoreLeft;

	protected int scoreRight;

	protected PlayMode playMode;

	protected int half;

	protected SoccerBall ball;

	protected ArrayList<SoccerAgent> agents;

	protected ArrayList<Foul> fouls;

	public MonitorWorldModel()
	{
		sceneGraph = null;

		fieldDimensions = Vector3D.ZERO;
		goalDimensions = Vector3D.ZERO;

		leftTeam = "";
		rightTeam = "";

		ball = new SoccerBall();
		agents = new ArrayList<>();
		fouls = new ArrayList<>();
	}

	@Override
	public void update(IMonitorMessageParser content)
	{
		ISimulationState stateUpdate = content.getSimulationState();
		ISceneGraph sceneGraphUpdate = content.getSceneGraph();

		if (stateUpdate != null) {
			// Update state
			updateState(stateUpdate);
		}

		if (sceneGraphUpdate != null) {
			// Compare and update state
			if ("RSG".equals(sceneGraphUpdate.getHeader().getType())) {
				sceneGraph = new SceneGraph(sceneGraphUpdate);
				createSimulationObjects();
				graphStructureChanged = true;
			} else if (sceneGraph != null) {
				sceneGraph.update(sceneGraphUpdate);
				refreshSimulationObjects();
				graphStructureChanged = false;
			}
		}
	}

	private void updateState(ISimulationState stateUpdate)
	{
		// Update field dimensions
		Float fieldLength = stateUpdate.getFieldLength();
		Float fieldWidth = stateUpdate.getFieldWidth();
		Float fieldHeight = stateUpdate.getFieldHeight();
		if (fieldLength != null && fieldWidth != null && fieldHeight != null) {
			fieldDimensions = new Vector3D(fieldLength, fieldWidth, fieldHeight);
		}

		// Update goal dimensions
		Float goalDepth = stateUpdate.getGoalDepth();
		Float goalWidth = stateUpdate.getGoalWidth();
		Float goalHeight = stateUpdate.getGoalHeight();
		if (goalDepth != null && goalWidth != null && goalHeight != null) {
			goalDimensions = new Vector3D(goalDepth, goalWidth, goalHeight);
		}

		// Update time
		Float newTime = stateUpdate.getTime();
		if (newTime != null) {
			deltaT = newTime - time;
			time = newTime;
		}

		// Update left team name
		String newLeftTeam = stateUpdate.getLeftTeam();
		if (newLeftTeam != null) {
			leftTeam = newLeftTeam;
		}

		// Update right team name
		String newRightTeam = stateUpdate.getRightTeam();
		if (newRightTeam != null) {
			rightTeam = newRightTeam;
		}

		// Update left score
		Integer newScoreLeft = stateUpdate.getLeftScore();
		if (newScoreLeft != null) {
			scoreLeft = newScoreLeft;
		}

		// Update right score
		Integer newScoreRight = stateUpdate.getRightScore();
		if (newScoreRight != null) {
			scoreRight = newScoreRight;
		}

		// Update play mode
		Integer newPlayMode = stateUpdate.getPlayMode();
		if (newPlayMode != null) {
			if (newPlayMode >= 0 && newPlayMode < PlayMode.values().length) {
				playMode = PlayMode.values()[newPlayMode];
			} else {
				playMode = PlayMode.NONE;
			}
		}

		// Update half
		Integer newHalf = stateUpdate.getHalf();
		if (newHalf != null) {
			half = newHalf;
		}

		// Update fouls
		fouls.addAll(stateUpdate.getFouls());
	}

	private void createSimulationObjects()
	{
		ArrayList<IBaseNode> topLevelNodes = sceneGraph.getRootNode().getChildren();

		if (topLevelNodes == null || topLevelNodes.size() < 36) {
			System.err.println("Unexpected number of top level nodes in scene graph");
			return;
		}

		// Reset SoccerBall
		ball.setGraphRoot(topLevelNodes.get(35));

		// Create SoccerAgents
		ArrayList<SoccerAgent> newAgents = new ArrayList<>();

		for (int i = 36; i < topLevelNodes.size(); i++) {
			IBaseNode currentNode = topLevelNodes.get(i);
			String teamName;
			if (currentNode.getChildren() != null && currentNode.getChildren().size() > 0) {
				if (currentNode.getNode(IMeshNode.class, "materials", "matLeft", false) != null) {
					teamName = leftTeam;
				} else {
					teamName = rightTeam;
				}

				newAgents.add(new SoccerAgent(currentNode, teamName));
			}
		}

		agents = newAgents;
	}

	private void refreshSimulationObjects()
	{
		// Refresh SoccerBall
		ball.refresh(deltaT);

		// Refresh SoccerAgents
		for (SoccerAgent agent : agents) {
			agent.refresh(deltaT);
		}
	}

	@Override
	public SceneGraph getSceneGraph()
	{
		return sceneGraph;
	}

	@Override
	public boolean hasSceneGraphStructureChanged()
	{
		return graphStructureChanged;
	}

	@Override
	public Vector3D getFieldDimensions()
	{
		return fieldDimensions;
	}

	@Override
	public Vector3D getGoalDimensions()
	{
		return goalDimensions;
	}

	@Override
	public float getTime()
	{
		return time;
	}

	@Override
	public String getLeftTeamName()
	{
		return leftTeam;
	}

	@Override
	public String getRightTeamName()
	{
		return rightTeam;
	}

	@Override
	public int getScoreLeft()
	{
		return scoreLeft;
	}

	@Override
	public int getScoreRight()
	{
		return scoreRight;
	}

	@Override
	public PlayMode getPlayMode()
	{
		return playMode;
	}

	@Override
	public int getHalf()
	{
		return half;
	}

	@Override
	public ISoccerBall getBall()
	{
		return ball;
	}

	@Override
	public List<? extends ISoccerAgent> getSoccerAgents()
	{
		return agents;
	}

	@Override
	public List<Foul> getFouls()
	{
		return fouls;
	}
}
