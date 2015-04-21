#! /bin/bash

REF=/usr/share/jenkins/ref/plugins
mkdir -p $REF

while read spec; do
    plugin=(${spec//:/ });
    [[ ${plugin[0]} =~ ^# ]] && continue
    [[ ${plugin[0]} =~ ^\s*$ ]] && continue
    echo "Getting plug-in ${plugin[0]}-${plugin[1]}"
    curl -L ${JENKINS_UC}/download/plugins/${plugin[0]}/${plugin[1]}/${plugin[0]}.hpi -o $REF/${plugin[0]}.hpi;
done  < $1
