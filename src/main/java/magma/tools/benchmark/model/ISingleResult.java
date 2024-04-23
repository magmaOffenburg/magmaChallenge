package magma.tools.benchmark.model;

public interface ISingleResult
{
	boolean isFallen();

	/**
	 * @return true if a penalty was assigned with this try
	 */
	boolean hasPenalty();

	boolean isValid();

	String getStatusText();

	double getScore();
}