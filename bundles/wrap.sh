#!/bin/bash

if [ "$1" != "" ]; then

    # work folder
    cp -r ./bundles ./target
    cd ./target/bundles

    # copy latest version into work folder
    cp ../minerhealth-*.jar ./minerhealth.jar
    
    chmod 0755 ./minerhealth.jar

    # pack
    zip -9 -r "../minerhealth.v$1.zip" "./minerhealth.jar" "./minerhealth.sh" "./minerhealth.bat" README.TXT

fi
