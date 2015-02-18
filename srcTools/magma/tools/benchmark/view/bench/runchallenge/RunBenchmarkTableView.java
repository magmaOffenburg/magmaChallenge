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

package magma.tools.benchmark.view.bench.runchallenge;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import magma.tools.benchmark.model.IModelReadOnly;
import magma.tools.benchmark.model.ITeamResult;
import magma.tools.benchmark.model.TeamConfiguration;
import magma.tools.benchmark.model.bench.runchallenge.RunBenchmarkTeamResult;
import magma.tools.benchmark.view.bench.BenchmarkTableView;

/**
 * 
 * @author kdorer
 */
public class RunBenchmarkTableView extends BenchmarkTableView
{

	public static RunBenchmarkTableView getInstance(IModelReadOnly model,
			String defaultPath)
	{
		RunBenchmarkTableView view = new RunBenchmarkTableView(model, defaultPath);
		view.createTeamTable(null);
		model.attach(view);
		return view;
	}

	private RunBenchmarkTableView(IModelReadOnly model, String defaultPath)
	{
		super(model, defaultPath);
	}

	@Override
	public List<TeamConfiguration> getTeamConfiguration()
	{
		List<TeamConfiguration> result = new ArrayList<TeamConfiguration>();
		int teamid = 0;
		String teamName;
		float dropHeight = 0.4f;
		do {
			String teamPath = (String) table.getValueAt(teamid,
					RunBenchmarkTableModelExtension.COLUMN_PATH);
			String teamBinary = (String) table.getValueAt(teamid,
					RunBenchmarkTableModelExtension.COLUMN_BINARY);
			teamName = (String) table.getValueAt(teamid,
					RunBenchmarkTableModelExtension.COLUMN_TEAMNAME);
			dropHeight = (Float) table.getValueAt(teamid,
					RunBenchmarkTableModelExtension.COLUMN_DROP_HEIGHT);
			if (teamName != null && !teamName.isEmpty()) {
				TeamConfiguration config = new TeamConfiguration(teamName,
						teamPath, teamBinary, dropHeight);
				result.add(config);
				teamid++;
			}
		} while (teamName != null && !teamName.isEmpty()
				&& teamid < table.getRowCount());

		return result;
	}

	@Override
	public void update(IModelReadOnly model)
	{
		List<ITeamResult> teamResults = model.getTeamResults();

		for (int i = 0; i < teamResults.size(); i++) {
			RunBenchmarkTeamResult teamResult = (RunBenchmarkTeamResult) teamResults
					.get(i);

			int teamRow = getTeamRow(teamResult.getName());

			float averageScore = teamResult.getAverageScore();
			table.setValueAt(averageScore, teamRow,
					RunBenchmarkTableModelExtension.COLUMN_SCORE);

			int runs = ((ITeamResult) teamResult.getResult(0)).size();
			table.setValueAt(runs, teamRow,
					RunBenchmarkTableModelExtension.COLUMN_RUNS);

			int fallenCount = teamResult.getFallenCount();
			table.setValueAt(fallenCount, teamRow,
					RunBenchmarkTableModelExtension.COLUMN_FALLS);

			float averageSpeed = teamResult.getAverageSpeed();
			table.setValueAt(averageSpeed, teamRow,
					RunBenchmarkTableModelExtension.COLUMN_SPEED);

			float averageOffGround = teamResult.getAverageOffGround();
			table.setValueAt(averageOffGround, teamRow,
					RunBenchmarkTableModelExtension.COLUMN_OFF_GROUND);

			float averageOneLeg = teamResult.getAverageOneLeg();
			table.setValueAt(averageOneLeg, teamRow,
					RunBenchmarkTableModelExtension.COLUMN_ONE_LEG);

			float averageTwoLegs = teamResult.getAverageTwoLegs();
			table.setValueAt(averageTwoLegs, teamRow,
					RunBenchmarkTableModelExtension.COLUMN_TWO_LEGS);

			table.setValueAt("", teamRow,
					RunBenchmarkTableModelExtension.COLUMN_STATUS);
		}

		if (!model.isRunning()) {
			enableEditing();
		}
	}

	/**
	 * @param name
	 * @return
	 */
	private int getTeamRow(String name)
	{
		for (int i = 0; i < table.getRowCount(); i++) {
			if (name.equals(table.getValueAt(i,
					RunBenchmarkTableModelExtension.COLUMN_TEAMNAME))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 
	 */
	@Override
	protected JTable createTeamTable(List<TeamConfiguration> config)
	{
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setCellSelectionEnabled(true);
		DefaultTableModel tableModel = RunBenchmarkTableModelExtension
				.getInstance(config);
		table.setModel(tableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(102);
		table.getColumnModel().getColumn(2).setPreferredWidth(56);
		table.getColumnModel().getColumn(3).setPreferredWidth(44);
		table.getColumnModel().getColumn(4).setPreferredWidth(38);
		table.getColumnModel().getColumn(5).setPreferredWidth(85);
		table.getColumnModel().getColumn(7).setPreferredWidth(54);
		table.getColumnModel().getColumn(8).setPreferredWidth(54);
		table.getColumnModel().getColumn(9).setPreferredWidth(302);
		table.getColumnModel().getColumn(10).setPreferredWidth(156);
		table.setColumnSelectionAllowed(true);
		table.setAutoCreateRowSorter(true);

		BenchmarkTableCell benchmarkTableCell = new BenchmarkTableView.BenchmarkTableCell();
		table.getColumn("status").setCellRenderer(benchmarkTableCell);
		table.getColumn("status").setCellEditor(benchmarkTableCell);
		table.setRowHeight(30);
		table.addMouseListener(new TableMouseListener(
				RunBenchmarkTableModelExtension.COLUMN_STATUS,
				RunBenchmarkTableModelExtension.COLUMN_TEAMNAME));
		return table;
	}

	/**
	 * @param row
	 * @return
	 */
	@Override
	public ResultStatus getStatus(int row)
	{
		return getLocalStatus(row,
				RunBenchmarkTableModelExtension.COLUMN_TEAMNAME);
	}
}