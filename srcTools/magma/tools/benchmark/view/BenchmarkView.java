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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import magma.tools.benchmark.model.BenchmarkConfiguration;
import magma.tools.benchmark.model.IModelReadOnly;
import magma.tools.benchmark.model.TeamConfiguration;
import magma.tools.benchmark.model.TeamResult;
import magma.util.observer.IObserver;

/**
 * 
 * @author kdorer
 */
public class BenchmarkView extends JFrame implements IObserver<IModelReadOnly>
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

	private static final long serialVersionUID = 1L;

	private JTextField runTime;

	private JTable table;

	private JScrollPane scrollPane;

	private JToolBar toolBar;

	private JButton btnTest;

	private JButton btnCompetition;

	private JButton btnStop;

	private JLabel lblAvgOutRuns;

	private JTextField averageRuns;

	private JButton btnStopServer;

	private JButton btnOpenScript;

	private JButton btnOpen;

	private IModelReadOnly model;

	private JFileChooser fc;

	// private JLabel lblServer;

	private JTextField serverIP;

	private JLabel lblServerport;

	private JTextField serverPort;

	private JLabel lblProxyport;

	private JTextField proxyPort;

	private JLabel lblTrainerport;

	private JTextField trainerPort;

	private JCheckBox chckbxVerbose;

	private JButton btnAbout;

	public static BenchmarkView getInstance(IModelReadOnly model,
			String defaultPath)
	{
		BenchmarkView view = new BenchmarkView(model, defaultPath);
		model.attach(view);
		return view;
	}

	private BenchmarkView(IModelReadOnly model, String defaultPath)
	{
		this.model = model;
		fc = new JFileChooser(defaultPath);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Run Challenge Benchmark");
		getContentPane().setLayout(new BorderLayout(0, 0));
		setSize(1000, 600);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

		JLabel lblRuntime = new JLabel("Runtime:");
		panel.add(lblRuntime);

		runTime = new JTextField();
		runTime.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e)
			{
				runTime.setText("" + getNumber(runTime.getText(), 2, 20));
			}
		});
		runTime.setHorizontalAlignment(SwingConstants.RIGHT);
		runTime.setText("10");
		panel.add(runTime);
		runTime.setColumns(4);

		lblAvgOutRuns = new JLabel("Avg out runs:");
		panel.add(lblAvgOutRuns);

		averageRuns = new JTextField();
		averageRuns.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e)
			{
				averageRuns.setText("" + getNumber(averageRuns.getText(), 1, 200));
			}
		});
		averageRuns.setEnabled(true);
		averageRuns.setEditable(true);
		averageRuns.setText("3");
		panel.add(averageRuns);
		averageRuns.setColumns(4);

		// lblServer = new JLabel("Server:");
		// panel.add(lblServer);

		serverIP = new JTextField();
		serverIP.setText("127.0.0.1");
		// panel.add(serverIP);
		// serverIP.setColumns(8);

		lblServerport = new JLabel("ServerPort:");
		panel.add(lblServerport);

		serverPort = new JTextField();
		serverPort.setText("3100");
		panel.add(serverPort);
		serverPort.setColumns(5);

		lblProxyport = new JLabel("ProxyPort:");
		panel.add(lblProxyport);

		proxyPort = new JTextField();
		proxyPort.setText("3110");
		panel.add(proxyPort);
		proxyPort.setColumns(5);

		lblTrainerport = new JLabel("TrainerPort:");
		panel.add(lblTrainerport);

		trainerPort = new JTextField();
		trainerPort.setText("3200");
		panel.add(trainerPort);
		trainerPort.setColumns(5);

		chckbxVerbose = new JCheckBox("Verbose");
		panel.add(chckbxVerbose);

		scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		createTeamTable(null);

		toolBar = new JToolBar();
		getContentPane().add(toolBar, BorderLayout.SOUTH);

		btnOpenScript = new JButton("Open Start Script...");
		btnOpenScript.setIcon(new ImageIcon(BenchmarkView.class
				.getResource("/images/runChallenge/documentOpen_16.png")));
		toolBar.add(btnOpenScript);

		btnOpen = new JButton("Open Competition...");
		btnOpen.setIcon(new ImageIcon(BenchmarkView.class
				.getResource("/images/runChallenge/documentOpen_16.png")));
		toolBar.add(btnOpen);

		btnTest = new JButton("Test");
		btnTest.setIcon(new ImageIcon(BenchmarkView.class
				.getResource("/images/runChallenge/info_16.png")));
		toolBar.add(btnTest);

		btnCompetition = new JButton("Competition");
		btnCompetition.setIcon(new ImageIcon(BenchmarkView.class
				.getResource("/images/runChallenge/execute_16.png")));
		toolBar.add(btnCompetition);

		btnStop = new JButton("Stop");
		btnStop.setIcon(new ImageIcon(BenchmarkView.class
				.getResource("/images/runChallenge/processStop_16.png")));
		btnStop.setEnabled(false);
		toolBar.add(btnStop);

		btnStopServer = new JButton("Stop Server");
		btnStopServer.setIcon(new ImageIcon(BenchmarkView.class
				.getResource("/images/runChallenge/helpAbout_16.png")));
		toolBar.add(btnStopServer);

		btnAbout = new JButton("About");
		btnAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				String text = "Run Challenge Benchmark Tool.\n\n";
				text += "Provided by the magmaOffenburg team.\n";
				text += "Version 1.0";
				JOptionPane.showMessageDialog(BenchmarkView.this, text,
						"Run Challenge Benchmark Tool",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		btnAbout.setIcon(new ImageIcon(BenchmarkView.class
				.getResource("/images/runChallenge/helpContents_16.png")));
		toolBar.add(btnAbout);
	}

	private int getNumber(String text, int minimum, int maximum)
	{
		try {
			int number = Integer.parseInt(text);
			if (number < minimum || number > maximum) {
				return minimum;
			}
			return number;
		} catch (NumberFormatException e) {
			return minimum;
		}
	}

	public void addOpenScriptButtonListener(ActionListener listener)
	{
		btnOpenScript.addActionListener(listener);
	}

	public void addOpenButtonListener(ActionListener listener)
	{
		btnOpen.addActionListener(listener);
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

	public void addKillServerListener(ActionListener listener)
	{
		btnStopServer.addActionListener(listener);
	}

	public BenchmarkConfiguration getBenchmarkConfiguration()
	{
		String sIP = serverIP.getText();
		int sPort = getNumber(serverPort.getText(), 1, 65536);
		int pPort = getNumber(proxyPort.getText(), 1, 65536);
		int tPort = getNumber(trainerPort.getText(), 1, 65536);
		int averageOutRuns = getNumber(averageRuns.getText(), 1, 200);
		int time = getNumber(runTime.getText(), 2, 20);
		boolean verbose = chckbxVerbose.isSelected();
		return new BenchmarkConfiguration(sIP, sPort, pPort, tPort,
				averageOutRuns, time, verbose);
	}

	public List<TeamConfiguration> getTeamConfiguration()
	{
		List<TeamConfiguration> result = new ArrayList<TeamConfiguration>();
		int teamid = 0;
		String teamName;
		float dropHeight = 0.65f;
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
		btnOpenScript.setEnabled(false);
		btnOpen.setEnabled(false);
		btnTest.setEnabled(false);
		btnCompetition.setEnabled(false);
		btnStop.setEnabled(true);
	}

	public void enableEditing()
	{
		table.setEnabled(true);
		btnOpenScript.setEnabled(true);
		btnOpen.setEnabled(true);
		btnTest.setEnabled(true);
		btnCompetition.setEnabled(true);
		btnStop.setEnabled(false);
	}

	@Override
	public void update(IModelReadOnly model)
	{
		List<TeamResult> teamResults = model.getTeamResults();

		for (int i = 0; i < teamResults.size(); i++) {
			TeamResult teamResult = teamResults.get(i);

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
	 * @return
	 */
	public File getFileName()
	{
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile();
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
		JOptionPane.showMessageDialog(this, jsp, "Problem",
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * @param loadConfigFile
	 */
	public void updateConfigTable(List<TeamConfiguration> loadConfigFile)
	{
		createTeamTable(loadConfigFile);
	}

	/**
	 * 
	 */
	private void createTeamTable(List<TeamConfiguration> config)
	{
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(table);
		table.setCellSelectionEnabled(true);
		DefaultTableModel tableModel = DefaultTableModelExtension
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
						JOptionPane.showMessageDialog(BenchmarkView.this, text, team,
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
			statusButton.setIcon(new ImageIcon(BenchmarkView.class
					.getResource("/images/runChallenge/info_16.png")));
			statusButton.setSize(30, 30);
			statusButton.addActionListener(new ActionListener() {
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

		public Component getTableCellEditorComponent(JTable table, Object value,
				boolean isSelected, int row, int column)
		{
			String text = (String) value;
			updateData(text, true, table, row);
			return panel;
		}

		public Object getCellEditorValue()
		{
			return null;
		}

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
