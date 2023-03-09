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
 * Play sides enumeration. Every enum stores the corresponding message used in
 * the server protocol.
 *
 * @author Stefan Glaser
 */
public enum PlaySide
{
	/** We play from left to right */
	LEFT("left"),

	/** We play from right to left */
	RIGHT("right"),

	/** No, or unknown play side */
	UNKNOWN("unknown");

	private final String playSideString;

	PlaySide(String playSideString)
	{
		this.playSideString = playSideString;
	}

	/**
	 * Convert a play side into an enum
	 *
	 * @param playSideString Play side string
	 * @return Resulting enum
	 */
	public static PlaySide parsePlaySide(String playSideString)
	{
		for (PlaySide side : values()) {
			if (playSideString.equalsIgnoreCase(side.playSideString))
				return side;
		}

		return UNKNOWN;
	}

	public String getName()
	{
		return playSideString;
	}

	public PlaySide getOpposite()
	{
		switch (this) {
		case LEFT:
			return RIGHT;
		case RIGHT:
			return LEFT;
		default:
			return UNKNOWN;
		}
	}
}
