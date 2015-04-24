# magmaChallenge
Challenge Benchmark Tool for the [RoboCup 3D Soccer Simulation League](http://en.wikipedia.org/wiki/RoboCup_3D_Soccer_Simulation_League)

## Installation

- Clone the repository: `git clone https://github.com/klausdorer/magmaChallenge`.
- Make sure you have Java installed and available on your command line.
- Make sure you have simspark and rcsserver3d installed and the command `rcssserver3d` is available on your command line.

## Preparation
Before you can use the challenge tool, you have to create a `.sh` start script for your team that starts a single player.

The script receives the following arguments:

1. server IP
2. server port
3. start x position (float)
4. start y position (float)
5. name of the challenge

Beams are not permitted (commands including a beam will be entirely ignored by the server).

## Usage

```bash
cd magmaChallenge/bin
./startChallengeBenchmark.sh
```

The `Open Start Script...` button allows you to open your start script and test your team. Alternatively you can manually edit the path and name of the start script in the table.

The `Open Competition...` button allows you to load a file with the configuration of the teams for a competition (`teamName;startScriptDirectory;startScriptName;playerDropHeight`, see [/examples](/examples)).  The initial path of the file dialog can be passed to `startChallengeBenchmark.sh` as an argument.

The `Test` button will try to start each team once and run it for 3 seconds to check if the team works. If it does not, a click on the status column of that team should provide an error message.

The `Competition` button will run <Avg out runs> trials for each team in the table and each will be <Runtime> seconds long. The average result is displayed in the table. Note that run time can only be set for the run challenge.

The `Stop` button stops a test or competition after the current run is finished.

The `Stop Server` button can be used to kill the rcssserver in case it was not done by the tool (in some error cases still) or if it hangs. The current trial is repeated then if competition is still running. It stops ALL servers on the localhost.

Internally the tool uses the monitor protocol to get world model information.

## Run Challenge

### Requirements

- The start player script has to start a player that runs forwards as fast as possible when it receives "RunChallenge" as the challenge name argument.
- The walk has to be 'human-like' (no strange crawling or saltos or similar, judged by a human).

### Evaluation

The score of the team is evaluated as the sum of 
- the speed (in m/s)
- the relative amount of time both legs are off the ground

The time starts when the player crosses the start line or 4 seconds after the player was beamed (by the automated referee) to its start position (-13.5, 0), which is 0.5m behind the start line (whichever happens first). The speed is the distance in x-direction divided by the time. If a player falls, the current run stops and 2 meters are subtracted from the player's current position as a penalty (however, the distance can never be smaller than 0). In that case the runs finishes early, but the full run time is used for the speed calculation. A player is considered to have fallen down if the z coordinate of the up vector is less than 0.6 or the z coordinate of the torso center is below 0.25.

The relative amount of time both legs are off the ground is determined by counting the cycles in which all (both) force sensors have a length of less than 0.01, divided by the number of total cycles (within the run time).

The final sum is rounded to 3 digits. Equal scores will result in the same place.

## Kick Challenge

### Requirements

- The start player script has to start a player that kicks the ball towards (0, 0) when it receives "KickChallenge" as the challenge name argument.

### Evaluation

TODO
