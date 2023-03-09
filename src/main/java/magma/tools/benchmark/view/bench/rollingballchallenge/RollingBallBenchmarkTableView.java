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

package magma.tools.benchmark.view.bench.rollingballchallenge;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import magma.tools.benchmark.model.IModelReadOnly;
import magma.tools.benchmark.model.ITeamResult;
import magma.tools.benchmark.model.TeamConfiguration;
import magma.tools.benchmark.model.bench.rollingballchallenge.RollingBallBenchmarkSingleResult;
import magma.tools.benchmark.model.bench.rollingballchallenge.RollingBallBenchmarkTeamResult;
import magma.tools.benchmark.view.bench.BenchmarkTableView;

/**
 *
 * @author kdorer
 */
public class RollingBallBenchmarkTableView extends BenchmarkTableView
{
	public static RollingBallBenchmarkTableView getInstance(IModelReadOnly model, String startScriptFolder)
	{
		RollingBallBenchmarkTableView view = new RollingBallBenchmarkTableView(model, startScriptFolder);
		model.attach(view);
		return view;
	}

	private RollingBallBenchmarkTableView(IModelReadOnly model, String startScriptFolder)
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
			String teamPath = (String) table.getValueAt(teamid, RollingBallBenchmarkTableModelExtension.COLUMN_PATH);
			teamName = (String) table.getValueAt(teamid, RollingBallBenchmarkTableModelExtension.COLUMN_TEAMNAME);
			float dropHeight = (Float) table.getValueAt(teamid, RollingBallBenchmarkTableModelExtension.COLUMN_DROP_HEIGHT);
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
			RollingBallBenchmarkTeamResult kickResult = (RollingBallBenchmarkTeamResult) teamResult;

			int teamRow = getTeamRow(kickResult.getName());

			double averageScore = kickResult.getScore();
			table.setValueAt(averageScore, teamRow, RollingBallBenchmarkTableModelExtension.COLUMN_SCORE);

			int runs = kickResult.size();
			table.setValueAt(runs, teamRow, RollingBallBenchmarkTableModelExtension.COLUMN_RUNS);

			double averageDistance = kickResult.getAverage(RollingBallBenchmarkSingleResult::getDistance);
			table.setValueAt(averageDistance, teamRow, RollingBallBenchmarkTableModelExtension.COLUMN_DISTANCE);

			double deltaY = kickResult.getAverage(RollingBallBenchmarkSingleResult::getDeltaY);
			table.setValueAt(deltaY, teamRow, RollingBallBenchmarkTableModelExtension.COLUMN_DELTAY);

			double falls = kickResult.getFallenCount();
			table.setValueAt(falls, teamRow, RollingBallBenchmarkTableModelExtension.COLUMN_FALLS);

			table.setValueAt("", teamRow, RollingBallBenchmarkTableModelExtension.COLUMN_STATUS);
		}

		if (!model.isRunning()) {
			enableEditing();
		}
	}

	private int getTeamRow(String name)
	{
		for (int i = 0; i < table.getRowCount(); i++) {
			if (name.equals(table.getValueAt(i, RollingBallBenchmarkTableModelExtension.COLUMN_TEAMNAME))) {
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
		DefaultTableModel tableModel = RollingBallBenchmarkTableModelExtension.getInstance(config);
		table.setModel(tableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(102);
		table.getColumnModel().getColumn(2).setPreferredWidth(56);
		table.getColumnModel().getColumn(3).setPreferredWidth(44);
		table.getColumnModel().getColumn(4).setPreferredWidth(60);
		table.getColumnModel().getColumn(5).setPreferredWidth(60);
		table.getColumnModel().getColumn(6).setPreferredWidth(60);
		table.getColumnModel().getColumn(7).setPreferredWidth(302);
		table.getColumnModel().getColumn(8).setPreferredWidth(156);
		table.setColumnSelectionAllowed(true);
		table.setAutoCreateRowSorter(true);

		BenchmarkTableCell benchmarkTableCell = new BenchmarkTableCell();
		table.getColumn("status").setCellRenderer(benchmarkTableCell);
		table.getColumn("status").setCellEditor(benchmarkTableCell);
		table.setRowHeight(30);
		table.addMouseListener(new TableMouseListener(
				RollingBallBenchmarkTableModelExtension.COLUMN_STATUS, RollingBallBenchmarkTableModelExtension.COLUMN_TEAMNAME));
		return table;
	}

	@Override
	public ResultStatus getStatus(int row)
	{
		return getLocalStatus(row, RollingBallBenchmarkTableModelExtension.COLUMN_TEAMNAME);
	}
}
