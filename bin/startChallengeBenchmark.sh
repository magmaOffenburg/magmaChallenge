#*******************************************************************************
# Copyright 2008, 2011 Hochschule Offenburg
# Klaus Dorer, Mathias Ehret, Stefan Glaser, Thomas Huber, Fabian Korak,
# Simon Raffeiner, Srinivasa Ragavan, Thomas Rinklin,
# Joachim Schilling, Ingo Schindler, Rajit Shahi, Bjoern Weiler
#
# This file is part of magmaOffenburg.
#
# magmaOffenburg is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# magmaOffenburg is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with magmaOffenburg. If not, see <http://www.gnu.org/licenses/>.
#*******************************************************************************
#!/bin/bash
###########################################
# Starts the magma challenge benchmark tool
# example: ./startChallengeBenchmark.sh
###########################################

# for Ubuntu
CLSEP=:
# for Cygwin
#CLSEP=\;

CLSPTH=build/classes
CLSPTH=${CLSPTH}${CLSEP}build/config
CLSPTH=${CLSPTH}${CLSEP}lib/commons-math3-3.3.jar
CLSPTH=${CLSPTH}${CLSEP}lib/magmaMonitor.jar
CLSPTH=${CLSPTH}${CLSEP}lib/SAProxy.jar
# only needed if not in build environment
CLSPTH=${CLSPTH}${CLSEP}lib/magmaChallenge.jar
CLSPTH=${CLSPTH}${CLSEP}config

DEFAULT_PATH=/host/Data/Programmierung/Magma/magmaChallenge/config/runChallenge/
ROBO_VIZ_SERVER=localhost

cd ..
# echo $CLSPTH
java -cp $CLSPTH magma.tools.benchmark.controller.BenchmarkController --defaultPath=$DEFAULT_PATH --roboVizServer=$ROBO_VIZ_SERVER
cd bin
