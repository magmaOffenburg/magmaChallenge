package magma.util.logging;

import java.io.Serializable;
import java.util.List;
import magma.common.spark.PlaySide;

public class AgentData implements Serializable
{
	public static final long serialVersionUID = 4290507372474569249L;

	public final PlaySide playSide;

	public final List<BehaviorData> behaviors;

	public AgentData(PlaySide playSide, List<BehaviorData> behaviors)
	{
		this.playSide = playSide;
		this.behaviors = behaviors;
	}
}
