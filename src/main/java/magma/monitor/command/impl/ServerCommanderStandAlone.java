/**
 *
 */
package magma.monitor.command.impl;

import hso.autonomy.util.connection.ConnectionException;
import hso.autonomy.util.connection.IServerConnection;
import hso.autonomy.util.connection.impl.ServerConnection;
import hso.autonomy.util.observer.IObserver;
import magma.common.spark.PlayMode;
import magma.monitor.command.IServerCommander;
import magma.monitor.command.Team;

/**
 * Class that allows to send commands to the server using monitor protocol
 * @author kdorer
 *
 */
public class ServerCommanderStandAlone implements IServerCommander, IObserver<byte[]>
{
	private IServerConnection connection;

	private IServerCommander commander;

	public ServerCommanderStandAlone(String host, int port, int id)
	{
		connection = new ServerConnection(host, port);
		commander = new ServerCommander(connection);
	}

	public void startCommander() throws ConnectionException
	{
		connection.establishConnection();
		connection.attach(this);
		connection.startReceiveLoop();
	}

	public boolean isConnected()
	{
		return connection.isConnected();
	}

	public void stopCommander()
	{
		connection.stopReceiveLoop();
	}

	@Override
	public void beamBall(float x, float y, float z, float vx, float vy, float vz)
	{
		commander.beamBall(x, y, z, vx, vy, vz);
	}

	@Override
	public void beamBall(float x, float y)
	{
		commander.beamBall(x, y);
	}

	@Override
	public void dropBall()
	{
		commander.dropBall();
	}

	@Override
	public void kickOff()
	{
		commander.kickOff();
	}

	@Override
	public void kickOff(Team k)
	{
		commander.kickOff(k);
	}

	@Override
	public void movePlayer(Team team, int playerNumber, float x, float y, float z)
	{
		commander.movePlayer(team, playerNumber, x, y, z);
	}

	@Override
	public void moveRotatePlayer(Team team, int playerNumber, float x, float y, float z, float rot)
	{
		commander.moveRotatePlayer(team, playerNumber, x, y, z, rot);
	}

	@Override
	public void setPlaymode(PlayMode playmode)
	{
		commander.setPlaymode(playmode);
	}

	@Override
	public void killServer()
	{
		commander.killServer();
	}

	@Override
	public void requestFullState()
	{
		commander.requestFullState();
	}

	@Override
	public void sendMessage(String msg)
	{
		commander.sendMessage(msg);
	}

	@Override
	public void setTime(float time)
	{
		commander.setTime(time);
	}

	@Override
	public void setScore(int left, int right)
	{
		commander.setScore(left, right);
	}

	@Override
	public void update(byte[] content)
	{
		// String msg = new String(content);
		// System.out.println(id + "- monitor got message " + msg.substring(0,
		// 10));
	}
}
