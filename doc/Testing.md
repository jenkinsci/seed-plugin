## Testing

Running the unit tests and integration tests:

```bash
./gradlew clean build
```

Running the acceptance tests with a local instance of Docker:

1. the `nemerosa/seed` image is built
1. a `nemerosa/seed` container is created and runs on an arbitrary port
1. the acceptance tests are run against this container
1. the container is removed

```bash
./gradlew clean localDockerAcceptanceTest
```

By default, the Docker host is assumed to run locally. If using the Docker machine, you can set the Docker host using:

```bash
./gradlew clean localDockerAcceptanceTest \
   -PjenkinsHost=`docker-machine ip`
```

You can also run the acceptance tests against an arbitrary running Jenkins instance, without relying on Docker build first. For example, if the Jenkins instance to test is located at https://test.com:8080

```bash
./gradlew clean localDockerAcceptanceTest \
   -P jenkinsScheme=https \
   -PjenkinsHost=test.com \
   -PjenkinsPort=8080
```

> Note that the full URL is not supported yet but will be.

All acceptance tests are located in the `net.nemerosa.seed.jenkins.docker` package and are active only if the `jenkinsUrl` system property is set.
