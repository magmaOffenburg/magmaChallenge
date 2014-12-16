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

	private Class<? extends Object>[] columnTypes;

	private boolean[] columnEditables;

	public static DefaultTableModelExtension getInstance(
			List<TeamConfiguration> config)
	{
		final int COLUMNS = 12;

		if (config == null) {
			TeamConfiguration singleTeam = new TeamConfiguration("magma",
					"/host/Data/Projekte/RoboCup/Konfigurationen/runChallenge/",
					"startPlayerRunning.sh", 0.4f);
			config = Collections.singletonList(singleTeam);
		}

		Object[][] content = new Object[config.size()][COLUMNS];
		int i = 0;
		for (TeamConfiguration team : config) {
			content[i][BenchmarkView.COLUMN_TEAMNAME] = team.getName();
			content[i][BenchmarkView.COLUMN_STATUS] = null;
			content[i][BenchmarkView.COLUMN_SCORE] = null;
			content[i][BenchmarkView.COLUMN_RUNS] = null;
			content[i][BenchmarkView.COLUMN_FALLS] = null;
			content[i][BenchmarkView.COLUMN_SPEED] = null;
			content[i][BenchmarkView.COLUMN_OFF_GROUND] = null;
			content[i][BenchmarkView.COLUMN_ONE_LEG] = null;
			content[i][BenchmarkView.COLUMN_TWO_LEGS] = null;
			content[i][BenchmarkView.COLUMN_PATH] = team.getPath();
			content[i][BenchmarkView.COLUMN_BINARY] = team.getLaunch();
			content[i][BenchmarkView.COLUMN_DROP_HEIGHT] = team.getDropHeight();
			i++;
		}

		String[] headers = new String[] { "team", "status", "score", "runs",
				"falls", "speed", "off ground", "one leg", "two legs", "path",
				"start script", "drop height" };

		return new DefaultTableModelExtension(content, headers);
	}

	/**
	 * @param data
	 * @param columnNames
	 */
	@SuppressWarnings("unchecked")
	private DefaultTableModelExtension(Object[][] data, Object[] columnNames)
	{
		super(data, columnNames);

		columnTypes = new Class[] { String.class, Object.class, Float.class,
				Integer.class, Integer.class, Float.class, Float.class,
				Float.class, Float.class, String.class, String.class, Float.class };

		columnEditables = new boolean[] { true, false, false, false, false,
				false, false, false, false, true, true, true };
	}

	@Override
	public Class<? extends Object> getColumnClass(int columnIndex)
	{
		return columnTypes[columnIndex];
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return columnEditables[column];
	}
}
