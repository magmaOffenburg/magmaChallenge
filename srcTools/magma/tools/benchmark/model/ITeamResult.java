package magma.tools.benchmark.model;

public interface ITeamResult extends ISingleResult {
	void addResult(ISingleResult result);

	float getAverageScore();

	int getFallenCount();

	int getPenaltyCount();

	@Override
	boolean isValid();

	int size();

	String getName();

	@Override
	String getStatusText();

	/** returns the nth result */
	ISingleResult getResult(int n);
}