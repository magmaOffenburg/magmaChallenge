package magma.monitor.worldmodel;

import hso.autonomy.util.geometry.Pose3D;
import java.util.Map;

public interface ISoccerAgent extends ISimulationObject
{
	String getTeamName();

	int getPlayerID();

	Pose3D getBodyPartPose(SoccerAgentBodyPart bodyPart);

	Map<SoccerAgentBodyPart, Pose3D> getAllBodyPartPoses();

	String getRobotModel();
}
