/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */

package hso.autonomy.util.misc;

import java.io.IOException;

/**
 * A collection of command line utilities for direct unix commands.
 * @author kdorer
 */
public class UnixCommandUtil
{
	/**
	 * Checks if the process with pid passed contains the passed name when run
	 * with ps. If so it is killed.
	 * @param pid the pid of the process to kill.
	 * @param name the name that has to appear when calling ps
	 * @return true if the process was killed.
	 */
	public static boolean killProcessConditional(String pid, String name)
	{
		String command = "ps " + pid;
		Process ps;
		try {
			ps = Runtime.getRuntime().exec(command);
			if (CommandUtil.checkForOutput(ps, name)) {
				Runtime.getRuntime().exec("kill -9 " + pid);
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Kills all processes with a given name. Use with care!
	 * @param name the name that has to appear when calling ps
	 */
	public static boolean killAll(String name)
	{
		try {
			Runtime.getRuntime().exec("killall -9 " + name);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
