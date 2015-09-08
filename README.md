Seed Jenkins plug-in
====================

The Seed project aims to help automating the generation and management of pipelines
for branches of a project in Jenkins.

The Seed structure can be generated automatically

* seed --> project seed
* project seed --> branch seed for a given branch
* branch seed --> pipeline for this branch

![Generator overview](https://raw.githubusercontent.com/wiki/jenkinsci/seed-plugin/Overview_Generator.png)

The branch pipeline generation is configured using some files on the branch. Different setup are possible according to the level of isolation and reuse you want for a project:

![Pipeline overview](https://raw.githubusercontent.com/wiki/jenkinsci/seed-plugin/Overview_Pipeline.png)

* in the direct DSL mode, the branch contains directly the Job DSL script which defines its pipeline
* if this script needs extra helper classes, a property file can be added to define a list of dependencies to download and associate to the classpath of the Job DSL script
* finally, if a direct script is not possible for reuse or security reasons, the Job DSL script itself can be downloaded from an external pipeline library

The plugin implementation is illustrated by the diagram below:

![Implementation overview](https://raw.githubusercontent.com/wiki/jenkinsci/seed-plugin/ImplementationOverview.png)

## Documentation

Documentation is available in the [Wiki](https://github.com/jenkinsci/seed-plugin/wiki).

Quick links:

* [Configuration](https://github.com/jenkinsci/seed-plugin/wiki/Configuration)
* [GitHub configuration](https://github.com/jenkinsci/seed-plugin/wiki/GitHub)
