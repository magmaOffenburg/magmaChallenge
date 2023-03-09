package magma.util.scenegraph;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public interface IMeshNode extends IBaseNode
{
	Boolean isVisible();

	Boolean isTransparent();

	String getObjName();

	Vector3D getScale();

	String[] getMaterials();
}
