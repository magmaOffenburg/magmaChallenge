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

package magma.tools.benchmark.view.bench.throwinchallenge;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import magma.tools.benchmark.model.IModelReadOnly;
import magma.tools.benchmark.model.ITeamResult;
import magma.tools.benchmark.model.TeamConfiguration;
import magma.tools.benchmark.model.bench.throwinchallenge.ThrowInBenchmarkTeamResult;
import magma.tools.benchmark.view.bench.BenchmarkTableView;

/**
 *
 * @author kdorer
 */
public class ThrowInBenchmarkTableView extends BenchmarkTableView
{
	public static ThrowInBenchmarkTableView getInstance(IModelReadOnly model, String startScriptFolder)
	{
		ThrowInBenchmarkTableView view = new ThrowInBenchmarkTableView(model, startScriptFolder);
		model.attach(view);
		return view;
	}

	private ThrowInBenchmarkTableView(IModelReadOnly model, String startScriptFolder)
	{
		super(model, startScriptFolder);
	}

	@Override
	public List<TeamConfiguration> getTeamConfiguration()
	{
		List<TeamConfiguration> result = new ArrayList<>();
		int teamid = 0;
		String teamName;
		do {
			String teamPath = (String) table.getValueAt(teamid, ThrowInBenchmarkTableModelExtension.COLUMN_PATH);
			teamName = (String) table.getValueAt(teamid, ThrowInBenchmarkTableModelExtension.COLUMN_TEAMNAME);
			float dropHeight = (Float) table.getValueAt(teamid, ThrowInBenchmarkTableModelExtension.COLUMN_DROP_HEIGHT);
			if (teamName != null && !teamName.isEmpty()) {
				TeamConfiguration config = new TeamConfiguration(teamName, teamPath, dropHeight);
				result.add(config);
				teamid++;
			}
		} while (teamName != null && !teamName.isEmpty() && teamid < table.getRowCount());

		return result;
	}

	@Override
	public void update(IModelReadOnly model)
	{
		List<ITeamResult> teamResults = model.getTeamResults();

		for (ITeamResult teamResult : teamResults) {
			ThrowInBenchmarkTeamResult throwInResult = (ThrowInBenchmarkTeamResult) teamResult;

			int teamRow = getTeamRow(throwInResult.getName());

			double averageScore = throwInResult.getScore();
			table.setValueAt(averageScore, teamRow, ThrowInBenchmarkTableModelExtension.COLUMN_SCORE);

			int runs = throwInResult.size();
			table.setValueAt(runs, teamRow, ThrowInBenchmarkTableModelExtension.COLUMN_RUNS);

			int fallenCount = throwInResult.getPenaltyCount();
			table.setValueAt(fallenCount, teamRow, ThrowInBenchmarkTableModelExtension.COLUMN_PENALTIES);

			double lastDistance = throwInResult.getLastDistance();
			table.setValueAt(lastDistance, teamRow, ThrowInBenchmarkTableModelExtension.COLUMN_DISTANCE);

			table.setValueAt("", teamRow, ThrowInBenchmarkTableModelExtension.COLUMN_STATUS);
		}

		if (!model.isRunning()) {
			enableEditing();
		}
	}

	private int getTeamRow(String name)
	{
		for (int i = 0; i < table.getRowCount(); i++) {
			if (name.equals(table.getValueAt(i, ThrowInBenchmarkTableModelExtension.COLUMN_TEAMNAME))) {
				return i;
			}
		}
		return -1;
	}

	@Override
	protected JTable createTeamTable(List<TeamConfiguration> config)
	{
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setCellSelectionEnabled(true);
		DefaultTableModel tableModel = ThrowInBenchmarkTableModelExtension.getInstance(config);
		table.setModel(tableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(102);
		table.getColumnModel().getColumn(2).setPreferredWidth(56);
		table.getColumnModel().getColumn(3).setPreferredWidth(44);
		table.getColumnModel().getColumn(4).setPreferredWidth(38);
		table.getColumnModel().getColumn(5).setPreferredWidth(85);
		table.getColumnModel().getColumn(6).setPreferredWidth(302);
		table.getColumnModel().getColumn(7).setPreferredWidth(156);
		table.setColumnSelectionAllowed(true);
		table.setAutoCreateRowSorter(true);

		BenchmarkTableCell benchmarkTableCell = new BenchmarkTableCell();
		table.getColumn("status").setCellRenderer(benchmarkTableCell);
		table.getColumn("status").setCellEditor(benchmarkTableCell);
		table.setRowHeight(30);
		table.addMouseListener(new TableMouseListener(
				ThrowInBenchmarkTableModelExtension.COLUMN_STATUS, ThrowInBenchmarkTableModelExtension.COLUMN_TEAMNAME));
		return table;
	}

	@Override
	public ResultStatus getStatus(int row)
	{
		return getLocalStatus(row, ThrowInBenchmarkTableModelExtension.COLUMN_TEAMNAME);
	}
}
