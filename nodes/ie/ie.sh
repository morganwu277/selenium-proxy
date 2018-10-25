#!/bin/bash

source ~/.bash_profile
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $DIR

function sub_in_file() {
  file=$1
  old=$2
  new=$3
  sed -e "s/$old/$new/g" $1 > tmp.json
  mv tmp.json $1
}

. ./../common.sh
private_ip=`cat Vagrantfile | grep private_network |awk '{print $4}'|sed -e 's/"//g'`

cp selenium-ie/node.json.tmpl selenium-ie/node.json

sub_in_file selenium-ie/node.json "<private_ip>" "$private_ip"
sub_in_file selenium-ie/node.json "<hub_ip>" "$hub_ip"


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



