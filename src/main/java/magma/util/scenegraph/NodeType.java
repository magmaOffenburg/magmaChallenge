package magma.util.scenegraph;

public enum NodeType
{
	BASE,
	TRANSFORM,
	LIGHT,
	MESH;

	public static NodeType determineNodeType(String type)
	{
		switch (type) {
		case "SMN":
		case "StaticMesh":
			return MESH;
		case "Light":
			return LIGHT;
		case "TRF":
			return TRANSFORM;
		default:
			// We have an unknown node
			return BASE;
		}
	}
}
