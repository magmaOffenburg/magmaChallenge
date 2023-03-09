package hso.autonomy.util.misc;

import java.io.File;

public class SystemUtil
{
	public static final File NULL_FILE = new File(isWindows() ? "NUL" : "/dev/null");

	public static boolean isWindows()
	{
		return System.getProperty("os.name").toLowerCase().contains("win");
	}
}
