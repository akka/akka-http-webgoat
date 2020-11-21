#!/usr/bin/env bash

# this is a copy-and-paste-job from the play-webgoat repo

set -e
set -v

# let's just assume that ~/.lightbend/license is already in place
# on CI, it's there because the scala-fortify job left it there.
# bit janky to have the dependency between the jobs, but meh.

if [[ $(uname -s) = "Darwin" ]]; then
   SCA_DIR=/Applications/Fortify
   SBT=/usr/local/bin/sbt
else
   SCA_DIR=/home/ubuntu/Fortify
   SBT=/usr/bin/sbt
fi
SOURCEANALYZER=$SCA_DIR/Fortify_SCA_and_Apps_20.2.0/bin/sourceanalyzer
$SOURCEANALYZER -b akka-http-webgoat -clean

java -version

export JAVA_OPTS=-Dsbt.log.noformat=true
$SBT clean compile

rm -f target/vulnerabilities-actual.txt
$SOURCEANALYZER \
  -b akka-http-webgoat \
  -logfile target/scan.log \
  -scan \
  | tail -n +4 > target/vulnerabilities-actual.txt

cat target/scan.log

diff -u vulnerabilities.txt target/vulnerabilities-actual.txt
echo SUCCESS
