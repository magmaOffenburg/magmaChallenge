package magma.tools.benchmark.model.bench.passingchallenge;

import java.awt.Color;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import magma.common.spark.PlayMode;
import magma.monitor.command.IServerCommander;
import magma.monitor.worldmodel.IMonitorWorldModel;
import magma.monitor.worldmodel.ISoccerAgent;
import magma.tools.benchmark.model.bench.BenchmarkRefereeBase;
import magma.tools.benchmark.model.bench.RunInformation;
import magma.tools.benchmark.model.bench.SinglePlayerLauncher;
import magma.util.roboviz.RoboVizDraw;
import magma.util.roboviz.RoboVizParameters;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class PassingBenchmarkReferee extends BenchmarkRefereeBase
{
	private static final String ROBOVIZ_GROUP = "PassingChallenge.area";

	private final RoboVizDraw roboVizDraw;

	private static final double TOUCH_RADIUS = 0.5;

	private static final double PASSING_RADIUS = 1.0;

	private static final double TIME_LIMIT = 80.0;

	private static final double MIN_X_DISTANCE = 3.0f;

	private static final double MIN_BALL_TRAVEL_DISTANCE = 2.5;

	private AgentState agentStates[] = new AgentState[PassingBenchmark.PLAYERS];

	private enum AgentState { NEUTRAL, TOUCHED, PASSED }

	private boolean goal;

	private Vector2D ballTouchedPos;

	private Vector2D lastBallPos;

	private double lastTime;

	private double lastBallSpeed;

	private int lastAgentTouched;

	private int delay = 125;

	public PassingBenchmarkReferee(IMonitorWorldModel mWorldModel, IServerCommander serverCommander, String serverPid,
			SinglePlayerLauncher launcher, float runTime, RunInformation runInfo, String roboVizServer)
	{
		super(mWorldModel, serverCommander, serverPid, launcher, runTime, runInfo, false);

		roboVizDraw = new RoboVizDraw(new RoboVizParameters(true, roboVizServer, RoboVizDraw.DEFAULT_PORT, 1));

		goal = false;

		for (int i = 0; i < PassingBenchmark.PLAYERS; i++) {
			agentStates[i] = AgentState.NEUTRAL;
		}
		lastAgentTouched = -1;
	}

	@Override
	protected boolean onStartBenchmark()
	{
		state = RefereeState.CONNECTED;
		return true;
	}

	@Override
	protected boolean onDuringBenchmark()
	{
		if (state == RefereeState.CONNECTED) {
			if (worldModel.getSoccerAgents().size() == PassingBenchmark.PLAYERS) {
				if (delay > 0) {
					delay--;
					return false;
				}
				state = RefereeState.BEAMED;
			} else {
				return false;
			}
		} else if (state == RefereeState.BEAMED) {
			if (validStartingPosition()) {
				state = RefereeState.STARTED;

				Vector3D ballPos3D = worldModel.getBall().getPosition();
				lastBallPos = new Vector2D(ballPos3D.getX(), ballPos3D.getY());
				ballTouchedPos = lastBallPos;
				lastTime = worldModel.getTime();
				lastBallSpeed = 0.0;
				serverCommander.setPlaymode(PlayMode.PLAY_ON);
			} else {
				return true;
			}
		} else if (state == RefereeState.STARTED) {
			validTouch();
			drawHasTouched();
			if (worldModel.getTime() > TIME_LIMIT) {
				serverCommander.setPlaymode(PlayMode.GAME_OVER);
			}

			if (worldModel.getPlayMode() == PlayMode.GOAL_LEFT) {
				goal = true;
				return true;
			}

			if (worldModel.getPlayMode() != PlayMode.BEFORE_KICK_OFF && worldModel.getPlayMode() != PlayMode.PLAY_ON &&
					worldModel.getPlayMode() != PlayMode.GAME_OVER) {
				return true;
			}

			if (state == RefereeState.STARTED && worldModel.getPlayMode() == PlayMode.GAME_OVER) {
				return true;
			}

			return false;
		}
		return false;
	}

	private void validTouch()
	{
		int index = getAgentClosestToBall();

		Vector3D agentPos3D = worldModel.getSoccerAgents().get(index).getPosition();
		Vector3D ballPos3D = worldModel.getBall().getPosition();

		Vector2D agentPos = new Vector2D(agentPos3D.getX(), agentPos3D.getY());
		Vector2D ballPos = new Vector2D(ballPos3D.getX(), ballPos3D.getY());

		double distToBall = lastBallPos.distance(agentPos);
		double deltaTime = worldModel.getTime() - lastTime;
		double ballSpeed = ballPos.distance(lastBallPos) / deltaTime;
		double ballAcc = (ballSpeed - lastBallSpeed) / deltaTime;

		lastTime = worldModel.getTime();
		lastBallPos = ballPos;
		lastBallSpeed = ballSpeed;

		if (distToBall > TOUCH_RADIUS || ballAcc <= 0) {
			return;
		}

		for (int i = 0; i < worldModel.getSoccerAgents().size(); i++) {
			if (i == index)
				continue;
			if (worldModel.getSoccerAgents().get(i).getPosition().distance(agentPos3D) < PASSING_RADIUS) {
				return;
			}
		}

		if (lastAgentTouched != -1 && lastAgentTouched != index &&
				agentStates[lastAgentTouched] == AgentState.TOUCHED) {
			if (ballPos.distance(ballTouchedPos) > MIN_BALL_TRAVEL_DISTANCE) {
				agentStates[lastAgentTouched] = AgentState.PASSED;
			} else {
				agentStates[lastAgentTouched] = AgentState.NEUTRAL;
			}
		}

		lastAgentTouched = index;
		ballTouchedPos = ballPos;
		if (agentStates[index] == AgentState.PASSED) {
			return;
		}

		agentStates[index] = AgentState.TOUCHED;
	}

	@Override
	protected void onStopBenchmark()
	{
		state = RefereeState.STOPPED;
	}

	public float getTime()
	{
		int touches = getNumberOfTouches();
		if (((touches == (PassingBenchmark.PLAYERS - 1) && agentStates[lastAgentTouched] == AgentState.TOUCHED &&
					goal)) ||
				(touches == (PassingBenchmark.PLAYERS))) {
			return worldModel.getTime();
		} else {
			float score = 85.0f;
			score -= touches;
			if (goal) {
				score--;
			}
			return score;
		}
	}

	private boolean validStartingPosition()
	{
		List<? extends ISoccerAgent> agents = worldModel.getSoccerAgents();
		for (int i = 0; i < agents.size(); i++) {
			for (int j = i + 1; j < agents.size(); j++) {
				double distance = Math.abs(agents.get(i).getPosition().getX() - agents.get(j).getPosition().getX());
				if (distance < MIN_X_DISTANCE) {
					return false;
				}
			}
		}
		return true;
	}

	private int getNumberOfTouches()
	{
		int touches = 0;
		for (AgentState touch : agentStates) {
			if (touch == AgentState.PASSED)
				touches++;
		}
		return touches;
	}

	private void drawHasTouched()
	{
		for (int i = 0; i < worldModel.getSoccerAgents().size(); i++) {
			Color color = Color.RED;
			Vector3D pos = worldModel.getSoccerAgents().get(i).getPosition();
			if (agentStates[i] == AgentState.PASSED) {
				color = Color.GREEN;
			} else if (agentStates[i] == AgentState.TOUCHED) {
				color = Color.ORANGE;
			}
			roboVizDraw.drawCircle(ROBOVIZ_GROUP + i, pos, 0.5f, 2.0f, color);
		}
	}

	private int getAgentClosestToBall()
	{
		Vector3D ballPos = worldModel.getBall().getPosition();
		double minDistance = 1000.0;
		int index = -1;
		for (ISoccerAgent agent : worldModel.getSoccerAgents()) {
			double agentBallDistance = agent.getPosition().distance(ballPos);
			if (agentBallDistance < minDistance) {
				index = worldModel.getSoccerAgents().indexOf(agent);
				minDistance = agentBallDistance;
			}
		}
		return index;
	}
}
