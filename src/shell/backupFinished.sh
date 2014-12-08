#!/bin/bash

# Determine location of this script
script="$(readlink -f ${BASH_SOURCE[0]})"
scriptLocation="$(dirname ${script})"

# Load configuration
. "$scriptLocation/config.cfg"

# Loop over folders (PIDs) for which a backup is running
# ... ... ...
pids=

# For each PID, check if backup is finished...
for pid in ${pids}
do
	# ... ... ...

	# Update the status using the 'status' web service
	if [ ] # ... ... ...
	then
		curl --data "pid=$pid&status=$statusBackupFinished&failure=false" "$applicationUrl/service/status"
	else
		curl --data "pid=$pid&status=$statusBackupFinished&failure=true" "$applicationUrl/service/status"
	fi
done