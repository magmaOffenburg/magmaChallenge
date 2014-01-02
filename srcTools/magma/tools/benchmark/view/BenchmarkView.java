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

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import magma.tools.benchmark.model.IModelReadOnly;
import magma.tools.benchmark.model.TeamConfiguration;
import magma.util.observer.IObserver;

/**
 * 
 * @author kdorer
 */
public class BenchmarkView extends JFrame implements IObserver<IModelReadOnly>
{
	private static final int COLUMN_TEAMNAME = 0;

	private static final int COLUMN_SCORE = 1;

	private static final int COLUMN_FALLS = 2;

	private static final int COLUMN_SPEED = 3;

	private static final int COLUMN_PATH = 4;

	private static final int COLUMN_BINARY = 5;

	private IModelReadOnly model;

	public static BenchmarkView getInstance(IModelReadOnly model)
	{
		BenchmarkView view = new BenchmarkView(model);
		model.attach(view);
		return view;
	}

	@SuppressWarnings("serial")
	private BenchmarkView(IModelReadOnly model)
	{
		this.model = model;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Run Challenge Benchmark");
		getContentPane().setLayout(new BorderLayout(0, 0));
		setSize(800, 600);

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

		JLabel lblRuntime = new JLabel("Runtime:");
		panel.add(lblRuntime);

		textField = new JTextField();
		textField.setHorizontalAlignment(SwingConstants.RIGHT);
		textField.setText("10");
		panel.add(textField);
		textField.setColumns(4);

		lblAvgOutRuns = new JLabel("Avg out runs:");
		panel.add(lblAvgOutRuns);

		textField_1 = new JTextField();
		textField_1.setEnabled(true);
		textField_1.setEditable(true);
		textField_1.setText("3");
		panel.add(textField_1);
		textField_1.setColumns(4);

		scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		scrollPane.setViewportView(table);
		table.setCellSelectionEnabled(true);
		table.setModel(new DefaultTableModel(new Object[][] {
				{ "magmaOffenburg", null, null, null,
						"/host/Data/Projekte/RoboCup/Konfigurationen/runChallenge/",
						"startPlayerRunning.sh" },
				{ null, null, null, null, null, null },
				{ null, null, null, null, null, null },
				{ null, null, null, null, null, null },
				{ null, null, null, null, null, null }, }, new String[] { "team",
				"score", "falls", "speed", "path", "binary" }) {
			Class[] columnTypes = new Class[] { String.class, Float.class,
					Integer.class, Float.class, String.class, String.class };

			@Override
			public Class getColumnClass(int columnIndex)
			{
				return columnTypes[columnIndex];
			}
		});
		table.getColumnModel().getColumn(0).setPreferredWidth(102);
		table.getColumnModel().getColumn(1).setPreferredWidth(56);
		table.getColumnModel().getColumn(2).setPreferredWidth(55);
		table.getColumnModel().getColumn(3).setPreferredWidth(85);
		table.getColumnModel().getColumn(4).setPreferredWidth(302);
		table.getColumnModel().getColumn(5).setPreferredWidth(156);
		table.setColumnSelectionAllowed(true);
		table.setAutoCreateRowSorter(true);

		toolBar = new JToolBar();
		getContentPane().add(toolBar, BorderLayout.SOUTH);

		btnTest = new JButton("Test");
		btnTest.setIcon(new ImageIcon(BenchmarkView.class
				.getResource("/images/info_16.png")));
		toolBar.add(btnTest);

		btnCompetition = new JButton("Competition");
		btnCompetition.setIcon(new ImageIcon(BenchmarkView.class
				.getResource("/images/execute_16.png")));
		toolBar.add(btnCompetition);

		btnStop = new JButton("Stop");
		btnStop.setIcon(new ImageIcon(BenchmarkView.class
				.getResource("/images/processStop_16.png")));
		toolBar.add(btnStop);
	}

	public void addTestButtonListener(ActionListener listener)
	{
		btnTest.addActionListener(listener);
	}

	public void addCompetitionButtonListener(ActionListener listener)
	{
		btnCompetition.addActionListener(listener);
	}

	public void addStopButtonListener(ActionListener listener)
	{
		btnStop.addActionListener(listener);
	}

	public List<TeamConfiguration> getTeamConfiguration()
	{
		List<TeamConfiguration> result = new ArrayList<TeamConfiguration>();
		int teamid = 0;
		String teamName;
		do {
			String teamPath = (String) table.getValueAt(teamid, COLUMN_PATH);
			String teamBinary = (String) table.getValueAt(teamid, COLUMN_BINARY);
			teamName = (String) table.getValueAt(teamid, COLUMN_TEAMNAME);
			if (teamName != null && !teamName.isEmpty()) {
				TeamConfiguration config = new TeamConfiguration(teamName,
						teamPath, teamBinary);
				result.add(config);
				teamid++;
			}
		} while (teamName != null && !teamName.isEmpty());

		return result;
	}

	private static final long serialVersionUID = 1L;

	private JTextField textField;

	private JTable table;

	private JScrollPane scrollPane;

	private JToolBar toolBar;

	private JButton btnTest;

	private JButton btnCompetition;

	private JButton btnStop;

	private JLabel lblAvgOutRuns;

	private JTextField textField_1;

	@Override
	public void update(IModelReadOnly model)
	{
		float averageScore = model.getAverageScore();
		table.setValueAt(averageScore, 0, COLUMN_SCORE);
	}
}
