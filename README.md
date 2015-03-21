Seed Jenkins plug-in
====================

This plug-in interacts with a Jenkins instance in order to pilot [Seed](https://github.com/nemerosa/seed) jobs
when some events are received.

## Events

Four events are currently supported by the Seed plug-in.

### Creation of a branch

Whenever a branch is created in a project, the `branch-creation` event is sent, with as a parameter the name of the branch.

The _branch seed_ job is triggered.

### Update of Seed files in a branch

### Push on a branch

## Connectors

### REST

### GitHub

### Manual
