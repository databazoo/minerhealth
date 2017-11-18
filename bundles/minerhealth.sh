#!/bin/sh

################################################################################

# Folder where Claymore generates logs
logDir=./Clay98/

# Provide a unique name for each machine here.
# By default machine's hostname is used.
machine=`hostname`

# Enable or disable active fan control according to temperature
fanControl=1

# Enable or disable remote restart (from web GUI)
remoteReboot=1

# Report interval (seconds)
reportInterval=20

################################################################################

# Running
java -jar "minerhealth.jar" $machine $logDir $fanControl $remoteReboot $reportInterval
