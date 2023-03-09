/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */
package hso.autonomy.util.symboltreeparser;

/**
 * Exception thrown if illegal symbols are found in the input stream
 *
 * @author Simon Raffeiner
 */
public class IllegalSymbolInputException extends RuntimeException
{
	public IllegalSymbolInputException(String message)
	{
		super(message);
	}
}
