package magma.tools.benchmark.model;

public interface ITeamResult extends ISingleResult 
{
	void addResult(ISingleResult result);

	int getFallenCount();

	int getPenaltyCount();

	int size();

	String getName();

}