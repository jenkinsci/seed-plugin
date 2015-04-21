# Jenkins image definition
# Inspired by the official image at https://github.com/cloudbees/jenkins-ci.org-docker
#
# [DCO] After several attempts, I've decided to fork the official image (which
# is simple enough), because it didn't support overriding plugins and
# initial configurations.

# Base Java image

FROM java:openjdk-8u40-jdk

# Basic tools

RUN apt-get update && apt-get install -y wget git curl zip && rm -rf /var/lib/apt/lists/*

# Home folder

ENV JENKINS_HOME /var/lib/jenkins

# Jenkins version

ENV JENKINS_VERSION 1.596.2

# Jenkins is ran with user `jenkins`, uid = 1000
# If you bind mount a volume from host/vloume from a data container,
# ensure you use same uid or assign the host directory to the `docker` user

RUN useradd -d "$JENKINS_HOME" -u 1000 -m -s /bin/bash jenkins

# Jenkins home directoy is a volume, so configuration and build history
# can be persisted and survive image upgrades
VOLUME $JENKINS_HOME

# Jenkins reference directory

RUN mkdir -p /usr/share/jenkins/ref

# TODO Add the Seed plug-in

# Update web site
ENV JENKINS_UC https://updates.jenkins-ci.org

# Installing the plug-ins

COPY plugins.sh /usr/share/jenkins/plugins.sh
COPY plugins.txt /usr/share/jenkins/plugins.txt
RUN /usr/share/jenkins/plugins.sh /usr/share/jenkins/plugins.txt

# Download of the Jenkins application
# Could use ADD but this one does not check Last-Modified header
# see https://github.com/docker/docker/issues/8331
# TODO Use the 1.609 version
RUN curl -L http://mirrors.jenkins-ci.org/war-stable/$JENKINS_VERSION/jenkins.war -o /usr/share/jenkins/jenkins.war

# Initial generator jobs

COPY job-dsl-core.jar /usr/share/jenkins/ref/init.groovy.d/
COPY seed-job-init.groovy /usr/share/jenkins/ref/init.groovy.d/
COPY seed-job-dsl.groovy /usr/share/jenkins/ref/dsl/

# For main web interface:
EXPOSE 8080

# Will be used by attached slave agents:
EXPOSE 50000

# Java setup

ENV JAVA_OPTS -Xmx2048m

# Logging directory

RUN mkdir -p /var/log/jenkins

# Rights on the Jenkins home

RUN chown -R jenkins /usr/share/jenkins
RUN chown -R jenkins ${JENKINS_HOME}
RUN chown -R jenkins /var/log/jenkins

# Running as Jenkins user
USER jenkins

# Starting script

COPY jenkins.sh /usr/local/bin/jenkins.sh
ENTRYPOINT ["/usr/local/bin/jenkins.sh", "--logfile=/var/log/jenkins/jenkins.log"]
