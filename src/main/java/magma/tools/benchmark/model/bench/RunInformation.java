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

	/** x coordinate of ball vel n the field */
	private final double ballVelX;

	/** y coordinate of ball vel on the field */
	private final double ballVelY;

	/** z coordinate of ball vel on the field */
	private final double ballVelZ;

	public RunInformation()
	{
		this(0, -13.5, 0, 0, 0);
	}

	public RunInformation(int runID, double beamX, double beamY, double ballX, double ballY)
	{
		this(runID, beamX, beamY, ballX, ballY, 0, 0, 0);
	}

	public RunInformation(int runID, double ballX, double ballY, double ballVelX, double ballVelY, double ballVelZ)
	{
		this(runID, 0, 0, ballX, ballY, ballVelX, ballVelY, ballVelZ);
	}

	public RunInformation(int runID, double beamX, double beamY, double ballX, double ballY, double ballVelX,
			double ballVelY, double ballVelZ)
	{
		super();
		this.runID = runID;
		this.beamX = beamX;
		this.beamY = beamY;
		this.ballX = ballX;
		this.ballY = ballY;
		this.ballVelX = ballVelX;
		this.ballVelY = ballVelY;
		this.ballVelZ = ballVelZ;
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

	public double getBallVelX()
	{
		return ballVelX;
	}

	public double getBallVelY()
	{
		return ballVelY;
	}

	public double getBallVelZ()
	{
		return ballVelZ;
	}
}
