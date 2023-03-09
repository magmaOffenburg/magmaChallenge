package magma.monitor.command.impl;

import hso.autonomy.util.connection.ConnectionException;
import hso.autonomy.util.connection.IServerConnection;
import magma.common.spark.PlayMode;
import magma.monitor.command.IServerCommander;
import magma.monitor.command.Team;

public class ServerCommander implements IServerCommander
{
	/** the connection to the server */
	private final IServerConnection connection;

	public ServerCommander(IServerConnection connection)
	{
		this.connection = connection;
	}

	@Override
	public void sendMessage(String msg)
	{
		try {
			connection.sendMessage(msg.getBytes());
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void beamBall(float x, float y, float z, float vx, float vy, float vz)
	{
		sendMessage("(ball (pos " + x + " " + y + " " + z + ") (vel " + vx + " " + vy + " " + vz + "))");
	}

	@Override
	public void beamBall(float x, float y)
	{
		beamBall(x, y, 0.042f, 0, 0, 0);
	}

	@Override
	public void dropBall()
	{
		sendMessage("(dropBall)");
	}

	@Override
	public void kickOff()
	{
		kickOff(Team.RANDOM);
	}

	@Override
	public void kickOff(Team k)
	{
		sendMessage("(kickOff " + k + ")");
	}

	@Override
	public void movePlayer(Team team, int playerNumber, float x, float y, float z)
	{
		// Team must be specified
		if (team == Team.RANDOM) {
			return;
		}

		String msg = "(agent (unum " + playerNumber + ") (team " + team + ") (pos " + x + " " + y + " " + z + "))";
		sendMessage(msg);
	}

	@Override
	public void moveRotatePlayer(Team team, int playerNumber, float x, float y, float z, float rot)
	{
		// Team must be specified
		if (team == Team.RANDOM) {
			return;
		}

		// <team> is one of Left, Right.
		// unum and team must both be specified, but the remainder are optional.
		// Zero or more operations may be applied to the specified agent via this
		// command.
		// x, y, z are absolute values in field coordinates. That is, (0,0) is the
		// centre of the field.
		//
		// TODO what units are <rot> in? again, relative or absolute?
		sendMessage("(agent (unum " + playerNumber + ") (team " + team + ") (move " + x + " " + y + " " + z + " " +
					rot + "))");
	}

	@Override
	public void setPlaymode(PlayMode playmode)
	{
		// Don't allow setting none-playmode
		if (playmode == PlayMode.NONE) {
			return;
		}

		// Where <playmode> is one of the predefined, case sensitive, play mode
		// values. Possible playmodes are given as strings in the play_modes
		// expression of the init expression the monitor receives when it
		// connects.
		sendMessage("(playMode " + playmode.getServerString() + ")");
	}

	@Override
	public void killServer()
	{
		sendMessage("(killsim)");
	}

	@Override
	public void requestFullState()
	{
		sendMessage("(reqfullstate)");
	}

	@Override
	public void setTime(float time)
	{
		if (time < 0) {
			return;
		}
		sendMessage("(time " + time + ")");
	}

	@Override
	public void setScore(int left, int right)
	{
		if (left < 0 || right < 0) {
			return;
		}
		sendMessage("(score (left " + left + ") (right " + right + "))");
	}
}
