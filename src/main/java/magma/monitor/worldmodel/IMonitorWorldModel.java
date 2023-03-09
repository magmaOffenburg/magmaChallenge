package magma.monitor.worldmodel;

import hso.autonomy.util.observer.IObserver;
import java.util.List;
import magma.common.spark.Foul;
import magma.common.spark.PlayMode;
import magma.monitor.messageparser.IMonitorMessageParser;
import magma.util.scenegraph.impl.SceneGraph;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public interface IMonitorWorldModel extends IObserver<IMonitorMessageParser>
{
	SceneGraph getSceneGraph();

	boolean hasSceneGraphStructureChanged();

	Vector3D getFieldDimensions();

	Vector3D getGoalDimensions();

	float getTime();

	String getLeftTeamName();

	String getRightTeamName();

	int getScoreLeft();

	int getScoreRight();

	PlayMode getPlayMode();

	int getHalf();

	ISoccerBall getBall();

	List<? extends ISoccerAgent> getSoccerAgents();

	List<Foul> getFouls();
}
