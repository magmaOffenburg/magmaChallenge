package magma.monitor.messageparser.impl;

/**
 * Thrown if a piece of a server message could not be converted
 *
 * @author Stefan Glaser
 */
public class NodeConversionException extends Exception
{
	/**
	 * Constructor
	 *
	 * @param msg Error message
	 */
	public NodeConversionException(String msg)
	{
		super(msg);
	}
}