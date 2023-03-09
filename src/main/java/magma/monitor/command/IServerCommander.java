package magma.monitor.command;

import magma.common.spark.PlayMode;

public interface IServerCommander
{
	/**
	 * Method to beam the ball to the specified position and velocity.
	 *
	 * @param x position x on field
	 * @param y position y on field
	 * @param z position z on field
	 * @param vx velocity in x-direction
	 * @param vy velocity in y-direction
	 * @param vz velocity in z-direction
	 */
	void beamBall(float x, float y, float z, float vx, float vy, float vz);

	/**
	 * Convenient-method to beam the ball on the ground to the specified 2D
	 * position.
	 *
	 * @param x - the x-position on the field
	 * @param y - the y-position on the field
	 */
	void beamBall(float x, float y);

	/**
	 * Drops the ball at its current position and move all players away by the
	 * free kick radius. If the ball is off the field, it is brought back within
	 * bounds.
	 */
	void dropBall();

	/**
	 * Give kickoff to a random team
	 */
	void kickOff();

	/**
	 * Give kickoff to the specified team.
	 *
	 * @param k - the team which gets the kickoff
	 */
	void kickOff(Team k);

	/**
	 * Move a player to a position on the field. The team and playerNumber must
	 * be specified. The RANDOM team results in no player movement.
	 *
	 * @param team - the team to which the player belongs (only Team.LEFT and
	 *        Team.RIGHT are allowed)
	 * @param playerNumber - the player number
	 * @param x - the x-position
	 * @param y - the y-position
	 * @param z - the z-position
	 */
	void movePlayer(Team team, int playerNumber, float x, float y, float z);

	/**
	 * Move a player to a position on the field. The team and playerNumber must
	 * be specified. The RANDOM team results in no player movement.
	 *
	 * @param team - the team to which the player belongs (only Team.LEFT and
	 *        Team.RIGHT are allowed)
	 * @param playerNumber - the player number
	 * @param x - the x-position
	 * @param y - the y-position
	 * @param z - the z-position
	 * @param rot - the rotation around the z-axis
	 */
	void moveRotatePlayer(Team team, int playerNumber, float x, float y, float z, float rot);

	/**
	 * Sets the current playmode of the simulation.
	 *
	 * @param playmode - the intended playmode
	 */
	void setPlaymode(PlayMode playmode);

	/**
	 * Kills the simulation server. This will most likely result in a connection
	 * loss.
	 */
	void killServer();

	/**
	 * Requests a full state update from the server.
	 */
	void requestFullState();

	/**
	 * @param msg allows to send any message to the server
	 */
	void sendMessage(String msg);

	/**
	 * Sets the game time of the server
	 * @param time the game time in seconds.
	 */
	void setTime(float time);

	/**
	 * Sets the current score of the game.
	 * @param left score of left team
	 * @param right score of right team
	 */
	void setScore(int left, int right);
}
