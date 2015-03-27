The `HTTP` connector exposes a very simple API, which allows you to pilot
the Jenkins plug-in from anywhere, using simple HTTP calls.

In the samples below, the Jenkins instance is designed as `http://localhost:8080/jenkins`
but it can be of course any other address.

The HTTP end point is available on the `seed-http` URI.

## Create

Triggering the creation of a branch in a project.

* URI: `/seed-http/create`
* Parameters:
  * `project` - ID of the project
  * `branch` - name of the branch to create

Sample:

```bash
curl -X POST http://localhost/jenkins/seed-http/create?project=PROJECT&branch=BRANCH
```

## Delete

Triggering the deletion of a branch in a project.

* URI: `/seed-http/delete`
* Parameters:
  * `project` - ID of the project
  * `branch` - name of the branch to delete

According to the [settings](../Configuration.md) of the project, the branch
pipeline will be deleted entirely or only its generator (and the pipeline
   be kept intact).

Sample:

```bash
curl -X POST http://localhost/jenkins/seed-http/delete?project=PROJECT&branch=BRANCH
```
