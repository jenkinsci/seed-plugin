Seed Jenkins plug-in
====================

The Seed project aims to help automating the generation of pipelines
for branches of a project in Jenkins.

Its behaviour is illustrated by the diagram below:

```
seed*
project/
   project-seed*
   branch-x/
      branch-x-seed*
      (branch-x-pipeline-jobs*)
```

* having an initial _Seed_ job to generate/update _Project seed_ jobs
* each project is hold in its own [folder](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Folders+Plugin)
* the _Project seed_ job is used to generate/update one _Branch seed_ job for a given branch
* each branch is hold in its own [folder](https://wiki.jenkins-ci.org/display/JENKINS/CloudBees+Folders+Plugin)
* the _Branch seed_ job is used to generate/update the pipeline for the branch

Those jobs can be triggered automatically when some _events_ are received on some _connectors_.

Note that the names and contents of the different levels of seed jobs can be configured to suit your needs.

## Events

Four events are currently supported by the Seed plug-in.

### Creation of a branch

Whenever a branch is created in a project, the `branch-creation` event is sent, with as a parameter the name of the branch.

The _project seed_ job is triggered for the given branch.

### Deletion of a branch

Whenever a branch is deleted in a project, the `branch-deletion` event is sent, with as a parameter the name of the branch.

The _branch seed_ job is deleted. The associated pipeline is deleted or not according the configuration of the plug-in.

### Update of Seed files in a branch

Whenever the Seed files are updated in a branch (files under the `/seed` folder), the _branch seed_ job is triggered, in order to regenerate the pipeline, if this configuration parameter is enabled.

### Push on a branch

Whenever some branch files are updated (outside of the Seed files), the branch pipeline start job is triggered.

## Connectors

The way to send events to the Seed Jenkins plug-in can done using _connectors_.

* [HTTP API](doc/connector/HTTP.md)
* [GitHub Web hook](doc/connector/GitHub.md)

## Configuration

The configuration of the plug-in has three levels:

* default configuration
* global configuration
* project specific configuration

Those levels are hierarchical - the project configuration is taken in account first, then the global one and then the default one.

The configuration is given using a YAML file, either entered directly in the Jenkins settings or referred to using an absolute path or a remote URL.

The [format of the configuration](doc/Configuration.md) data is described in other page.
