Plugins
=======

In order to get the list of deployed plug-ins in a Jenkins instance, in order to update the `src/main/docker/jenkins.txt` file,
one can the following Groovy script in the _Manage Jenkins > Script Console_:

```groovy
def plugins = Jenkins.instance.pluginManager.plugins
plugins.each {
    println "${it.shortName}:${it.version}"
}
```

It returns a file like:

```
ant:1.2
translation:1.12
junit:1.5
matrix-project:1.4.1
credentials:1.22
script-security:1.13
ssh-slaves:1.9
scm-api:0.2
antisamy-markup-formatter:1.3
matrix-auth:1.2
subversion:2.5
ldap:1.11
windows-slaves:1.0
pam-auth:1.2
ssh-credentials:1.11
mapdb-api:1.0.6.0
maven-plugin:2.9
cvs:2.12
external-monitor-job:1.4
mailer:1.15
javadoc:1.3
job-dsl:1.32
```

which can be copied directly into `src/main/docker/jenkins.txt`.
