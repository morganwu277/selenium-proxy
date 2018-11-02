#!/bin/bash

source ~/.bash_profile
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $DIR

. ./../common.sh

private_ip="192.168.34.1"
browserName="chrome"

cp ./../selenium/selenium-server-standalone-3.14.0.jar selenium-$browserName/

# sub variables
cp selenium-$browserName/node.json.tmpl selenium-$browserName/node.json
sub_in_file selenium-$browserName/node.json "<private_ip>" "$private_ip"
sub_in_file selenium-$browserName/node.json "<hub_ip>" "$hub_ip"


if [[ "$1" == "start" ]]; then
    echo "start... $browserName"
    cd selenium-$browserName
    bg_start_selenium_node
    cd -
fi

if [[ "$1" == "stop" ]]; then
    echo "stop... $browserName"
    bg_stop_selenium_node
fi

if [[ "$1" == "output" ]]; then
    echo "getting log..."
    cat selenium-*/output.log
fi

if [[ "$1" == "log" ]]; then
    echo "getting log..."
    cat selenium-*/output.log
fi


if [[ "$1" == "status" ]]; then
    echo "checking status..."
    # TODO: checking if node.json contains this chrome?
    if ps -ef | grep node.json | grep -v -q grep; then
      echo "up"
    else
      echo "down"
    fi
fi


