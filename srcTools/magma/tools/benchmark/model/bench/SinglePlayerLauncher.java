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

    // private String teamname;

    public SinglePlayerLauncher(String serverIP, int agentPort, String teamname,
                                String path, String binary)
    {
        this.serverIP = serverIP;
        this.agentPort = agentPort;
        // this.teamname = teamname;
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
        // String command = "./runTeam.sh " + teamname + " " + path + " " +
        // binary;
        String command = "bash " + path + binary + " " + serverIP + " "
                + agentPort + " " + runInfo.getBeamX() + " " + runInfo.getBeamY();
        System.out.println(command);

        File workingDir = new File(path);
        // File workingDir = new File("/home/robocup/magmaRunChallenge");
        // File workingDir = new File("/home/kdorer");
        Process ps = UnixCommandUtil.launch(command, null, workingDir);
        stdOut = new StreamBufferer(ps.getInputStream(), 5000);
        stdErr = new StreamBufferer(ps.getErrorStream(), 5000);
    }

    public void stopPlayer()
    {
        // String command = "./stopTeam.sh " + teamname + " " + path;
        String command = "bash " + path + "kill.sh";
        System.out.println(command);
        File workingDir = new File(path);
        // File workingDir = new File("/home/robocup/magmaRunChallenge");
        // File workingDir = new File("/home/kdorer");
        // Process ps = UnixCommandUtil.launch(command, null, workingDir);
        Process ps = UnixCommandUtil.launch(command, null, workingDir);
        stdOut = new StreamBufferer(ps.getInputStream(), 5000);
        stdErr = new StreamBufferer(ps.getErrorStream(), 5000);
    }

    public String getStatusText()
    {
        return "stderr: " + stdErr.getBuffer() + "\nstdout: "
                + stdOut.getBuffer();
    }
}