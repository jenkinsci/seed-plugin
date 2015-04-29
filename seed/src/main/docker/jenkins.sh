#! /bin/bash

# Copy of plugins
echo "Copy of plugins in ${JENKINS_HOME}/plugins"
mkdir -p ${JENKINS_HOME}/plugins
rm -rf ${JENKINS_HOME}/plugins/*
find /usr/share/jenkins/ref/plugins -type f -exec cp -fv {} ${JENKINS_HOME}/plugins \;

# Copy of DSL scripts
echo "Copy of DSL scripts in ${JENKINS_HOME}/dsl"
mkdir -p ${JENKINS_HOME}/dsl
rm -rf ${JENKINS_HOME}/dsl/*
find /usr/share/jenkins/ref/dsl -type f -exec cp -fv {} ${JENKINS_HOME}/dsl \;

# Copy of initialisation scripts
echo "Copy of initialisation scripts in ${JENKINS_HOME}/init.groovy.d"
rm -rf ${JENKINS_HOME}/init.groovy.d
mkdir -p ${JENKINS_HOME}/init.groovy.d
find /usr/share/jenkins/ref/init.groovy.d -type f -exec cp -fv {} ${JENKINS_HOME}/init.groovy.d \;

# if `docker run` first argument start with `--` the user is passing jenkins launcher arguments
if [[ $# -lt 1 ]] || [[ "$1" == "--"* ]]; then
   exec java $JAVA_OPTS -jar /usr/share/jenkins/jenkins.war $JENKINS_OPTS "$@"
fi

# As argument is not jenkins, assume user want to run his own process, for sample a `bash` shell to explore this image
exec "$@"
