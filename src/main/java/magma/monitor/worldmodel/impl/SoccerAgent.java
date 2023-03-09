package magma.monitor.worldmodel.impl;

import hso.autonomy.util.geometry.Pose3D;
import java.util.HashMap;
import java.util.Map;
import magma.monitor.worldmodel.ISoccerAgent;
import magma.monitor.worldmodel.SoccerAgentBodyPart;
import magma.util.scenegraph.IBaseNode;
import magma.util.scenegraph.IMeshNode;
import magma.util.scenegraph.IModelFiles;
import magma.util.scenegraph.ITransformNode;
import magma.util.scenegraph.NodeType;

public class SoccerAgent extends SimulationObject implements ISoccerAgent
{
	private String teamName;

	private int playerID;

	private String robotModel;

	private Map<SoccerAgentBodyPart, Pose3D> bodyPartPoses = new HashMap<>();

	public SoccerAgent(IBaseNode graphRoot, String teamName)
	{
		super(graphRoot);

		this.teamName = teamName;

		if (graphRoot != null) {
			IMeshNode meshNode = graphRoot.getNode(IMeshNode.class, "objName", IModelFiles.NAO_BODY, true);
			if (meshNode != null && meshNode.getMaterials() != null) {
				String[] materials = meshNode.getMaterials();
				for (String material : materials) {
					if (material.startsWith("matNum") && material.length() > 6) {
						String numberStr = material.substring(6);

						try {
							playerID = Integer.parseInt(numberStr);
						} catch (NumberFormatException e) {
							System.out.println("Error parsing playerID in: " + numberStr);
							e.printStackTrace();
						}
					}
				}
				robotModel = meshNode.getObjName();
			}

			refresh(0);
		}
	}

	@Override
	public String getTeamName()
	{
		return teamName;
	}

	public void setTeamName(String teamName)
	{
		this.teamName = teamName;
	}

	@Override
	public int getPlayerID()
	{
		return playerID;
	}

	@Override
	public void refresh(float deltaT)
	{
		if (graphRoot == null || graphRoot.getChildren() == null) {
			return;
		}

		graphRoot.getChildren()
				.stream()
				.filter(childNode -> childNode.getNodeType() == NodeType.TRANSFORM)
				.forEach(childNode -> {
					ITransformNode node = (ITransformNode) childNode;

					checkBodyPart(node, IModelFiles.NAO_HEAD, SoccerAgentBodyPart.HEAD, false);
					checkBodyPart(node, IModelFiles.NAO_BODY, SoccerAgentBodyPart.BODY, true);
					checkBodyPart(node, IModelFiles.LEFT_UPPER_ARM, SoccerAgentBodyPart.LEFT_UPPER_ARM, true);
					checkBodyPart(node, IModelFiles.RIGHT_UPPER_ARM, SoccerAgentBodyPart.RIGHT_UPPER_ARM, true);
					checkBodyPart(node, IModelFiles.LEFT_LOWER_ARM, SoccerAgentBodyPart.LEFT_LOWER_ARM, false);
					checkBodyPart(node, IModelFiles.RIGHT_LOWER_ARM, SoccerAgentBodyPart.RIGHT_LOWER_ARM, false);
					checkBodyPart(node, IModelFiles.LEFT_THIGH, SoccerAgentBodyPart.LEFT_THIGH, false);
					checkBodyPart(node, IModelFiles.RIGHT_THIGH, SoccerAgentBodyPart.RIGHT_THIGH, false);
					checkBodyPart(node, IModelFiles.LEFT_FOOT, SoccerAgentBodyPart.LEFT_FOOT, false);
					checkBodyPart(node, IModelFiles.RIGHT_FOOT, SoccerAgentBodyPart.RIGHT_FOOT, false);
				});

		IBaseNode child = graphRoot.getChildren().get(0);
		if (child.getNodeType() == NodeType.TRANSFORM) {
			position = ((ITransformNode) child).getPosition();
		}
	}

	private void checkBodyPart(ITransformNode node, String model, SoccerAgentBodyPart bodyPart, boolean requiresRegEx)
	{
		if (hasModel(node, model, requiresRegEx)) {
			bodyPartPoses.put(bodyPart, new Pose3D(node.getPosition(), node.getOrientation()));
		}
	}

	private boolean hasModel(ITransformNode node, String model, boolean requiresRegEx)
	{
		return node.getNode(IMeshNode.class, "objName", model, requiresRegEx) != null;
	}

	@Override
	public Pose3D getBodyPartPose(SoccerAgentBodyPart bodyPart)
	{
		return bodyPartPoses.get(bodyPart);
	}

	@Override
	public Map<SoccerAgentBodyPart, Pose3D> getAllBodyPartPoses()
	{
		return bodyPartPoses;
	}

	@Override
	public String getRobotModel()
	{
		return robotModel;
	}
}
