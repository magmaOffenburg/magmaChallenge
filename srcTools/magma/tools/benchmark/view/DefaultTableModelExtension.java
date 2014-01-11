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

package magma.tools.benchmark.view;

import java.util.Collections;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import magma.tools.benchmark.model.TeamConfiguration;

/**
 * 
 * @author kdorer
 */
class DefaultTableModelExtension extends DefaultTableModel
{
	private static final long serialVersionUID = 1L;

	private Class[] columnTypes;

	private boolean[] columnEditables;

	public static DefaultTableModelExtension getInstance(
			List<TeamConfiguration> config)
	{
		final int COLUMNS = 8;

		if (config == null) {
			TeamConfiguration singleTeam = new TeamConfiguration("magma",
					"/host/Data/Projekte/RoboCup/Konfigurationen/runChallenge/",
					"startPlayerRunning.sh");
			config = Collections.singletonList(singleTeam);
		}

		Object[][] content = new Object[config.size()][COLUMNS];
		int i = 0;
		for (TeamConfiguration team : config) {
			content[i][BenchmarkView.COLUMN_TEAMNAME] = team.getName();
			content[i][BenchmarkView.COLUMN_STATUS] = null;
			content[i][BenchmarkView.COLUMN_SCORE] = null;
			content[i][BenchmarkView.COLUMN_FALLS] = null;
			content[i][BenchmarkView.COLUMN_SPEED] = null;
			content[i][BenchmarkView.COLUMN_OFF_GROUND] = null;
			content[i][BenchmarkView.COLUMN_PATH] = team.getPath();
			content[i][BenchmarkView.COLUMN_BINARY] = team.getLaunch();
			i++;
		}

		String[] headers = new String[] { "team", "status", "score", "falls",
				"speed", "off ground", "path", "start script" };

		return new DefaultTableModelExtension(content, headers);
	}

	/**
	 * @param data
	 * @param columnNames
	 */
	private DefaultTableModelExtension(Object[][] data, Object[] columnNames)
	{
		super(data, columnNames);

		columnTypes = new Class[] { String.class, Object.class, Float.class,
				Integer.class, Float.class, Float.class, String.class, String.class };

		columnEditables = new boolean[] { true, false, false, false, false,
				false, true, true };
	}

	@Override
	public Class getColumnClass(int columnIndex)
	{
		return columnTypes[columnIndex];
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return columnEditables[column];
	}
}
