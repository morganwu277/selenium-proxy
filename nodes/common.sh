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
