#!/usr/bin/env bash

cd selenium-safari
java -cp selenium-server-standalone-3.14.0.jar org.openqa.grid.selenium.GridLauncherV3 -debug \
     -role node -nodeConfig node.json > output.log 2>&1 &
