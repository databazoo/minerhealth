#!/bin/sh

################################################################################

# Your client ID
clientID=c107de54-40ef-43a4-99e3-acb5828c18ad

# Provide a unique name for each machine here.
# By default machine's hostname is used.
machine=`hostname`

# Folder where Claymore generates logs
logDir=./Clay98/

# Enable or disable active fan control according to temperature (if supported)
fanControl=1

# Enable or disable remote restart (from web GUI)
remoteReboot=1

# Report interval (seconds)
reportInterval=20

# If temperature or performance limit is breached, how many times should we
# recheck before rebooting the machine?
# Example: reportInterval=20 and recheckAttemptsLimit=6 make it 120 seconds.
recheckAttemptsLimit=6

################################################################################

# Running
java -jar "minerhealth.jar" $clientID $machine $logDir $fanControl $remoteReboot $reportInterval $recheckAttemptsLimit
