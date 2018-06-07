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

package magma.tools.benchmark.view.bench.goaliechallenge;

import java.util.List;
import javax.swing.table.DefaultTableModel;
import magma.tools.benchmark.model.TeamConfiguration;

/**
 *
 * @author kdorer
 */
class GoalieBenchmarkTableModelExtension extends DefaultTableModel
{
	static final int COLUMN_TEAMNAME = 0;

	static final int COLUMN_STATUS = 1;

	static final int COLUMN_SCORE = 2;

	static final int COLUMN_RUNS = 3;

	static final int COLUMN_PATH = 4;

	private final Class<?>[] columnTypes;

	private final boolean[] editableColumns;

	public static GoalieBenchmarkTableModelExtension getInstance(List<TeamConfiguration> config)
	{
		final int COLUMNS = 5;

		Object[][] content = new Object[config.size()][COLUMNS];
		int i = 0;
		for (TeamConfiguration team : config) {
			content[i][COLUMN_TEAMNAME] = team.getName();
			content[i][COLUMN_STATUS] = null;
			content[i][COLUMN_SCORE] = null;
			content[i][COLUMN_RUNS] = null;
			content[i][COLUMN_PATH] = team.getPath();
			i++;
		}

		String[] headers =
				new String[] {"team", "status", "score", "runs", "path"};

		return new GoalieBenchmarkTableModelExtension(content, headers);
	}

	private GoalieBenchmarkTableModelExtension(Object[][] data, Object[] columnNames)
	{
		super(data, columnNames);

		columnTypes = new Class[] {String.class, Object.class, Float.class, Integer.class, String.class};

		editableColumns = new boolean[] {true, false, false, false, true};
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		return columnTypes[columnIndex];
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return editableColumns[column];
	}
}
