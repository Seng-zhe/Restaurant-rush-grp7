#!/usr/bin/env bash
# Builds and runs Restaurant Rush. Pass a demo file to replay it:
#   ./run.sh demo/stage1-victory.txt
set -e
cd "$(dirname "$0")"
rm -rf out
javac -d out $(find src -name "*.java")
if [ -n "$1" ]; then
  java -cp out restaurantrush.Main < "$1"
else
  java -cp out restaurantrush.Main
fi
