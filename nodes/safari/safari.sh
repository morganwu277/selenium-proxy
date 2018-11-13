#!/bin/bash

source ~/.bash_profile
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $DIR

. ./../common.sh

private_ip="127.0.0.1"
hub_ip="127.0.0.1"
browserName="safari"

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
    # ps -ef|grep 'safaridriver' |grep -v grep |awk '{print $2}'|xargs kill
    # ps -ef|grep 'Safari --automation' |grep -v grep |awk '{print $2}'|xargs kill
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
    # TODO: checking if node.json contains this safari?
    if ps -ef | grep node.json | grep -v -q grep; then
      echo "up"
    # elif ps -ef | grep 'safaridriver' | grep -v -q grep; then
    #   echo "up"
    # elif ps -ef | grep 'Safari --automation' | grep -v -q grep; then
    #   echo "up"
    else
      echo "down"
    fi
fi

