package magma.util.logging;

import java.io.Serializable;

public class BehaviorData implements Serializable
{
	public static final long serialVersionUID = 4295507372474569249L;

	private final String name;

	private final double performs;

	private final double performedPercentage;

	public BehaviorData(String name, int performs, double performedPercentage)
	{
		this.name = name;
		this.performs = performs;
		this.performedPercentage = performedPercentage;
	}

	public String getName()
	{
		return name;
	}

	public double getPerforms()
	{
		return performs;
	}

	public double getPerformedPercentage()
	{
		return performedPercentage;
	}

	@Override
	public String toString()
	{
		return name + " " + performs + " " + performedPercentage + "%";
	}
}
