#!/bin/bash

# Determine location of this script
script="$(readlink -f ${BASH_SOURCE[0]})"
scriptLocation="$(dirname ${script})"

# Load configuration
. "$scriptLocation/config.cfg"

# Call the 'folders' web service and extract the PIDs from the resulting JSON
pidsEval="curl '$applicationUrl/service/folders' | jq .pids[]"
pids=$(eval ${pidsEval})

# Create a folder for each PID
for pid in ${pids}
do
	# Remove the quotes around the PID
	pid="${pid%\"}"
	pid="${pid#\"}"

	# Create a folder for the PID
	mkdir -p "$ingestLocation/$pid"

	# Update the status using the 'status' web service
	if [ -d "$ingestLocation/$pid" ]
	then
		curl --data "pid=$pid&status=$statusFolderCreated&failure=false" "$applicationUrl/service/status"
	else
		curl --data "pid=$pid&status=$statusFolderCreated&failure=true" "$applicationUrl/service/status"
	fi
done
