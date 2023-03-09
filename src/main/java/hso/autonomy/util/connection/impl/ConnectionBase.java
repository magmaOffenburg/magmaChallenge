/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */
package hso.autonomy.util.connection.impl;

import hso.autonomy.util.connection.IServerConnection;
import hso.autonomy.util.observer.IObserver;
import hso.autonomy.util.observer.IPublishSubscribe;
import hso.autonomy.util.observer.Subject;

/**
 * Base for connection implementations
 *
 * @author Klaus Dorer
 */
public abstract class ConnectionBase implements IServerConnection
{
	protected final IPublishSubscribe<byte[]> observer;

	// True if connection is closed (after disconnection/error)
	protected boolean shutDown;

	// true if we are currently connected to the server
	protected boolean connected;

	public ConnectionBase()
	{
		shutDown = false;
		connected = false;
		observer = new Subject<>();
	}

	/**
	 * Stops the loop of receiving messages and notifying observers after
	 * receiving the next message
	 */
	@Override
	public void stopReceiveLoop()
	{
		shutDown = true;
	}

	/**
	 * Attaches a new observer to this connection
	 *
	 * @param newObserver the observer that is interested in new messages
	 */
	@Override
	public void attach(IObserver<byte[]> newObserver)
	{
		observer.attach(newObserver);
	}

	/**
	 * @return true if we are currently connected to the server
	 */
	@Override
	public boolean isConnected()
	{
		return connected;
	}
}
