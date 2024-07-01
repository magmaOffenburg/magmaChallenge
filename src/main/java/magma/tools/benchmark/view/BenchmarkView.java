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

import hso.autonomy.util.observer.IObserver;
import java.awt.BorderLayout;
import java.awt.Dimension;
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
import javax.swing.WindowConstants;
import magma.tools.benchmark.ChallengeType;
import magma.tools.benchmark.model.BenchmarkConfiguration;
import magma.tools.benchmark.model.IModelReadOnly;
import magma.tools.benchmark.model.IModelReadWrite;
import magma.tools.benchmark.model.TeamConfiguration;
import magma.tools.benchmark.view.bench.BenchmarkTableView;

/**
 *
 * @author kdorer
 */
public class BenchmarkView extends JFrame implements IObserver<IModelReadOnly>
{
	public static final String VERSION = "3.2-beta1";

	private final JComboBox<ChallengeType> challenge;

	private final JTextField runTime;

	private JScrollPane scrollPane;

	private final JButton btnTest;

	private final JButton btnCompetition;

	private final JButton btnStop;

	private final JTextField averageRuns;

	private final JButton btnStopServer;

	private final JButton btnOpenScript;

	private final JButton btnOpen;

	private IModelReadOnly model;

	private final JFileChooser fc;

	// private JLabel lblServer;

	private final JTextField serverIP;

	private final JTextField serverPort;

	private final JTextField proxyPort;

	private final JTextField trainerPort;

	private final JTextField seedBox;

	private final JCheckBox checkboxVerbose;

	private BenchmarkTableView tableView;

	private final String roboVizServer;

	public static BenchmarkView getInstance(IModelReadOnly model, BenchmarkTableView tableView,
			ChallengeType defaultChallenge, String defaultPath, String roboVizServer)
	{
		BenchmarkView view = new BenchmarkView(model, tableView, defaultChallenge, defaultPath, roboVizServer);
		model.attach(view);
		return view;
	}

	private BenchmarkView(IModelReadOnly model, BenchmarkTableView tableView, ChallengeType defaultChallenge,
			String defaultPath, String roboVizServer)
	{
		this.model = model;
		this.tableView = tableView;
		this.roboVizServer = roboVizServer;
		fc = new JFileChooser(defaultPath);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Magma Challenge Benchmark");
		getContentPane().setLayout(new BorderLayout(0, 0));
		setSize(1100, 200);
		setLocationRelativeTo(null);
		try {
			setIconImage(ImageIO.read(BenchmarkView.class.getResource("/images/magma32.png")));
		} catch (IOException e) {
		}

		scrollPane = new JScrollPane();
		scrollPane.setViewportView(tableView.updateConfigTable(null));
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);

		challenge = new JComboBox<>(ChallengeType.values());
		challenge.setSelectedItem(defaultChallenge);
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
		runTime.setText(String.valueOf(BenchmarkConfiguration.DEFAULT_RUNTIME));
		panel.add(runTime);
		runTime.setColumns(4);

		JLabel lblAvgOutRuns = new JLabel("Avg out runs:");
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
		averageRuns.setText(String.valueOf(BenchmarkConfiguration.DEFAULT_AVERAGE_OUT_RUNS));
		panel.add(averageRuns);
		averageRuns.setColumns(4);

		// lblServer = new JLabel("Server:");
		// panel.add(lblServer);

		serverIP = new JTextField();
		serverIP.setText(BenchmarkConfiguration.DEFAULT_SERVER_IP);
		// panel.add(serverIP);
		// serverIP.setColumns(8);

		JLabel lblServerPort = new JLabel("ServerPort:");
		panel.add(lblServerPort);

		serverPort = new JTextField();
		serverPort.setText(String.valueOf(BenchmarkConfiguration.DEFAULT_SERVER_PORT));
		panel.add(serverPort);
		serverPort.setColumns(5);

		JLabel lblProxyPort = new JLabel("ProxyPort:");
		panel.add(lblProxyPort);

		proxyPort = new JTextField();
		proxyPort.setText(String.valueOf(BenchmarkConfiguration.DEFAULT_PROXY_PORT));
		panel.add(proxyPort);
		proxyPort.setColumns(5);

		JLabel lblTrainerPort = new JLabel("TrainerPort:");
		panel.add(lblTrainerPort);

		trainerPort = new JTextField();
		trainerPort.setText(String.valueOf(BenchmarkConfiguration.DEFAULT_TRAINER_PORT));
		panel.add(trainerPort);
		trainerPort.setColumns(5);

		JLabel lblSeedBox = new JLabel("SeedBox:");
		panel.add(lblSeedBox);

		seedBox = new JTextField();
		seedBox.setText(String.valueOf(BenchmarkConfiguration.DEFAULT_RANDOM_SEED));
		panel.add(seedBox);
		seedBox.setColumns(5);

		checkboxVerbose = new JCheckBox("Verbose");
		checkboxVerbose.setSelected(BenchmarkConfiguration.DEFAULT_VERBOSE);
		panel.add(checkboxVerbose);

		JToolBar toolBar = new JToolBar();
		getContentPane().add(toolBar, BorderLayout.SOUTH);

		btnOpenScript = new JButton("Open Start Script Folder...");
		btnOpenScript.setIcon(new ImageIcon(BenchmarkView.class.getResource("/images/documentOpen_16.png")));
		toolBar.add(btnOpenScript);

		btnOpen = new JButton("Open Competition...");
		btnOpen.setIcon(new ImageIcon(BenchmarkView.class.getResource("/images/documentOpen_16.png")));
		toolBar.add(btnOpen);

		btnTest = new JButton("Test");
		btnTest.setIcon(new ImageIcon(BenchmarkView.class.getResource("/images/info_16.png")));
		toolBar.add(btnTest);

		btnCompetition = new JButton("Competition");
		btnCompetition.setIcon(new ImageIcon(BenchmarkView.class.getResource("/images/execute_16.png")));
		toolBar.add(btnCompetition);

		btnStop = new JButton("Stop");
		btnStop.setIcon(new ImageIcon(BenchmarkView.class.getResource("/images/processStop_16.png")));
		btnStop.setEnabled(false);
		toolBar.add(btnStop);

		btnStopServer = new JButton("Stop Server");
		btnStopServer.setIcon(new ImageIcon(BenchmarkView.class.getResource("/images/helpAbout_16.png")));
		toolBar.add(btnStopServer);

		JButton btnAbout = new JButton("About");
		btnAbout.addActionListener(e -> {
			String text = "Magma Challenge Benchmark Tool.\n\n";
			text += "Provided by the magmaOffenburg team.\n";
			text += "Version " + VERSION;
			JOptionPane.showMessageDialog(
					BenchmarkView.this, text, "Magma Challenge Benchmark Tool", JOptionPane.INFORMATION_MESSAGE);
		});
		btnAbout.setIcon(new ImageIcon(BenchmarkView.class.getResource("/images/helpContents_16.png")));
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
		String serverIP = this.serverIP.getText();
		int serverPort = getNumber(this.serverPort.getText(), 1, 65536);
		int proxyPort = getNumber(this.proxyPort.getText(), 1, 65536);
		int trainerPort = getNumber(this.trainerPort.getText(), 1, 65536);
		int averageOutRuns = getNumber(averageRuns.getText(), 1, 200);
		int time = getNumber(runTime.getText(), 2, 20);
		int seed = getNumber(this.seedBox.getText(), 1, 100000000);
		boolean verbose = checkboxVerbose.isSelected();
		return new BenchmarkConfiguration(serverIP, serverPort, proxyPort, trainerPort, averageOutRuns, time, verbose,
				false, seed, roboVizServer);
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

	public File getFileName(int fileSelectionMode)
	{
		fc.resetChoosableFileFilters();
		fc.setFileSelectionMode(fileSelectionMode);

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
		JOptionPane.showMessageDialog(this, jsp, "Problem", JOptionPane.ERROR_MESSAGE);
	}

	public List<TeamConfiguration> getTeamConfiguration()
	{
		return tableView.getTeamConfiguration();
	}

	public void updateConfigTable(List<TeamConfiguration> loadConfigFile)
	{
		scrollPane.setViewportView(tableView.updateConfigTable(loadConfigFile));
	}

	public void setDependencies(IModelReadWrite model2, BenchmarkTableView tableView2)
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
