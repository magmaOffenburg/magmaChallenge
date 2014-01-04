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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import magma.tools.benchmark.model.BenchmarkConfiguration;
import magma.tools.benchmark.model.BenchmarkMain;
import magma.tools.benchmark.model.IModelReadWrite;
import magma.tools.benchmark.model.InvalidConfigFileException;
import magma.tools.benchmark.model.TeamConfiguration;
import magma.tools.benchmark.view.BenchmarkView;

/**
 * 
 * @author kdorer
 */
public class BenchmarkController
{
	private IModelReadWrite model;

	private BenchmarkView view;

	public static void main(String[] args)
	{
		new BenchmarkController();
	}

	/**
	 * 
	 */
	public BenchmarkController()
	{
		model = new BenchmarkMain();
		view = BenchmarkView.getInstance(model);
		view.addCompetitionButtonListener(new CompetitionListener(false));
		view.addOpenButtonListener(new LoadConfigFileListener());
		view.addTestButtonListener(new CompetitionListener(true));
		view.addStopButtonListener(new StopListener());
		view.addKillServerListener(new KillServerListener());
		view.setVisible(true);
	}

	/**
	 * listener for competition button
	 * 
	 * @author kdorer
	 */
	class CompetitionListener implements ActionListener
	{
		private boolean isTest;

		/**
		 * @param b
		 */
		public CompetitionListener(boolean isTest)
		{
			this.isTest = isTest;
		}

		public void actionPerformed(ActionEvent arg0)
		{
			BenchmarkConfiguration config = view.getBenchmarkConfiguration();
			if (isTest) {
				config.setRuntime(4);
				config.setAverageOutRuns(1);
			}
			List<TeamConfiguration> teamConfigurations = view
					.getTeamConfiguration();
			view.disableEditing();
			model.start(config, teamConfigurations);
		}
	}

	/**
	 * listener for stop button
	 * 
	 * @author kdorer
	 */
	class StopListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			model.stop();
		}
	}

	/**
	 * listener for stop button
	 * 
	 * @author kdorer
	 */
	class KillServerListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			model.stopServer();
		}
	}

	/**
	 * listener for stop button
	 * 
	 * @author kdorer
	 */
	class LoadConfigFileListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			try {
				File file = view.getFileName();
				if (file == null) {
					return;
				}
				List<TeamConfiguration> loadConfigFile = model.loadConfigFile(file);
				view.updateConfigTable(loadConfigFile);

			} catch (InvalidConfigFileException e) {
				view.showErrorMessage(e.getMessage());
			}
		}
	}
}
