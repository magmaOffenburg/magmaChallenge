package magma.util.scenegraph;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public interface ITransformNode extends IBaseNode
{
	float[] getLocalTransformation();

	Vector3D getPosition();

	Rotation getOrientation();
}
