#! /bin/bash

# Copy of plugins
mkdir -p ${JENKINS_HOME}/plugins
find /usr/share/jenkins/ref/plugins -type f -exec cp -f {} ${JENKINS_HOME}/plugins \;

# Copy of DSL scripts
mkdir -p ${JENKINS_HOME}/dsl
find /usr/share/jenkins/ref/dsl -type f -exec cp -f {} ${JENKINS_HOME}/dsl \;

# Copy of initialisation scripts
rm -rf ${JENKINS_HOME}/init.groovy.d
mkdir -p ${JENKINS_HOME}/init.groovy.d
find /usr/share/jenkins/ref/init.groovy.d -type f -exec cp -f {} ${JENKINS_HOME}/init.groovy.d \;

# if `docker run` first argument start with `--` the user is passing jenkins launcher arguments
if [[ $# -lt 1 ]] || [[ "$1" == "--"* ]]; then
   exec java $JAVA_OPTS -jar /usr/share/jenkins/jenkins.war $JENKINS_OPTS "$@"
fi

# As argument is not jenkins, assume user want to run his own process, for sample a `bash` shell to explore this image
exec "$@"
