package magma.tools.benchmark.model;

public interface ITeamResult extends ISingleResult 
{
	void addResult(ISingleResult result);

	void replaceResult(ISingleResult result);

	int getFallenCount();

	int getPenaltyCount();

	int size();

	String getName();

	ISingleResult createSingleResult();
}