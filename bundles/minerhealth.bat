
@echo off

REM ############################################################################

REM Folder where Claymore generates logs
SET logDir=Clay98

REM Provide a unique name for each machine here.
REM By default machine's hostname is used.
SET machine=%COMPUTERNAME%

REM Enable or disable active fan control according to temperature
SET fanControl=1

REM Enable or disable remote restart (from web GUI)
SET remoteReboot=1

REM Report interval (seconds)
SET reportInterval=20

REM ############################################################################

REM Running
java -jar minerhealth.jar %machine% %logDir% %fanControl% %remoteReboot% %reportInterval%