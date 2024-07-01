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

package magma.tools.benchmark.view.bench;

import hso.autonomy.util.observer.IObserver;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import magma.tools.benchmark.model.IModelReadOnly;
import magma.tools.benchmark.model.ITeamResult;
import magma.tools.benchmark.model.TeamConfiguration;

/**
 *
 * @author kdorer
 */
public abstract class BenchmarkTableView implements IObserver<IModelReadOnly>
{
	protected JTable table;

	protected final IModelReadOnly model;

	private final String startScriptFolder;

	protected BenchmarkTableView(IModelReadOnly model, String startScriptFolder)
	{
		this.model = model;
		this.startScriptFolder = startScriptFolder;
		createTeamTable();
	}

	public abstract List<TeamConfiguration> getTeamConfiguration();

	public void disableEditing()
	{
		table.setEnabled(false);
	}

	public void enableEditing()
	{
		table.setEnabled(true);
	}

	protected List<TeamConfiguration> getDefaultConfig()
	{
		return Collections.singletonList(new TeamConfiguration("magma", startScriptFolder, 0.4f));
	}

	private ITeamResult getTeamEntry(String name, List<ITeamResult> teamResults)
	{
		for (ITeamResult result : teamResults) {
			if (name.equals(result.getName())) {
				return result;
			}
		}
		return null;
	}

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
		JOptionPane.showMessageDialog(null, jsp, "Problem", JOptionPane.ERROR_MESSAGE);
	}

	public JTable updateConfigTable(List<TeamConfiguration> loadConfigFile)
	{
		return loadConfigFile == null ? createTeamTable() : createTeamTable(loadConfigFile);
	}

	protected JTable createTeamTable()
	{
		return createTeamTable(getDefaultConfig());
	}

	protected abstract JTable createTeamTable(List<TeamConfiguration> config);

	public abstract ResultStatus getStatus(int row);

	protected ResultStatus getLocalStatus(int row, int column)
	{
		List<ITeamResult> teamResults = model.getTeamResults();
		String team = (String) table.getValueAt(row, column);
		ITeamResult entry = getTeamEntry(team, teamResults);
		if (entry == null) {
			return ResultStatus.NO_RESULT;
		}
		if (entry.isValid()) {
			return ResultStatus.SUCCESS;
		} else {
			return ResultStatus.FAILED;
		}
	}

	/**
	 * The states of a benchmark run
	 */
	protected enum ResultStatus
	{
		NO_RESULT,
		SUCCESS,
		FAILED
	}

	/**
	 * Listener for status button clicks
	 */
	protected class TableMouseListener extends MouseAdapter
	{
		private final int columnStatus;

		private final int columnTeamname;

		public TableMouseListener(int columnStatus, int columnTeamname)
		{
			this.columnStatus = columnStatus;
			this.columnTeamname = columnTeamname;
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			JTable target = (JTable) e.getSource();
			int row = target.getSelectedRow();
			int column = target.getSelectedColumn();
			if (column == columnStatus) {
				List<ITeamResult> teamResults = model.getTeamResults();
				String team = (String) target.getValueAt(row, columnTeamname);
				ITeamResult result = getTeamEntry(team, teamResults);
				if (result != null) {
					String text = result.getStatusText();
					if (result.isValid()) {
						JOptionPane.showMessageDialog(null, text, team, JOptionPane.INFORMATION_MESSAGE);
					} else {
						showErrorMessage(text);
					}
				}
			}
		}
	}

	protected class BenchmarkTableCell extends AbstractCellEditor implements TableCellEditor, TableCellRenderer
	{
		final JPanel panel;

		final JButton statusButton;

		public BenchmarkTableCell()
		{
			statusButton = new JButton();
			statusButton.setIcon(new ImageIcon(BenchmarkTableView.class.getResource("/images/info_16.png")));
			statusButton.setSize(30, 30);
			statusButton.addActionListener(e -> System.out.println("pressed"));

			panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panel.add(statusButton);
		}

		private void updateData(String text, boolean isSelected, JTable table, int row)
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
		public Component getTableCellEditorComponent(
				JTable table, Object value, boolean isSelected, int row, int column)
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
		public Component getTableCellRendererComponent(
				JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
			String text = (String) value;
			updateData(text, isSelected, table, row);
			return panel;
		}
	}
}
