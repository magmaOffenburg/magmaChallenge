/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */
package hso.autonomy.util.connection;

/**
 *
 * @author kdorer
 */
public class ConnectionException extends Exception
{
	public ConnectionException()
	{
	}

	public ConnectionException(String message)
	{
		super(message);
	}

	public ConnectionException(Throwable cause)
	{
		super(cause);
	}

	public ConnectionException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
