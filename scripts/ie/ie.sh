#!/bin/bash

source ~/.bash_profile
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $DIR


hub_ip="192.168.34.1"
private_ip=`cat Vagrantfile | grep private_network |awk '{print $4}'|sed -e 's/"//g'`

cp selenium-ie/node.json.tmpl selenium-ie/node.json
sed -e "s/<private_ip>/$private_ip/g" selenium-ie/node.json > tmp.json
mv tmp.json selenium-ie/node.json
sed -e "s/<hub_ip>/$hub_ip/g" selenium-ie/node.json > tmp.json
mv tmp.json selenium-ie/node.json


if [[ "$1" == "start" ]]; then
    echo "start... ie"
    vagrant up --provision > command_run.txt 2>&1 &
fi

if [[ "$1" == "stop" ]]; then
    echo "stop... ie"
    vagrant halt > command_run.txt 2>&1 &
fi

if [[ "$1" == "output" ]]; then
    echo "getting command output..."
    cat command_run.txt
fi



