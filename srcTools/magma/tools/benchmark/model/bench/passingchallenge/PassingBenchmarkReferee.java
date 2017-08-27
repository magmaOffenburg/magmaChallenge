package magma.tools.benchmark.model.bench.passingchallenge;

import java.awt.Color;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import com.sun.javafx.geom.Vec2d;

import magma.common.spark.PlayMode;
import magma.monitor.command.IServerCommander;
import magma.monitor.worldmodel.IMonitorWorldModel;
import magma.monitor.worldmodel.ISoccerAgent;
import magma.tools.benchmark.model.bench.BenchmarkRefereeBase;
import magma.tools.benchmark.model.bench.RunInformation;
import magma.tools.benchmark.model.bench.SinglePlayerLauncher;
import magma.util.roboviz.RoboVizDraw;
import magma.util.roboviz.RoboVizParameters;


public class PassingBenchmarkReferee extends BenchmarkRefereeBase{
	
	private static final String ROBOVIZ_GROUP = "PassingChallenge.area";
	
	private final RoboVizDraw roboVizDraw;

	private static final double touchRadius = 0.5;
	
	private static final double passRadius = 1.0;
	
	private static final double timeLimit = 80.0;
	
	private static final double minDistX = 3.0f;
	
	private static final double minBallTravelDist = 2.5;
	
	//private boolean touched[] = new boolean[PassingBenchmark.PLAYERS];
	
	
	private AgentState agentState[] = new AgentState[PassingBenchmark.PLAYERS];

	
	private enum AgentState {
		NEUTRAL, TOUCHED, PASSED 
	}
	
	private boolean goal;
	

	private Vec2d ballTouchedPos;
	private Vec2d lastBallPos;
	private double lastTime;

	private double lastBallSpeed;
	private int lastAgentTouched;

	private int delay = 125;

	//private static final double ballSpeedThreshold = 1.0;
	
	public PassingBenchmarkReferee(IMonitorWorldModel mWorldModel, IServerCommander serverCommander, String serverPid,
			SinglePlayerLauncher launcher, float runTime, RunInformation runInfo, String roboVizServer) {
		
		super(mWorldModel, serverCommander, serverPid, launcher, runTime, runInfo, false);
		
		roboVizDraw = new RoboVizDraw(new RoboVizParameters(true, roboVizServer, RoboVizDraw.DEFAULT_PORT));

		goal = false;
		
		for(int i = 0; i < PassingBenchmark.PLAYERS; i++)
		{
			agentState[i] = AgentState.NEUTRAL;
		}
		lastAgentTouched = -1;
	}

	@Override
	protected boolean onStartBenchmark() {
		state = RefereeState.CONNECTED;
		return true;
		
	}

	@Override
	protected boolean onDuringBenchmark() {
		
		if(state == RefereeState.CONNECTED)
		{
			if(worldModel.getSoccerAgents().size() == PassingBenchmark.PLAYERS)
			{
				if (delay > 0) {
					delay--;
					return false;
				}
				state = RefereeState.BEAMED;
			}
			else
			{
				return false;
			}
			
		}
		else if(state == RefereeState.BEAMED)
		{
			if(validStartingPosition())
			{
				state = RefereeState.STARTED;

				Vector3D ballPos3D = worldModel.getBall().getPosition();
				lastBallPos = new Vec2d(ballPos3D.getX(), ballPos3D.getY());
				ballTouchedPos = lastBallPos;
				lastTime = worldModel.getTime();
				lastBallSpeed = 0.0;
				serverCommander.setPlaymode(PlayMode.PLAY_ON);
			}
			else
			{
				return true;
			}
		}
		else if(state == RefereeState.STARTED) {
		validTouch();
		drawHasTouched();
			if(worldModel.getTime() > timeLimit)
			{
				serverCommander.setPlaymode(PlayMode.GAME_OVER);
			}
			
			if(worldModel.getPlayMode() == PlayMode.GOAL_LEFT)
			{
				goal = true;
				return true;
			}
			
			if(worldModel.getPlayMode() != PlayMode.BEFORE_KICK_OFF && worldModel.getPlayMode() != PlayMode.PLAY_ON &&
					worldModel.getPlayMode() != PlayMode.GAME_OVER)
			{
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
		
		Vec2d agentPos = new Vec2d(agentPos3D.getX(), agentPos3D.getY());
		Vec2d ballPos = new Vec2d(ballPos3D.getX(), ballPos3D.getY());
		
		double distToBall = lastBallPos.distance(agentPos);	
		double deltaTime = worldModel.getTime() - lastTime;
		double ballSpeed = ballPos.distance(lastBallPos)/deltaTime;
		double ballAcc = (ballSpeed - lastBallSpeed)/deltaTime;
		
		lastTime = worldModel.getTime();
		lastBallPos = ballPos;
		lastBallSpeed = ballSpeed;

		//if(distToBall > touchRadius || ballSpeed > ballSpeedThreshold) return;
		if(distToBall > touchRadius || ballAcc <= 0) return;
		
		
		for(int i = 0; i < worldModel.getSoccerAgents().size(); i ++)
		{
			if(i == index) continue;
			if( worldModel.getSoccerAgents().get(i).getPosition().distance(agentPos3D) < passRadius)
			{
				return;
			}
		}
		
		
		
		
		if(lastAgentTouched != -1 && lastAgentTouched != index &&
			agentState[lastAgentTouched] == AgentState.TOUCHED
			)
		{
			if(ballPos.distance(ballTouchedPos) > minBallTravelDist)
			{
				agentState[lastAgentTouched] = AgentState.PASSED;
			}
			else
			{
				agentState[lastAgentTouched] = AgentState.NEUTRAL;
			}
		}
		

		lastAgentTouched = index;
		ballTouchedPos = ballPos;
		if(agentState[index] == AgentState.PASSED) return;		
		
		agentState[index] = AgentState.TOUCHED;
		
		//touched[index] = true;
	}
	

	@Override
	protected void onStopBenchmark() {
		state = RefereeState.STOPPED;	
	}

	public float getTime() {
		if(((nTouched() == (PassingBenchmark.PLAYERS-1) && agentState[lastAgentTouched] == AgentState.TOUCHED && goal)) ||
				(nTouched() == (PassingBenchmark.PLAYERS)))
		{
			return worldModel.getTime();
		}
		else
		{
			float score = 85.0f;
			score -= nTouched();
			if(goal)
			{
				score--;
			}
			return score;
		}
		
		
		
		//return 80.0f;
	}
	
	private boolean validStartingPosition()
	{
		for(int i = 0; i <  worldModel.getSoccerAgents().size(); i++)
		{
			for(int j = i + 1; j <  worldModel.getSoccerAgents().size(); j++ )
			{
				double dist = Math.abs(worldModel.getSoccerAgents().get(i).getPosition().getX() - 
						worldModel.getSoccerAgents().get(j).getPosition().getX());
				if(dist < minDistX)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	private int nTouched()
	{
		int nTouch = 0;
		for (AgentState touch : agentState)
		{
			if(touch == AgentState.PASSED) nTouch++;
		}
		return nTouch;
	}
	
	
	
	private void drawHasTouched()
	{
		for(int i = 0; i < worldModel.getSoccerAgents().size(); i ++)
		{
			Color c = Color.RED;
			Vector3D pos = worldModel.getSoccerAgents().get(i).getPosition();
			if(agentState[i] == AgentState.PASSED)
			{
				c = Color.GREEN;
			}
			else if(agentState[i] == AgentState.TOUCHED)
			{
				c = Color.ORANGE;
			}
			roboVizDraw.drawCircle(ROBOVIZ_GROUP + Integer.toString(i), pos, 0.5f, 2.0f, c);
		}
		
		//Vector3D balltpos = new Vector3D(ballTouchedPos.x, ballTouchedPos.y, worldModel.getBall().getPosition().getZ());
		//roboVizDraw.drawCircle(ROBOVIZ_GROUP + "BALL", balltpos, 0.2f, 2.0f, Color.WHITE);
	}
	
	private int getAgentClosestToBall()
	{
		Vector3D ballPos = worldModel.getBall().getPosition();
		double minDist = 1000.0;
		int index = -1;
		for(ISoccerAgent agent : worldModel.getSoccerAgents())
		{
			double distAgentBall = agent.getPosition().distance(ballPos);
			if(distAgentBall < minDist)
			{
				index = worldModel.getSoccerAgents().indexOf(agent);
				minDist = distAgentBall;
			}
		}
		return index;
	}

}
