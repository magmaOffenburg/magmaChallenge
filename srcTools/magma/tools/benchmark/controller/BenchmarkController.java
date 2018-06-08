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

package magma.tools.benchmark.controller;

import hso.autonomy.util.commandline.Argument;
import hso.autonomy.util.commandline.EnumArgument;
import hso.autonomy.util.commandline.HelpArgument;
import hso.autonomy.util.commandline.StringArgument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import magma.tools.benchmark.ChallengeType;
import magma.tools.benchmark.UserInterface;
import magma.tools.benchmark.model.BenchmarkConfiguration;
import magma.tools.benchmark.model.IModelReadWrite;
import magma.tools.benchmark.model.InvalidConfigFileException;
import magma.tools.benchmark.model.TeamConfiguration;
import magma.tools.benchmark.model.bench.BenchmarkMain;
import magma.tools.benchmark.view.BenchmarkView;
import magma.tools.benchmark.view.bench.BenchmarkTableView;

/**
 * @author kdorer
 */
public class BenchmarkController
{
	private IModelReadWrite model;

	private BenchmarkView view;

	private final String startScriptFolder;

	private final String roboVizServer;

	public static void run(String[] args, UserInterface userInterface)
	{
		EnumArgument<ChallengeType> challengeArgument = new EnumArgument<>("challenge", ChallengeType.DEFAULT,
				"which challenge to run (CLI version) or to select by default (GUI version)", ChallengeType.class);
		StringArgument startScriptFolderArgument = new StringArgument(
				"startScriptFolder", "", "directory in which startChallengePlayer.sh (and kill.sh) are located");
		StringArgument roboVizServerArgument =
				new StringArgument("roboVizServer", "localhost", "which IP to connect to for RoboViz drawings");

		StringArgument defaultPathArgument =
				new StringArgument("defaultPath", "examples", "the initial path to use for file dialogs");

		new HelpArgument(challengeArgument, startScriptFolderArgument, roboVizServerArgument, defaultPathArgument)
				.parse(args);

		if (userInterface == UserInterface.CLI) {
			challengeArgument.setRequired(true);
			startScriptFolderArgument.setRequired(true);
		}

		ChallengeType challenge = challengeArgument.parse(args);
		String startScriptFolder = startScriptFolderArgument.parse(args);
		String roboVizServer = roboVizServerArgument.parse(args);
		String defaultPath = defaultPathArgument.parse(args);
		Argument.endParse(args);

		defaultPath = defaultPath.replaceFirst("^~", System.getProperty("user.home"));

		new BenchmarkController(userInterface, challenge, startScriptFolder, roboVizServer, defaultPath);
	}

	public BenchmarkController(UserInterface userInterface, ChallengeType challenge, String startScriptFolder,
			String roboVizServer, String defaultPath)
	{
		this.startScriptFolder = startScriptFolder;
		this.roboVizServer = roboVizServer;
		model = challenge.benchmarkMainConstructor.create(roboVizServer);
		System.out.println("magmaChallenge " + BenchmarkView.VERSION);

		switch (userInterface) {
		case CLI:
			model.start(new BenchmarkConfiguration(roboVizServer),
					Collections.singletonList(new TeamConfiguration("magma", startScriptFolder, 0.4f)));
			break;
		case GUI:
			BenchmarkTableView tableView = challenge.benchmarkTableViewConstructor.create(model, startScriptFolder);
			view = BenchmarkView.getInstance(model, tableView, challenge, defaultPath, roboVizServer);
			view.addChallengeListener(new ChallengeListener());
			view.addCompetitionButtonListener(new CompetitionListener(false));
			view.addOpenScriptButtonListener(new LoadScriptFolderListener());
			view.addOpenButtonListener(new LoadConfigFileListener());
			view.addTestButtonListener(new CompetitionListener(true));
			view.addStopButtonListener(new StopListener());
			view.addKillServerListener(new KillServerListener());
			view.setVisible(true);
			break;
		}
	}

	private class ChallengeListener implements ActionListener
	{
		@SuppressWarnings("unchecked")
		@Override
		public void actionPerformed(ActionEvent event)
		{
			ChallengeType challenge = (ChallengeType) ((JComboBox<ChallengeType>) event.getSource()).getSelectedItem();
			if (challenge == null) {
				return;
			}

			BenchmarkTableView tableView = challenge.benchmarkTableViewConstructor.create(model, startScriptFolder);
			IModelReadWrite newModel = challenge.benchmarkMainConstructor.create(roboVizServer);

			model.stop();
			model = newModel;
			view.setDependencies(model, tableView);
			view.revalidate();
		}
	}

	private class CompetitionListener implements ActionListener
	{
		private final boolean isTest;

		CompetitionListener(boolean isTest)
		{
			this.isTest = isTest;
		}

		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			BenchmarkConfiguration config = view.getBenchmarkConfiguration();
			if (isTest) {
				config.setRuntime(4);
				config.setAverageOutRuns(1);
				config.setIsTest(true);
			}
			List<TeamConfiguration> teamConfigurations = view.getTeamConfiguration();
			view.disableEditing();
			model.start(config, teamConfigurations);
		}
	}

	private class StopListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			model.stop();
		}
	}

	private class KillServerListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			model.stopServer();
		}
	}

	private class LoadConfigFileListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			try {
				File file = view.getFileName(JFileChooser.FILES_AND_DIRECTORIES);
				if (file == null) {
					return;
				}
				List<TeamConfiguration> loadConfigFile = model.loadConfigFile(file);
				model.resetModel();
				view.updateConfigTable(loadConfigFile);

			} catch (InvalidConfigFileException e) {
				view.showErrorMessage(e.getMessage());
			}
		}
	}

	private class LoadScriptFolderListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			File folder = getFolder();
			if (folder == null) {
				return;
			}

			TeamConfiguration config = new TeamConfiguration("teamname", folder.getAbsolutePath(), 0.4f);
			List<TeamConfiguration> loadConfigFile = Collections.singletonList(config);
			model.resetModel();
			view.updateConfigTable(loadConfigFile);
		}

		private File getFolder()
		{
			File folder;
			boolean retry;
			do {
				retry = false;
				folder = view.getFileName(JFileChooser.DIRECTORIES_ONLY);
				if (folder == null) {
					break; // cancel
				}
				if (!containsStartScript(folder)) {
					int selection = JOptionPane.showConfirmDialog(view,
							"Folder must contain a file named " + BenchmarkMain.START_SCRIPT_NAME + ". Retry?",
							"No start script found", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
					retry = (selection == JOptionPane.YES_OPTION);
					folder = null;
				}
			} while (retry);

			return folder;
		}

		private boolean containsStartScript(File folder)
		{
			if (folder == null) {
				return false;
			}

			File[] files = folder.listFiles();
			if (files == null) {
				return false;
			}

			for (File file : files) {
				if (file.getName().equals(BenchmarkMain.START_SCRIPT_NAME)) {
					return true;
				}
			}

			return false;
		}
	}
}
