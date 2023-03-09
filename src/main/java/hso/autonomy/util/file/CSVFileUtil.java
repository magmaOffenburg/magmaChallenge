/* Copyright 2008 - 2017 Hochschule Offenburg
 * For a list of authors see README.md
 * This software of HSOAutonomy is released under MIT License (see LICENSE).
 */

package hso.autonomy.util.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kdorer
 */
public class CSVFileUtil
{
	private File filePath;

	private String separator;

	public CSVFileUtil(File file, String separator)
	{
		this.filePath = file;
		this.separator = separator;
	}

	/**
	 * Reads the CSV file and populates the contents in an ArrayList of Strings.
	 * @throws IOException If the file could not be successfully opened and
	 *         parsed
	 */
	public List<String[]> readCsvFile() throws IOException
	{
		try (BufferedReader br = new BufferedReader(new FileReader(filePath));) {
			String line;
			List<String[]> csvContents = new ArrayList<>();

			while ((line = br.readLine()) != null) {
				String[] csvLineContents = line.split(separator);
				csvContents.add(csvLineContents);
			}

			return csvContents;
		}
	}
}
