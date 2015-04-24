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

package magma.tools.benchmark.model.bench;

import java.io.File;

import magma.util.UnixCommandUtil;
import magma.util.file.StreamBufferer;

public class SinglePlayerLauncher
{
	/** number of cycles to wait before next player starts */
	// for more than one player
	// private static final int WAIT_CYCLES_LAUNCHING = 10;

	private static final int WAIT_CYCLES_LAUNCHING = 1;

	private String serverIP;

	private int agentPort;

	private String binary;

	private int cyclesToWait;

	private boolean started;

	private String path;

	private StreamBufferer stdOut;

	private StreamBufferer stdErr;

	public SinglePlayerLauncher(String serverIP, int agentPort, String path,
			String binary)
	{
		this.serverIP = serverIP;
		this.agentPort = agentPort;
		this.path = path;
		this.binary = binary;
		started = false;
		cyclesToWait = WAIT_CYCLES_LAUNCHING;
	}

	/**
	 * Launches the player
	 * @return false if no more players have to be launched
	 */
	public boolean launchPlayer(RunInformation runInfo, int playersOnField)
	{
		if (playersOnField == 1) {
			cyclesToWait--;
			if (cyclesToWait < 0) {
				cyclesToWait = WAIT_CYCLES_LAUNCHING;
				started = false;
				return false;
			}
		}
		if (!started) {
			startPlayer(runInfo);
			started = true;
		}
		return true;
	}

	private void startPlayer(RunInformation runInfo)
	{
		String fullPath = path + binary;
		if (!validatePath(fullPath)) {
			return;
		}

		String command = "bash " + path + binary + " " + serverIP + " "
				+ agentPort + " " + runInfo.getBeamX() + " " + runInfo.getBeamY();
		System.out.println(command);

		File workingDir = new File(path);
		Process ps = UnixCommandUtil.launch(command, null, workingDir);
		stdOut = new StreamBufferer(ps.getInputStream(), 5000);
		stdErr = new StreamBufferer(ps.getErrorStream(), 5000);
	}

	public void stopPlayer()
	{
		if (!validatePath(path)) {
			return;
		}

		String command = "bash " + path + "kill.sh";
		System.out.println(command);
		File workingDir = new File(path);

		Process ps = UnixCommandUtil.launch(command, null, workingDir);
		stdOut = new StreamBufferer(ps.getInputStream(), 5000);
		stdErr = new StreamBufferer(ps.getErrorStream(), 5000);
	}

	public String getStatusText()
	{
		if (stdOut == null || stdErr == null) {
			return "";
		}
		return "stderr: " + stdErr.getBuffer() + "\nstdout: "
				+ stdOut.getBuffer();
	}

	private boolean validatePath(String path)
	{
		File file = new File(path);
		if (file.exists()) {
			return true;
		}
		System.out.println("Path " + file.getAbsolutePath() + " does not exist.");
		return false;
	}
}