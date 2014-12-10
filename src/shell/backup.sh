#!/bin/bash

# Determine location of this script
script="$(readlink -f ${BASH_SOURCE[0]})"
scriptLocation="$(dirname ${script})"

# Load configuration
. "$scriptLocation/config.cfg"

# Load the PID
pid=$1

# Start backup for the PID
curl --data "pid=$pid&status=$statusBackupRunning&failure=false" "$applicationUrl/service/status"

backupLocation="$ingestLocation/$pid"
chown -R root "$backupLocation"
# Do the backup ...
success=$? # What is the exit code of the backup?
chown -R "$owner" "$backupLocation"

# Update the status using the 'status' web service
if [ ${success} -eq 0 ] # Did backup fail or succeed? ...
then
	curl --data "pid=$pid&status=$statusBackupFinished&failure=false" "$applicationUrl/service/status"
else
	curl --data "pid=$pid&status=$statusBackupFinished&failure=true" "$applicationUrl/service/status"
fi
