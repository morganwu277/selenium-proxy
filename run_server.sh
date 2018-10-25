#!/usr/bin/env bash

source ~/.bash_profile
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $DIR

java -cp selenium-server-standalone-3.14.0.jar:selenium-proxy-1.0-SNAPSHOT.jar org.openqa.grid.selenium.GridLauncherV3 \
    -role hub -hubConfig server.json -debug
