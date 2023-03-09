package magma.monitor.command;

public enum Team
{
	LEFT("Left"),
	RIGHT("Right"),
	RANDOM("None");

	private final String text;

	Team(String text)
	{
		this.text = text;
	}

	@Override
	public String toString()
	{
		return this.text;
	}
}
