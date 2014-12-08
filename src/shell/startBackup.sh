#!/bin/bash

# Determine location of this script
script="$(readlink -f ${BASH_SOURCE[0]})"
scriptLocation="$(dirname ${script})"

# Load configuration
. "$scriptLocation/config.cfg"

# Call the 'startBackup' web service and extract the PIDs from the resulting JSON
pidsEval="curl '$applicationUrl/service/startBackup' | jq '.pids[]'"
pids=$(eval ${pidsEval})

# Start the backup for each PID
for pid in ${pids}
do
	# Remove the quotes around the PID
	pid="${pid%\"}"
	pid="${pid#\"}"

	# Start backup for the current PID
	curl --data "pid=$pid&status=$statusBackupRunning&failure=false" "$applicationUrl/service/status"

	# ... ... ...
done
