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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import magma.tools.benchmark.model.BenchmarkConfiguration;
import magma.tools.benchmark.model.IModelReadOnly;
import magma.tools.benchmark.model.IModelReadWrite;
import magma.tools.benchmark.model.TeamConfiguration;
import magma.tools.benchmark.view.bench.BenchmarkTableView;
import magma.util.observer.IObserver;

/**
 * 
 * @author kdorer
 */
public class BenchmarkView extends JFrame implements IObserver<IModelReadOnly>
{
	private static final String VERSION = "Version 2.2";

	private static final long serialVersionUID = 1L;

	private JComboBox<String> challenge;

	private JTextField runTime;

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

	private BenchmarkTableView tableView;

	private final String roboVizServer;

	public static BenchmarkView getInstance(IModelReadOnly model,
			BenchmarkTableView tableView, String defaultPath, String roboVizServer)
	{
		BenchmarkView view = new BenchmarkView(model, tableView, defaultPath,
				roboVizServer);
		model.attach(view);
		return view;
	}

	private BenchmarkView(IModelReadOnly model, BenchmarkTableView tableView,
			String defaultPath, String roboVizServer)
	{
		this.model = model;
		this.tableView = tableView;
		this.roboVizServer = roboVizServer;
		fc = new JFileChooser(defaultPath);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Magma Challenge Benchmark");
		getContentPane().setLayout(new BorderLayout(0, 0));
		setSize(1000, 600);
		setLocationRelativeTo(null);
		try {
			setIconImage(ImageIO.read(BenchmarkView.class
					.getResource("/images/magma32.png")));
		} catch (IOException e) {

		}

		scrollPane = new JScrollPane();
		scrollPane.setViewportView(tableView.updateConfigTable(null));
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

		String[] items = { "Kick Challenge", "Run Challenge" };
		challenge = new JComboBox<>(items);
		panel.add(challenge);

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
		averageRuns.setText("1");
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

		toolBar = new JToolBar();
		getContentPane().add(toolBar, BorderLayout.SOUTH);

		btnOpenScript = new JButton("Open Start Script...");
		btnOpenScript.setIcon(new ImageIcon(BenchmarkView.class
				.getResource("/images/documentOpen_16.png")));
		toolBar.add(btnOpenScript);

		btnOpen = new JButton("Open Competition...");
		btnOpen.setIcon(new ImageIcon(BenchmarkView.class
				.getResource("/images/documentOpen_16.png")));
		toolBar.add(btnOpen);

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
		btnStop.setEnabled(false);
		toolBar.add(btnStop);

		btnStopServer = new JButton("Stop Server");
		btnStopServer.setIcon(new ImageIcon(BenchmarkView.class
				.getResource("/images/helpAbout_16.png")));
		toolBar.add(btnStopServer);

		btnAbout = new JButton("About");
		btnAbout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				String text = "Magma Challenge Benchmark Tool.\n\n";
				text += "Provided by the magmaOffenburg team.\n";
				text += VERSION;
				JOptionPane.showMessageDialog(BenchmarkView.this, text,
						"Magma Challenge Benchmark Tool",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		btnAbout.setIcon(new ImageIcon(BenchmarkView.class
				.getResource("/images/helpContents_16.png")));
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

	public void addChallengeListener(ActionListener listener)
	{
		challenge.addActionListener(listener);
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
				averageOutRuns, time, verbose, false, 123l, roboVizServer);
	}

	public void disableEditing()
	{
		tableView.disableEditing();
		challenge.setEnabled(false);
		btnOpenScript.setEnabled(false);
		btnOpen.setEnabled(false);
		btnTest.setEnabled(false);
		btnCompetition.setEnabled(false);
		btnStop.setEnabled(true);
	}

	public void enableEditing()
	{
		tableView.enableEditing();
		challenge.setEnabled(true);
		btnOpenScript.setEnabled(true);
		btnOpen.setEnabled(true);
		btnTest.setEnabled(true);
		btnCompetition.setEnabled(true);
		btnStop.setEnabled(false);
	}

	@Override
	public void update(IModelReadOnly model)
	{
		if (!model.isRunning()) {
			enableEditing();
		}
	}

	public File getFileName(final String extension)
	{
		fc.resetChoosableFileFilters();
		if (extension != null) {
			fc.setFileFilter(new FileNameExtensionFilter("*" + extension,
					extension));
		}

		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile();
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
		JOptionPane.showMessageDialog(this, jsp, "Problem",
				JOptionPane.ERROR_MESSAGE);
	}

	public List<TeamConfiguration> getTeamConfiguration()
	{
		return tableView.getTeamConfiguration();
	}

	public void updateConfigTable(List<TeamConfiguration> loadConfigFile)
	{
		scrollPane.setViewportView(tableView.updateConfigTable(loadConfigFile));
	}

	public void setDependencies(IModelReadWrite model2,
			BenchmarkTableView tableView2)
	{
		model = model2;
		tableView = tableView2;
		getContentPane().remove(scrollPane);
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(tableView.updateConfigTable(null));
		model.attach(this);
		model.attach(tableView2);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
	}
}
