/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */
package hso.autonomy.util.connection;

import hso.autonomy.util.observer.ISubscribe;

/**
 * Server connection interface
 *
 * @author Klaus Dorer
 */
public interface IServerConnection extends ISubscribe<byte[]>
{
	/**
	 * Creates a connection to the server and initializes the input and output
	 * stream
	 */
	void establishConnection() throws ConnectionException;

	/**
	 * Sends the passed message to the server
	 * @param msg the message to send
	 */
	void sendMessage(byte[] msg) throws ConnectionException;

	/**
	 * Starts the receive loop that only ends once the socket is shutdown
	 * @throws ConnectionException if any connection problem occurred
	 */
	void startReceiveLoop() throws ConnectionException;

	/**
	 * Stops the loop of receiving messages and notifying observers after
	 * receiving the next message
	 */
	void stopReceiveLoop();

	/**
	 * @return true if we are currently connected to the server
	 */
	boolean isConnected();
}