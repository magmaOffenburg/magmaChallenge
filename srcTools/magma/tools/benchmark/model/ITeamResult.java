package magma.tools.benchmark.model;

public interface ITeamResult
{
	void addResult(ISingleResult result);

	float getAverageScore();

	int getFallenCount();

	boolean isValid();

	int size();

	String getName();

	String getStatusText();
}