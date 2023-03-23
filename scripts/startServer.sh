#!/bin/bash

if [[ $# -ne 3 ]]
then
    echo "Usage: $0 <agent-port> <monitor-port> <script-path>"
    exit 1
fi

#rcssserver3d --agent-port 3100 --server-port 3200 --script-path config/rcssserver3d.rb > /dev/null 2>&1
rcssserver3d --agent-port $1 --server-port $2 --script-path $3 > /dev/null 2>&1
