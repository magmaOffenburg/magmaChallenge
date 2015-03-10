package magma.tools.benchmark.model;

public interface ISingleResult
{
	/**
	 * @return the fallen
	 */
	boolean isFallen();

	/**
	 * @return true if a penalty was assigned with this try
	 */
	boolean hasPenalty();

	/**
	 * @return the fallen
	 */
	boolean isValid();

	/**
	 * @return
	 */
	String getStatusText();
}