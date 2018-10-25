#!/usr/bin/env bash

OUT_DIR="./out"
mvn clean package
rm -rf $OUT_DIR
mkdir $OUT_DIR
cp -r server.json \
   run_server.sh \
   nodes \
   dependencies/selenium-*.jar \
   target/selenium-*.jar \
   $OUT_DIR

cd $OUT_DIR
find . |grep .vagrant |xargs rm -rf
find . |grep node.json$ | xargs rm -rf
find . |grep output.log$ | xargs rm -rf
