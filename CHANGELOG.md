Version 2.7 (RoboCup 2018)
---------------------
- added Goalie challenge for RoboCup 2018

Version 2.6
---------------------
- added the Passing and Scoring challenge from RoboCup 2017 ([#9](https://github.com/magmaOffenburg/magmaChallenge/pull/9))
- added a headless mode / `startCLI.sh` ([#10](https://github.com/magmaOffenburg/magmaChallenge/issues/10))
- added a `--startScriptFolder` argument
- added a `--challenge` argument
- added support for paths starting with `~` ([#11](https://github.com/magmaOffenburg/magmaChallenge/issues/11))
- moved `bin/startChallengeBenchmark` to `startGUI.sh`

Version 2.5 (RoboCup 2016)
---------------------
- added Gazebo run challenge for RoboCup 2016
- changed "Open Start Script" to "Open Start Script Folder"
- added a `--help` argument
- decreased the window height to 200

Version 2.4
---------------------
- added keep away challenge for RoboCup 2016
- removed UI for player start script file name (now assumed to be `startChallengePlayer.sh`)

Version 2.3 (RoboCup 2015)
---------------------
- disabled beam noise

Version 2.2 (German Open 2015)
---------------------
- only perform single run if test is selected
- added RoboViz visualization for the penalty circle and the target position
- added a `--roboVizServer` argument
- couple of GUI improvements
- distance column shows last distance instead of average
- added separated timeout for ball to leave circle
- fixed a problem that kill script has not been called
- waiting a bit longer before starting play on
- added readme to GitHub page

Version 2.1
---------------------
- made challenge selectable in GUI
- added KickChallenge Referee
- fixed a bug that open competition did not update table
- stopping benchmark if ball has left circle and stopped (0.001)

Version 2.0
---------------------
- added kick challenge for RoboCup 2015

Version 1.4 (RoboCup 2014)
---------------------
- using client beaming instead of monitor beaming to beam and hover an agent the first 200 ms at its drop height to allow more reliable joint control (Patrick)
- fixed a problem that multiple force resistance sensors were not properly detected (Patrick)
- added a stop condition that stops the benchmark at 14.5 m to prevent agents running into opponent goal (Patrick)

Version 1.3
---------------------
- added team individual drop height to be specified in a competition file.
- doubled time until challenge starts from 2 to 4 seconds.
- dropping ball and move it out of way into opponent half

Version 1.2 (German Open 2014)
---------------------
- added support for running teams of multiple users (still manual changes necessary)
- added the number of runs done so far
- added counters for single support phase and double support phase in addition to no support phase 
- adjusted a couple of timeouts
- fixed a bug that run challenge did not stop anymore
- calling a script to explicitly stop clients

Version 1.1
---------------------
- off ground started to count too early, fixed
- check for if start line was never crossed has been misplaced
- long error messages have been unreadable, using Scroll Pane and TextArea
- added support for a verbose mode that prints all messages of the proxy
- added a button to open a single start script

Version 1.0
---------------------
Initial version for the run challenge benchmark tool 