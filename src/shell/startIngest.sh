#!/bin/bash

# Determine location of this script
script="$(readlink -f ${BASH_SOURCE[0]})"
scriptLocation="$(dirname ${script})"

# Load configuration
. "$scriptLocation/config.cfg"

# Call the 'startIngest' web service and extract the PIDs from the resulting JSON
pidsEval="curl '$applicationUrl/service/startIngest' | jq '.pids[]'"
pids=$(eval ${pidsEval})

# Create an empty ingest.txt file for each PID to trigger the ingest
for pid in ${pids}
do
	# Remove the quotes around the PID
	pid="${pid%\"}"
	pid="${pid#\"}"

	# Create an empty ingest.txt file for the PID
	touch "$ingestLocation/$pid/ingest.txt"

	# Update the status using the 'status' web service
	if [ -f "$ingestLocation/$pid/ingest.txt" ]
	then
		curl --data "pid=$pid&status=$statusUploadingToPermanentStorage&failure=false" "$applicationUrl/service/status"
	else
		curl --data "pid=$pid&status=$statusUploadingToPermanentStorage&failure=true" "$applicationUrl/service/status"
	fi
done
