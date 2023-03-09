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
package hso.autonomy.util.connection.impl;

import hso.autonomy.util.connection.ConnectionException;
import java.io.IOException;

/**
 * Server connection implementation
 *
 * @author Klaus Dorer
 */
public class ServerConnection extends TCPConnection
{
	private final boolean littleEndian;

	public ServerConnection(String host, int port)
	{
		this(host, port, true);
	}

	public ServerConnection(String host, int port, boolean littleEndian)
	{
		super(host, port);
		this.littleEndian = littleEndian;
	}

	/**
	 * Send a message to the server
	 *
	 * @param msg Message in ASCII form
	 */
	@Override
	public void sendMessage(byte[] msg) throws ConnectionException
	{
		// creation of the messages header (4 bytes)
		int len = msg.length;
		int byte0 = (len >> 24) & 0xFF;
		int byte1 = (len >> 16) & 0xFF;
		int byte2 = (len >> 8) & 0xFF;
		int byte3 = len & 0xFF;

		try {
			if (littleEndian) {
				out.write((byte) byte0);
				out.write((byte) byte1);
				out.write((byte) byte2);
				out.write((byte) byte3);
			} else {
				out.write((byte) byte3);
				out.write((byte) byte2);
				out.write((byte) byte1);
				out.write((byte) byte0);
			}
			out.write(msg);
			out.flush();
		} catch (IOException e) {
			shutDown = true;
			throw new ConnectionException("Error writing to socket, shutting down...", e);
		}
	}

	@Override
	protected byte[] receiveMessage() throws ConnectionException
	{
		byte[] result;
		int length;

		try {
			int byte0 = in.read();
			int byte1 = in.read();
			int byte2 = in.read();
			int byte3 = in.read();
			if (littleEndian) {
				length = byte0 << 24 | byte1 << 16 | byte2 << 8 | byte3;
			} else {
				length = byte3 << 24 | byte2 << 16 | byte1 << 8 | byte0;
			}
			// the header
			int total = 0;

			if (length < 0) {
				// server was shutdown
				shutDown = true;
				throw new ConnectionException("Server shut down");
			}

			result = new byte[length];
			while (total < length) {
				total += in.read(result, total, length - total);
			}
		} catch (IOException e) {
			shutDown = true;
			throw new ConnectionException("Error when reading from socket, closing down...", e);
		}
		return result;
	}
}
