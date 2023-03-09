package magma.util.scenegraph;

public interface ILightNode extends IBaseNode
{
	float[] getDiffuse();

	float[] getAmbient();

	float[] getSpecular();
}
