/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */

package hso.autonomy.util.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author kdorer
 */
public class StreamConsumer
{
	private InputStream in;

	private boolean print;

	private int id;

	public StreamConsumer(InputStream in, boolean print, int id)
	{
		this.in = in;
		this.print = print;
		this.id = id;
		ConsumerThread t = new ConsumerThread();
		t.start();
	}

	class ConsumerThread extends Thread
	{
		@Override
		public void run()
		{
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
				String line = reader.readLine();
				while (line != null) {
					if (print) {
						System.out.println(id + "-" + line);
					}
					line = reader.readLine();
				}
			} catch (IOException e) {
			}
		}
	}
}
