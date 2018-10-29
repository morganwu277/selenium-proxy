#!/usr/bin/env bash

export hub_ip="192.168.34.1"

function sub_in_file() {
  file=$1
  old=$2
  new=$3
  sed -e "s/$old/$new/g" $1 > tmp.json
  mv tmp.json $1
}

function vagrant_up() {
  if vagrant snapshot list |grep init_state > /dev/null; then
    vagrant snapshot restore --provision init_state
  else
    vagrant up --provision
  fi

}

function vagrant_down() {
  vagrant halt
}

# start selenium node in the background
function bg_start_selenium_node() {
  java -cp selenium-server-standalone-3.14.0.jar org.openqa.grid.selenium.GridLauncherV3 -role node -nodeConfig node.json > output.log 2>&1 &
}

# stop selenium node
function bg_stop_selenium_node() {
  pid=`ps -ef|grep GridLauncherV3 |grep node|awk '{print $2}'`
  kill $pid
}

