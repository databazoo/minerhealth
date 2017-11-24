
@echo off

REM ############################################################################

REM Your client ID
SET clientID=c107de54-40ef-43a4-99e3-acb5828c18ad

REM Provide a unique name for each machine here.
REM By default machine's hostname is used.
SET machine=%COMPUTERNAME%

REM Folder where Claymore generates logs
SET logDir=Clay98

REM Enable or disable active fan control according to temperature (if supported)
SET fanControl=1

REM Enable or disable remote restart (from web GUI)
SET remoteReboot=1

REM Report interval (seconds)
SET reportInterval=20

REM ############################################################################

REM Running
java -jar minerhealth.jar %clientID% %machine% %logDir% %fanControl% %remoteReboot% %reportInterval%
