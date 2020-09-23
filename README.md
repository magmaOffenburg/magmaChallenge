# magmaChallenge

![Build Status](https://github.com/magmaOffenburg/magmaChallenge/workflows/Build/badge.svg)

Challenge Benchmark Tool for the [RoboCup 3D Soccer Simulation League](http://wiki.robocup.org/wiki/Soccer_Simulation_League) created by the [magmaOffenburg team](http://robocup.hs-offenburg.de/).

![](screenshots/userInterface.png)

## Contents

- [Installation](#installation)
- [Preparation](#preparation)
- [Usage](#usage)
- [Run Challenge](#run-challenge)
- [Kick Challenge](#kick-challenge)
- [Keep Away Challenge](#keep-away-challenge)
- [Passing Challenge](#passing-challenge)
- [Goalie Challenge](#goalie-challenge)

## Installation

- Clone the repository: `git clone https://github.com/magmaOffenburg/magmaChallenge`.
- Make sure you have Java 8 installed and available on your command line.
- Make sure you have [simspark and rcssserver3d](http://simspark.sourceforge.net/wiki/index.php/Main_Page) installed and the command `rcssserver3d` is available on your command line.

For contributions, please set up `clang-format` as described [here](https://github.com/hsoautonomy/formatting). 

## Preparation
Before you can use the challenge tool, you have to create a `startChallengePlayer.sh` start script for your team that starts a single player.

The script receives the following arguments:

1. server IP
2. server port
3. start x position (float)
4. start y position (float)
5. name of the challenge

Beams are not permitted (commands including a beam will be entirely ignored by the server). Usage of the start position is optional, it can be useful to initialize localization filters.

Additionally, there needs to be a `kill.sh` script that kills the agent process in the same folder.

## Usage

### Start script

```bash
bash startGUI.sh
```

The script can be used to configure the following arguments that are passed to `BenchmarkController`:
- `--defaultPath=`: the path used for the file dialogs.
- `--roboVizServer=`: the IP to connect to for RoboViz drawings (only used in the Kick Challenge at the moment).

### User Interface

The `Open Start Script...` button allows you to open your start script and test your team. Alternatively you can manually edit the path and name of the start script in the table.

The `Open Competition...` button allows you to load a file with the configuration of the teams for a competition (`teamName;startScriptDirectory;startScriptName;playerDropHeight`, see [/examples](/examples)).

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

The start player script has to start a player that kicks the ball towards (0, 0) when it receives "KickChallenge" as the challenge name argument.

![](screenshots/kickChallenge.png)

### Evaluation

The score of the team is evaluated as the average of the distances to the target position (0, 0).

The accuracy is measured over ten kicks. Each kick, the player starts from a random position in his own half within 45 degrees seen from (0, 0). The start positions of the player are of ever increasing distance (3-12 meters) to the destination spot. They are the same for all teams.

The time starts when the player gets closer than 0.5m to the ball or 3 seconds after the player was beamed (by the automated referee) to its start position roughly 1m behind the ball (whichever happens first).

An attempt is over, if 
- the player leaves a 2m circle around the initial ball position (which also results in a score penalty of 5m),
- the ball leaves that circle and stops,
- or the ball does not leave the circle 5 seconds after start of the run.

The final sum is rounded to 3 digits. Equal scores will result in the same place. 

## Keep Away Challenge

### Requirements

The keep away challenge requires the `Keepaway` variable in rcssserver3d's `naosoccersim.rb` to be set to `true`.

The start script has to start three players when it receives "KeepAwayChallenge" as the challenge name argument. Beaming is allowed in this challenge, so the "start x position" and "start y position" arguments the script receives can be ignored.

![](screenshots/keepAwayChallenge.png)

### Evaluation

An attempt ends if:

- the ball leaves the keep away area
- the opponent player touches the ball

These conditions are checked by the server (has to be run in keep away mode), which switches the play mode to `GameOver` if either is true.

The keep away area's size decreases over time and is calculated like this:

```java
float time = worldModel.getTime() / 60f;
float widthReduction = WIDTH_REDUCTION_RATE / 2.0f * time;
float lengthReduction = LENGTH_REDUCTION_RATE / 2.0f * time;

float areaMinX = AREA_CENTER_X - AREA_LENGTH / 2.0f + lengthReduction;
float areaMaxX = AREA_CENTER_X + AREA_LENGTH / 2.0f - lengthReduction;
float areaMinY = AREA_CENTER_Y - AREA_WIDTH / 2.0f + widthReduction;
float areaMaxY = AREA_CENTER_Y + AREA_WIDTH / 2.0f - widthReduction;
```

with the following constants:

```java
AREA_CENTER_X = 0;
AREA_CENTER_Y = 0;
AREA_WIDTH = 20;
AREA_LENGTH = 20;
WIDTH_REDUCTION_RATE = 4;
LENGTH_REDUCTION_RATE = 4;
```

The score is determined by the server time at the moment the play mode switches to `GameOver`.

The opponent player is a magmaOffenburg agent that runs to the ball at full speed.

This challenge is inspired by [UT Austin Villa's submission](https://www.youtube.com/watch?v=65t9_YRsUMc) to the free challenge for RoboCup 2015 in Hefei. 

## Passing Challenge

### Requirements

The passing challenge requires the `StartAnyFieldPosition` variable in rcssserver3d's `naosoccersim.rb` to be set to `true`.

The start script has to start four players when it receives "PassingChallenge" as the challenge name argument. Beaming is allowed in this challenge, so the "start x position" and "start y position" arguments the script receives can be ignored. For this challenge, the agents can be beamed directly in the opponent's field. The agents must start with a 3 meter distance, in the x axis, from each other.

### Evaluation

If the initial position of the agents does not comply with the rules, the team is awarded the trial score of 85. The trial ends if: a goal is scored; the ball leaves the field; or 80 seconds have passed. For each distinct agent touching the ball, the score is reduced by one point. An agent is considered distinct from another when touching the ball if the ball has travelled at least 2.5 meters after being kicked by the previous agent. If a goal is scored, the score is reduced by one point. If the goal is scored after the ball has been kicked by the four players, the score is the time (in seconds) from the start of the trial until the scoring event. There are 4 trials for each team and the average of the best 3 trial scores is considered as the final score. The team with the lowest final score wins the challenge.


## Goalie Challenge

### Requirements

The start player script has to start a player that will act as the goalie without leaving the penalty area when it receives "GoalieChallenge" as the challenge name argument.
Please check [Preparation](#preparation) section for the meaning of the other script parameters.

### Evaluation

Teams can participate in this challenge using their normal goalie or with a goalie with very few adaptations.
The goalie challenge tool may also be used as a developing tool to test goalie behavior.

Evaluation rules:

- the final score is based on the percentage of unsuccessful goal attempts, i.e. the percentage of failed goal attempts; 
- the winner will be the goalie able to achieve the higher percentage of unsuccessful goal attempts;
- the goalie cannot get out of the penalty area or a goal is considered;
- if the top teams draw, the challenge will be repeated for these teams with a different seed a maximum of 3 times. If the draw is maintained, several winners will be announced;
- the evaluation will be based on 12 shots with different targets on the goal, different initial speeds, and different origins on the field;
- the sequence of shots is randomly selected from a large list of possible kicks (targeting the goal);
- the random sequence is the same for all teams (for the same seed);
- during the competition, each team may decide on one of the digits for the random seed;
- shots are performed automatically using the magma challenge tool;
