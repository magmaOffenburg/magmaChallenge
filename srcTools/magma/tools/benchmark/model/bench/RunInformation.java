/**
 *
 */
package magma.tools.benchmark.model.bench;

/**
 * Contains run specific information like beam coordinates
 * @author kdorer
 */
public class RunInformation
{
	/** the counter for which run it is currently */
	private final int runID;

	/** x coordinate on the field */
	private final double beamX;

	/** y coordinate on the field */
	private final double beamY;

	/** x coordinate of ball on the field */
	private final double ballX;

	/** y coordinate of ball on the field */
	private final double ballY;

	public RunInformation()
	{
		this(0, -13.5, 0, 0, 0);
	}

	public RunInformation(int runID, double beamX, double beamY, double ballX, double ballY)
	{
		super();
		this.runID = runID;
		this.beamX = beamX;
		this.beamY = beamY;
		this.ballX = ballX;
		this.ballY = ballY;
	}

	public int getRunID()
	{
		return runID;
	}

	public double getBeamX()
	{
		return beamX;
	}

	public double getBeamY()
	{
		return beamY;
	}

	public double getBallX()
	{
		return ballX;
	}

	public double getBallY()
	{
		return ballY;
	}
}
