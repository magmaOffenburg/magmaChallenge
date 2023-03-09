package magma.monitor.messageparser.impl;

import java.util.List;
import magma.common.spark.Foul;
import magma.common.spark.Foul.FoulType;
import magma.util.scenegraph.NodeType;
import magma.util.scenegraph.impl.BaseNode;
import magma.util.scenegraph.impl.LightNode;
import magma.util.scenegraph.impl.MeshNode;
import magma.util.scenegraph.impl.SceneGraphHeader;
import magma.util.scenegraph.impl.TransformNode;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class RSGConverter
{
	public SimulationState convertSimulationState(List<Object> node) throws NodeConversionException
	{
		SimulationState state = new SimulationState();

		for (int i = 0; i < node.size(); i++) {
			// We expect all child nodes to be List<Object>s
			List<Object> child = getSymbolNode(node.get(i));
			if (child == null) {
				continue;
			}

			// We expect at least to children in each parameter node
			if (child.size() < 2) {
				continue;
			}

			// Since we only expect parameters in the form:
			// (<parameter> <value> [<value>])
			// all children of the current child node are expected to be strings
			boolean valid = true;
			for (int j = 0; j < child.size(); j++) {
				if (!(child.get(j) instanceof String)) {
					valid = false;
					break;
				}
			}
			if (!valid) {
				continue;
			}

			// We expect the first child of each child node to indicate the name of
			// the parameter
			String param = (String) child.get(0);

			// We expect at lest one further String as value of the parameter
			String value = (String) child.get(1);

			try {
				switch (param) {
				case "time":
					state.setTime(Float.parseFloat(value));
					break;
				case "score_left":
					state.setLeftScore(Integer.parseInt(value));
					break;
				case "score_right":
					state.setRightScore(Integer.parseInt(value));
					break;
				case "play_mode":
					state.setPlayMode(Integer.parseInt(value));
					break;
				case "FieldLength":
					state.setFieldLength(Float.parseFloat(value));
					break;
				case "FieldWidth":
					state.setFieldWidth(Float.parseFloat(value));
					break;
				case "FieldHeight":
					state.setFieldHeight(Float.parseFloat(value));
					break;
				case "GoalWidth":
					state.setGoalWidth(Float.parseFloat(value));
					break;
				case "GoalDepth":
					state.setGoalDepth(Float.parseFloat(value));
					break;
				case "GoalHeight":
					state.setGoalHeight(Float.parseFloat(value));
					break;
				case "BorderSize":
					state.setBorderSize(Float.parseFloat(value));
					break;
				case "FreeKickDistance":
					state.setFreeKickDistance(Float.parseFloat(value));
					break;
				case "WaitBeforeKickOff":
					state.setWaitBeforeKickOff(Float.parseFloat(value));
					break;
				case "AgentRadius":
					state.setAgentRadius(Float.parseFloat(value));
					break;
				case "BallRadius":
					state.setBallRadius(Float.parseFloat(value));
					break;
				case "BallMass":
					state.setBallMass(Float.parseFloat(value));
					break;
				case "RuleGoalPauseTime":
					state.setRuleGoalPauseTime(Float.parseFloat(value));
					break;
				case "RuleKickInPauseTime":
					state.setRuleKickInPauseTime(Float.parseFloat(value));
					break;
				case "RuleHalfTime":
					state.setRuleHalfTime(Float.parseFloat(value));
					break;
				case "play_modes":
					String[] playModes = new String[child.size() - 1];
					playModes[0] = value;
					for (int j = 2; j < child.size(); j++) {
						playModes[j - 1] = (String) child.get(j);
					}
					state.setPlayModes(playModes);
					break;
				case "team_left":
					state.setLeftTeam(value);
					break;
				case "team_right":
					state.setRightTeam(value);
					break;
				case "half":
					state.setHalf(Integer.parseInt(value));
					break;
				case "foul":
					Foul foul = new Foul(state.getTime(), parseInteger(child, 1),
							FoulType.values()[parseInteger(child, 2)], parseInteger(child, 3), parseInteger(child, 4));
					state.addFoul(foul);
					break;
				case "PassModeMinOppBallDist":
					state.setPassModeMinOppBallDist(Float.parseFloat(value));
					break;
				case "PassModeDuration":
					state.setPassModeDuration(Float.parseFloat(value));
					break;
				case "pass_mode_score_wait_left":
					state.setPassModeScoreWaitLeft(Float.parseFloat(value));
					break;
				case "pass_mode_score_wait_right":
					state.setPassModeScoreWaitRight(Float.parseFloat(value));
					break;
				default:
					// Unknown parameter
					System.err.println("Unknown Parameter: " + child);
					break;
				}

			} catch (NumberFormatException e) {
				System.err.println("Value conversion error in: " + node.get(i));
				e.printStackTrace();
			} catch (Exception e) {
				System.err.println("Unexpected Exception while parsing state parameter in Node: " + node.get(i));
				e.printStackTrace();
			}
		}

		return state;
	}

	public SceneGraphHeader convertSceneGraphHeader(List<Object> node) throws NodeConversionException
	{
		SceneGraphHeader header = new SceneGraphHeader();

		// We expect the scene graph header node to contain 3 values
		if (node.size() < 3) {
			throw new NodeConversionException("Unexpected size of SceneGraphHeader: " + node);
		}

		// Check scene type
		String type = getString(node.get(0));
		if (!"RSG".equals(type) && !"RDS".equals(type)) {
			throw new NodeConversionException("Unexpected scene type: " + node);
		}

		// Check if two further string values exist
		String major = getString(node.get(1));
		String minor = getString(node.get(2));
		if (major == null || minor == null) {
			throw new NodeConversionException("Unexpected SceneGraphHeader format: " + node);
		}

		// Parse Header information
		try {
			header.setType(type);
			header.setMajorVersion(Integer.parseInt(major));
			header.setMinorVersion(Integer.parseInt(minor));
		} catch (NumberFormatException e) {
			throw new NodeConversionException("Value conversion error in: " + node);
		} catch (Exception e) {
			System.err.println("Unexpected Exception while parsing scene graph header of Node: " + node);
			e.printStackTrace();
		}

		return header;
	}

	public BaseNode convertSceneGraphNode(List<Object> node) throws NodeConversionException
	{
		switch (determineNodeType(node)) {
		case LIGHT:
			return convertLightNode(node);
		case MESH:
			return convertMeshNode(node);
		case TRANSFORM:
			return convertTransformNode(node);
		case BASE:
		default:
			return convertBaseNode(node);
		}
	}

	private NodeType determineNodeType(List<Object> node)
	{
		// We expect at least two children to be able to differentiate scene graph
		// nodes
		if (node.size() > 1) {
			// If the first child is a string value and equal to "nd", then the
			// second child is either an type identifier or a SLT List<Object>
			if ("nd".equals(getString(node.get(0)))) {
				String typeString = getString(node.get(1));
				if (typeString != null) {
					// There exists a type identifier, so determine the node type
					return NodeType.determineNodeType(typeString);
				} else {
					List<Object> child = getSymbolNode(node.get(1));
					if (child.size() > 0 && "SLT".equals(getString(child.get(0)))) {
						// There exists a SLT node as second child, therefore it has
						// to be a transform node
						return NodeType.TRANSFORM;
					}
				}
			}
		}

		// By default return a BaseNode node type
		return NodeType.BASE;
	}

	private LightNode convertLightNode(List<Object> node) throws NodeConversionException
	{
		LightNode lightNode = new LightNode();

		// Run through all child nodes to find known configurations
		for (int i = 0; i < node.size(); i++) {
			List<Object> child = getSymbolNode(node.get(i));

			if (child == null) {
				continue;
			}
			if (child.size() < 5) {
				// We expect at least 5 values to be a valid light parameter
				System.err.println("Unexpected Parameter node length while parsing light node: " + child);
				continue;
			}

			String parameter = getString(child.get(0));
			try {
				switch (parameter) {
				case "setDiffuse":
					lightNode.setDiffuse(parseFloatArray(child));
					break;
				case "setAmbient":
					lightNode.setAmbient(parseFloatArray(child));
					break;
				case "setSpecular":
					lightNode.setSpecular(parseFloatArray(child));
					break;
				default:
					// Unknown parameter
					System.err.println("Unknown Parameter node while parsing light node: " + child);
					break;
				}

			} catch (NumberFormatException | NullPointerException e) {
				System.err.println("Value conversion error in: " + child);
				e.printStackTrace();
			} catch (Exception e) {
				System.err.println("Unexpected Exception while parsing light-parameter node: " + child);
				e.printStackTrace();
			}
		}

		return lightNode;
	}

	private MeshNode convertMeshNode(List<Object> node) throws NodeConversionException
	{
		MeshNode meshNode = new MeshNode();

		float radius = 1;
		float length = 1;

		// Run through all child nodes to find known configurations
		for (int i = 0; i < node.size(); i++) {
			List<Object> child = getSymbolNode(node.get(i));

			if (child == null || child.size() <= 0) {
				continue;
			}

			String parameter = getString(child.get(0));
			String value;

			try {
				switch (parameter) {
				case "setVisible":
					value = getString(child.get(1));
					if (value != null) {
						meshNode.setVisible(!"0".equals(value));
					}
					break;
				case "setTransparent":
					meshNode.setTransparent(true);
					break;
				case "load":
					value = getString(child.get(1));
					if (value != null) {
						meshNode.setObjName(value);
					} else {
						System.err.println("second value of load-parameter not valid: " + child);
					}

					if (child.size() > 3) {
						radius = parseFloat(child, 2);
						length = parseFloat(child, 3);
					}
					break;
				case "sSc":
					double x = parseDouble(child, 1) * radius;
					double y = parseDouble(child, 2) * radius;
					double z = parseDouble(child, 3) * length;

					meshNode.setScale(new Vector3D(x, y, z));
					break;
				case "resetMaterials":
				case "sMat":
					String[] newMaterials = new String[child.size() - 1];
					for (int j = 1; j < child.size(); j++) {
						value = getString(child.get(j));
						if (value != null) {
							newMaterials[j - 1] = value;
						} else {
							System.err.println("a material-parameter is not valid: " + child);
						}
					}
					meshNode.setMaterials(newMaterials);
					break;
				default:
					// Unknown parameter
					System.err.println("Unknown Parameter while parsing mesh node: " + child);
					break;
				}
			} catch (NumberFormatException | NullPointerException e) {
				System.err.println("Value conversion error in: " + child);
				e.printStackTrace();
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println("Unexpected parameter node format: " + child);
				e.printStackTrace();
			} catch (Exception e) {
				System.err.println("Unexpected Exception while parsing mesh-parameter node: " + child);
				e.printStackTrace();
			}
		}

		return meshNode;
	}

	private TransformNode convertTransformNode(List<Object> node) throws NodeConversionException
	{
		TransformNode transformNode = new TransformNode();
		List<Object> child;

		// Add all child nodes, expect SLT-nodes to the TransformNode
		for (int i = 0; i < node.size(); i++) {
			child = getSymbolNode(node.get(i));

			if (child == null) {
				continue;
			}

			if (child.size() > 16 && "SLT".equals(child.get(0))) {
				try {
					// int index;
					float[] matrix = new float[16];
					// Parse 4x4 local transform matrix
					// Message is column major so we have to convert it
					for (int j = 0; j < 16; j++) {
						matrix[j] = parseFloat(child, j + 1);
					}

					// If all values of the matrix were successfully converted
					// (no exception occurred), create and set the local
					// transformation of the current TransformNode
					transformNode.setLocalTransform(matrix);
				} catch (NumberFormatException | NullPointerException e) {
					// don't fill up log files with -nan errors (see #76)
					if (!e.getMessage().contains("-nan")) {
						System.err.println("Value conversion error in: " + child);
						e.printStackTrace();
					}
				} catch (Exception e) {
					System.err.println("Unexpected Exception while parsing 4x4 matrix of SLT Node: " + child);
					e.printStackTrace();
				}
			} else {
				// If node is not an SLT node, simply call generic node
				// converter recursively and add the resulting node as child
				transformNode.addChildNode(convertSceneGraphNode(child));
			}
		}

		return transformNode;
	}

	private BaseNode convertBaseNode(List<Object> node) throws NodeConversionException
	{
		BaseNode baseNode = new BaseNode();

		if (node.size() > 0) {
			List<Object> child;

			// Add all sub nodes to the base node
			for (int i = 0; i < node.size(); i++) {
				child = getSymbolNode(node.get(i));

				if (child != null) {
					baseNode.addChildNode(convertSceneGraphNode(child));
				}
			}
		}

		return baseNode;
	}

	@SuppressWarnings("unchecked")
	private List<Object> getSymbolNode(Object obj)
	{
		if (obj instanceof List<?>) {
			return (List<Object>) obj;
		}

		return null;
	}

	private String getString(Object obj)
	{
		if (obj instanceof String) {
			return (String) obj;
		}

		return null;
	}

	private Integer parseInteger(List<Object> node, int child)
	{
		return Integer.parseInt(getString(node.get(child)));
	}

	private Float parseFloat(List<Object> node, int child)
	{
		return Float.parseFloat(getString(node.get(child)));
	}

	private Double parseDouble(List<Object> node, int child)
	{
		return Double.parseDouble(getString(node.get(child)));
	}

	private float[] parseFloatArray(List<Object> node)
	{
		float[] a = new float[4];
		a[0] = parseFloat(node, 1);
		a[1] = parseFloat(node, 2);
		a[2] = parseFloat(node, 3);
		a[3] = parseFloat(node, 4);
		return a;
	}
}
