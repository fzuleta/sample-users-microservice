#!/usr/bin/env bash
# in this file time is set to the same value, only in the ones that need to wait for the DB to be built is longer
myFile="/firstrun.exists"
if [ -e "$myFile" ]; then
    sleep 3

else
    touch "$myFile"
    echo "Allowing some time to start"
    sleep 3
fi

cd /opt

JAVA_OPTS_MEMORY="-Xms256m -Xmx2048m"
JAVA_OPTS_SCRIPT="-Djna.nosys=true -XX:+HeapDumpOnOutOfMemoryError -XX:MaxDirectMemorySize=512g -Djava.awt.headless=true -Dfile.encoding=UTF8 -Drhino.opt.level=9"

echo $JAVA_OPTS_MEMORY
echo $JAVA_OPTS_SCRIPT

java -jar \
    $JAVA_OPTS_MEMORY \
    $JAVA_OPTS_SCRIPT \
    main.jar
