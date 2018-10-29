#!/bin/bash

source ~/.bash_profile
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $DIR

. ./../common.sh

private_ip="192.168.34.24"
browserName="firefox"

cp ./../selenium/selenium-server-standalone-3.14.0.jar selenium-$browserName/

# sub variables
cp selenium-$browserName/node.json.tmpl selenium-$browserName/node.json
sub_in_file selenium-$browserName/node.json "<private_ip>" "$private_ip"
sub_in_file selenium-$browserName/node.json "<hub_ip>" "$hub_ip"


if [[ "$1" == "start" ]]; then
    echo "start... $browserName"
    vagrant up --provision > command_run.txt 2>&1 &
fi

if [[ "$1" == "stop" ]]; then
    echo "stop... $browserName"
    vagrant halt > command_run.txt 2>&1 &
fi

if [[ "$1" == "output" ]]; then
    echo "getting command output..."
    cat command_run.txt
fi



