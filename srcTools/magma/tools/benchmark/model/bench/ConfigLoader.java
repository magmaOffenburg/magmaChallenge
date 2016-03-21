/* Copyright 2009 Hochschule Offenburg
 * Klaus Dorer, Mathias Ehret, Stefan Glaser, Thomas Huber,
 * Simon Raffeiner, Srinivasa Ragavan, Thomas Rinklin,
 * Joachim Schilling, Rajit Shahi
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
 */

package magma.tools.benchmark.model.bench;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import magma.tools.benchmark.model.InvalidConfigFileException;
import magma.tools.benchmark.model.TeamConfiguration;
import magma.util.file.CSVFileUtil;

/**
 * 
 * @author kdorer
 */
public class ConfigLoader
{
	public List<TeamConfiguration> loadConfigFile(File file)
			throws InvalidConfigFileException
	{
		CSVFileUtil csvFileHandler = new CSVFileUtil(file, ";");
		try {
			List<String[]> readCsvFile = csvFileHandler.readCsvFile();

			List<TeamConfiguration> result = new ArrayList<>(readCsvFile.size());
			for (String[] line : readCsvFile) {
				if (line.length != 4) {
					String error = "Invalid config file format, expected 4 columns, but was: "
							+ line.length + "\nline: " + Arrays.toString(line);
					throw new InvalidConfigFileException(error);
				}
				float dropHeight = Float.valueOf(line[3]);
				TeamConfiguration config = new TeamConfiguration(line[0], line[1],
						line[2], dropHeight);
				result.add(config);
			}

			return result;

		} catch (IOException e) {
			throw new InvalidConfigFileException(e.getMessage(), e);
		}
	}
}
