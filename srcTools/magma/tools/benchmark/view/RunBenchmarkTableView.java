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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import magma.tools.benchmark.model.IModelReadOnly;
import magma.tools.benchmark.model.TeamConfiguration;
import magma.tools.benchmark.model.TeamResult;
import magma.tools.benchmark.model.bench.runchallenge.RunBenchmarkTeamResult;
import magma.util.observer.IObserver;

/**
 * 
 * @author kdorer
 */
public class RunBenchmarkTableView implements IObserver<IModelReadOnly>
{
	static final int COLUMN_TEAMNAME = 0;

	static final int COLUMN_STATUS = 1;

	static final int COLUMN_SCORE = 2;

	static final int COLUMN_RUNS = 3;

	static final int COLUMN_FALLS = 4;

	static final int COLUMN_SPEED = 5;

	static final int COLUMN_OFF_GROUND = 6;

	static final int COLUMN_ONE_LEG = 7;

	static final int COLUMN_TWO_LEGS = 8;

	static final int COLUMN_PATH = 9;

	static final int COLUMN_BINARY = 10;

	static final int COLUMN_DROP_HEIGHT = 11;

	private JTable table;

	private IModelReadOnly model;

	public static RunBenchmarkTableView getInstance(IModelReadOnly model,
			String defaultPath)
	{
		RunBenchmarkTableView view = new RunBenchmarkTableView(model, defaultPath);
		model.attach(view);
		return view;
	}

	private RunBenchmarkTableView(IModelReadOnly model, String defaultPath)
	{
		this.model = model;
		createTeamTable(null);
	}

	public List<TeamConfiguration> getTeamConfiguration()
	{
		List<TeamConfiguration> result = new ArrayList<TeamConfiguration>();
		int teamid = 0;
		String teamName;
		float dropHeight = 0.4f;
		do {
			String teamPath = (String) table.getValueAt(teamid, COLUMN_PATH);
			String teamBinary = (String) table.getValueAt(teamid, COLUMN_BINARY);
			teamName = (String) table.getValueAt(teamid, COLUMN_TEAMNAME);
			dropHeight = (Float) table.getValueAt(teamid, COLUMN_DROP_HEIGHT);
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

	public void disableEditing()
	{
		table.setEnabled(false);
	}

	public void enableEditing()
	{
		table.setEnabled(true);
	}

	@Override
	public void update(IModelReadOnly model)
	{
		List<TeamResult> teamResults = model.getTeamResults();

		for (int i = 0; i < teamResults.size(); i++) {
			RunBenchmarkTeamResult teamResult = (RunBenchmarkTeamResult) teamResults
					.get(i);

			int teamRow = getTeamRow(teamResult.getName());

			float averageScore = teamResult.getAverageScore();
			table.setValueAt(averageScore, teamRow, COLUMN_SCORE);

			int runs = teamResult.size();
			table.setValueAt(runs, teamRow, COLUMN_RUNS);

			int fallenCount = teamResult.getFallenCount();
			table.setValueAt(fallenCount, teamRow, COLUMN_FALLS);

			float averageSpeed = teamResult.getAverageSpeed();
			table.setValueAt(averageSpeed, teamRow, COLUMN_SPEED);

			float averageOffGround = teamResult.getAverageOffGround();
			table.setValueAt(averageOffGround, teamRow, COLUMN_OFF_GROUND);

			float averageOneLeg = teamResult.getAverageOneLeg();
			table.setValueAt(averageOneLeg, teamRow, COLUMN_ONE_LEG);

			float averageTwoLegs = teamResult.getAverageTwoLegs();
			table.setValueAt(averageTwoLegs, teamRow, COLUMN_TWO_LEGS);

			table.setValueAt("", teamRow, COLUMN_STATUS);
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
			if (name.equals(table.getValueAt(i, COLUMN_TEAMNAME))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @param name
	 * @return
	 */
	private TeamResult getTeamEntry(String name, List<TeamResult> teamResults)
	{
		for (TeamResult result : teamResults) {
			if (name.equals(result.getName())) {
				return result;
			}
		}
		return null;
	}

	/**
	 * @param message
	 */
	public void showErrorMessage(String message)
	{
		JTextArea jta = new JTextArea(message);
		@SuppressWarnings("serial")
		JScrollPane jsp = new JScrollPane(jta) {
			@Override
			public Dimension getPreferredSize()
			{
				return new Dimension(600, 320);
			}
		};
		JOptionPane.showMessageDialog(null, jsp, "Problem",
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * @param loadConfigFile
	 */
	public JTable updateConfigTable(List<TeamConfiguration> loadConfigFile)
	{
		return createTeamTable(loadConfigFile);
	}

	/**
	 * 
	 */
	private JTable createTeamTable(List<TeamConfiguration> config)
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

		BenchmarkTableCell benchmarkTableCell = new BenchmarkTableCell();
		table.getColumn("status").setCellRenderer(benchmarkTableCell);
		table.getColumn("status").setCellEditor(benchmarkTableCell);
		table.setRowHeight(30);
		table.addMouseListener(new TableMouseListener());
		return table;
	}

	class TableMouseListener extends MouseAdapter
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			JTable target = (JTable) e.getSource();
			int row = target.getSelectedRow();
			int column = target.getSelectedColumn();
			if (column == COLUMN_STATUS) {
				List<TeamResult> teamResults = model.getTeamResults();
				String team = (String) target.getValueAt(row, COLUMN_TEAMNAME);
				TeamResult result = getTeamEntry(team, teamResults);
				if (result != null) {
					String text = result.getStatusText();
					if (result.isValid()) {
						JOptionPane.showMessageDialog(null, text, team,
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						showErrorMessage(text);
					}
				}
			}
		}
	}

	enum ResultStatus {
		NO_RESULT, SUCCESS, FAILED
	};

	class BenchmarkTableCell extends AbstractCellEditor implements
			TableCellEditor, TableCellRenderer
	{
		/**  */
		private static final long serialVersionUID = 1L;

		JPanel panel;

		JButton statusButton;

		public BenchmarkTableCell()
		{
			statusButton = new JButton();
			statusButton.setIcon(new ImageIcon(RunBenchmarkTableView.class
					.getResource("/images/runChallenge/info_16.png")));
			statusButton.setSize(30, 30);
			statusButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					System.out.println("pressed");
				}
			});

			panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel.add(statusButton);
		}

		private void updateData(String text, boolean isSelected, JTable table,
				int row)
		{
			statusButton.setText(text);
			if (isSelected) {
				panel.setBackground(table.getSelectionBackground());
			} else {
				ResultStatus status = getStatus(row);
				if (status == ResultStatus.SUCCESS) {
					panel.setBackground(Color.green);
				} else if (status == ResultStatus.FAILED) {
					panel.setBackground(Color.RED);
				} else {
					panel.setBackground(table.getBackground());
				}
			}
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int column)
		{
			String text = (String) value;
			updateData(text, true, table, row);
			return panel;
		}

		@Override
		public Object getCellEditorValue()
		{
			return null;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column)
		{
			String text = (String) value;
			updateData(text, isSelected, table, row);
			return panel;
		}
	}

	/**
	 * @param row
	 * @return
	 */
	public ResultStatus getStatus(int row)
	{
		List<TeamResult> teamResults = model.getTeamResults();
		String team = (String) table.getValueAt(row, COLUMN_TEAMNAME);
		TeamResult entry = getTeamEntry(team, teamResults);
		if (entry == null) {
			return ResultStatus.NO_RESULT;
		}
		if (entry.isValid()) {
			return ResultStatus.SUCCESS;
		} else {
			return ResultStatus.FAILED;
		}
	}

}
