/*******************************************************************************
 * Copyright 2008, 2012 Hochschule Offenburg
 * Klaus Dorer, Mathias Ehret, Stefan Glaser, Thomas Huber, Fabian Korak,
 * Simon Raffeiner, Srinivasa Ragavan, Thomas Rinklin,
 * Joachim Schilling, Ingo Schindler, Rajit Shahi, Bjoern Weiler
 *
 * This file is part of magmaOffenburg.
 *
 * magmaOffenburg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * magmaOffenburg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with magmaOffenburg. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package magma.common.spark;

/**
 * Play modes enumeration. Every enum stores the corresponding message used in
 * the server protocol. The order and index position of these constants is
 * important for the monitor protocol only passes ints!!
 *
 * @author Stefan Glaser
 */
public enum PlayMode
{
	/** The game has not started yet, set in humanoid games */
	BEFORE_KICK_OFF("BeforeKickOff"),

	/**
	 * The ball is in the center, and the team on the left side is allowed to
	 * kick it first
	 */
	KICK_OFF_LEFT("KickOff_Left"),

	/**
	 * The ball is in the center, and the team on the right side is allowed to
	 * kick it first
	 */
	KICK_OFF_RIGHT("KickOff_Right"),

	/**
	 * The game is processing normally, no special rules are in place
	 */
	PLAY_ON("PlayOn"),

	/**
	 * The ball left the playfield, the team on the left is allowed to kick it
	 * back into the field
	 */
	KICK_IN_LEFT("KickIn_Left"),

	/**
	 * The ball left the playfield, the team on the right is allowed to kick it
	 * back into the field
	 */
	KICK_IN_RIGHT("KickIn_Right"),

	/**
	 * The ball left the playfield, the team on the left is allowed to kick it
	 * back into the field from a corner
	 */
	CORNER_KICK_LEFT("corner_kick_left"),

	/**
	 * The ball left the playfield, the team on the right is allowed to kick it
	 * back into the field from a corner
	 */
	CORNER_KICK_RIGHT("corner_kick_right"),

	/**
	 * The ball left the playfield while it was near the left goal, and the left
	 * team is allowed to kick it first
	 */
	GOAL_KICK_LEFT("goal_kick_left"),

	/**
	 * The ball left the playfield while it was near the right goal, and the
	 * right team is allowed to kick it first
	 */
	GOAL_KICK_RIGHT("goal_kick_right"),

	/**
	 * The team on the right violated the offside rule and the team on the left
	 * is allowed to kick the ball first
	 */
	OFFSIDE_LEFT("offside_left"),

	/**
	 * The team on the left violated the offside rule and the team on the right
	 * is allowed to kick the ball first
	 */
	OFFSIDE_RIGHT("offside_right"),

	/**
	 * The game is over
	 */
	GAME_OVER("GameOver"),

	/**
	 * A goal was counted for the team on the left
	 */
	GOAL_LEFT("Goal_Left"),

	/**
	 * A goal was counted for the team on the right
	 */
	GOAL_RIGHT("Goal_Right"),

	/**
	 * The left team got a free kick (probably because the referee decided to do
	 * so) and may kick the ball first
	 */
	FREE_KICK_LEFT("free_kick_left"),

	/**
	 * The right team got a free kick (probably because the referee decided to do
	 * so) and may kick the ball first
	 */
	FREE_KICK_RIGHT("free_kick_right"),

	DIRECT_FREE_KICK_LEFT("direct_free_kick_left"),

	DIRECT_FREE_KICK_RIGHT("direct_free_kick_right"),

	/**
	 * No playmode or an unknown one (should not be used!)
	 */
	NONE("NONE"),

	/** Humanoid State before a free kick or during punishment */
	FREEZE("Freeze"),

	PENALTY_KICK_LEFT("penalty_kick_left"),

	PENALTY_KICK_RIGHT("penalty_kick_right"),

	/**
	 * In the Humanoid league, there is a difference between penalty kick (in the game) and penalty shootouts (at the
	 * end of a game). Thus we need to reflect this with different behaviors.
	 */
	PENALTY_SHOOT_LEFT("penalty_shoot_left"),

	/**
	 * In the Humanoid league, there is a difference between penalty kick (in the game) and penalty shootouts (at the
	 * end of a game). Thus we need to reflect this with different behaviors.
	 */
	PENALTY_SHOOT_RIGHT("penalty_shoot_right"),

	PASS_LEFT("pass_left"),

	PASS_RIGHT("pass_right");

	private final String name;

	PlayMode(String name)
	{
		this.name = name;
	}

	/**
	 * Convert a play mode into an enum
	 *
	 * @param playModeString Play mode string
	 * @return Resulting enum
	 */
	public static PlayMode parsePlayMode(String playModeString)
	{
		for (PlayMode mode : values()) {
			if (playModeString.equalsIgnoreCase(mode.name))
				return mode;
		}
		return NONE;
	}

	public boolean isBeamingAllowed()
	{
		return this == BEFORE_KICK_OFF || this == GOAL_LEFT || this == GOAL_RIGHT;
	}

	/**
	 * Convert a PlayMode to a string.
	 *
	 * @return The playmode string that will be accepted by the server.
	 */
	public String getServerString()
	{
		return name;
	}
}
