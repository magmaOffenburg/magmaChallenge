package magma.monitor.referee;

public interface IReferee
{
	enum RefereeState
	{
		CREATED,
		CONNECTED,
		BEAMED,
		STARTED,
		STOPPED,
		FAILED
	}

	boolean decide();

	RefereeState getState();
}
