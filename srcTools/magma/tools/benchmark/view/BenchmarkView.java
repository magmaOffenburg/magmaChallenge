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

/**
 * 
 * @author kdorer
 */
public class BenchmarkView extends JFrame
{
	public BenchmarkView()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Run Challenge Benchmark");
		getContentPane().setLayout(new BorderLayout(0, 0));
		setSize(600, 400);

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
		textField_1.setText("40");
		panel.add(textField_1);
		textField_1.setColumns(4);

		scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		scrollPane.setViewportView(table);
		table.setCellSelectionEnabled(true);
		table.setModel(new DefaultTableModel(
				new Object[][] {
						{
								"/host/Data/Projekte/RoboCup/Konfigurationen/runChallenge/",
								"magmaOffenburg", null, null, null },
						{ null, null, null, null, null },
						{ null, null, null, null, null },
						{ null, null, null, null, null },
						{ null, null, null, null, null }, }, new String[] { "path",
						"team", "score", "falls", "score progress" }) {
			Class[] columnTypes = new Class[] { String.class, String.class,
					Float.class, Integer.class, Object.class };

			@Override
			public Class getColumnClass(int columnIndex)
			{
				return columnTypes[columnIndex];
			}
		});
		table.getColumnModel().getColumn(0).setPreferredWidth(302);
		table.getColumnModel().getColumn(1).setPreferredWidth(102);
		table.getColumnModel().getColumn(2).setPreferredWidth(56);
		table.getColumnModel().getColumn(3).setPreferredWidth(55);
		table.getColumnModel().getColumn(4).setPreferredWidth(85);
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
	}

	/**
	 * 
	 */
	public void addCompetitionButtonListener(ActionListener listener)
	{
		btnCompetition.addActionListener(listener);
	}

	private static final long serialVersionUID = 1L;

	private JTextField textField;

	private JTable table;

	private JScrollPane scrollPane;

	private JToolBar toolBar;

	private JButton btnTest;

	private JButton btnCompetition;

	private JLabel lblAvgOutRuns;

	private JTextField textField_1;

	public static void main(String[] args)
	{
		BenchmarkView view = new BenchmarkView();
		view.setVisible(true);
	}
}
