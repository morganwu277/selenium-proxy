#!/bin/bash

source ~/.bash_profile
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $DIR

. ./../common.sh

private_ip="192.168.34.22"
browserName="edge"

cp ./../selenium/selenium-server-standalone-3.14.0.jar selenium-$browserName/

# sub variables
[[ -f Vagrantfile.tmpl ]] && cp Vagrantfile.tmpl Vagrantfile
[[ -f Vagrantfile.tmpl ]] && sub_in_file Vagrantfile "<private_ip>" "$private_ip"
cp selenium-$browserName/node.json.tmpl selenium-$browserName/node.json
sub_in_file selenium-$browserName/node.json "<private_ip>" "$private_ip"
sub_in_file selenium-$browserName/node.json "<hub_ip>" "$hub_ip"


if [[ "$1" == "start" ]]; then
    echo "start... $browserName"
    vagrant_up > command_run.txt 2>&1 &
fi

if [[ "$1" == "stop" ]]; then
    echo "stop... $browserName"
    vagrant_down > command_run.txt 2>&1 &
fi

if [[ "$1" == "output" ]]; then
    echo "getting command output..."
    cat command_run.txt
fi

if [[ "$1" == "log" ]]; then
    echo "getting log..."
    cat selenium-*/output.log
fi

if [[ "$1" == "status" ]]; then
    echo "checking status..."
    if ps -ef | grep edge_default | grep -v -q grep; then
      echo "up"
    else
      echo "down"
    fi
fi
