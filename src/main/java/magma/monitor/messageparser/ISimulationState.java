package magma.monitor.messageparser;

import java.util.List;
import magma.common.spark.Foul;

public interface ISimulationState
{
	Float getFieldLength();

	Float getFieldWidth();

	Float getFieldHeight();

	Float getGoalWidth();

	Float getGoalDepth();

	Float getGoalHeight();

	Float getBorderSize();

	Float getFreeKickDistance();

	Float getWaitBeforeKickOff();

	Float getAgentRadius();

	Float getBallRadius();

	Float getBallMass();

	Float getRuleGoalPauseTime();

	Float getRuleKickInPauseTime();

	Float getRuleHalfTime();

	String[] getPlayModes();

	Float getTime();

	String getLeftTeam();

	String getRightTeam();

	Integer getHalf();

	Integer getLeftScore();

	Integer getRightScore();

	Integer getPlayMode();

	Float getPassModeMinOppBallDist();

	Float getPassModeDuration();

	Float getPassModeScoreWaitLeft();

	Float getPassModeScoreWaitRight();

	List<Foul> getFouls();
}
