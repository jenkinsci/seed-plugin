Configuration
=============

The configuration of the Seed Jenkins plug-in is expressed in YAML.

```yaml
# Global configuration

# If a project is not explicitly defined, it is
# configured automatically using the `seed` branch
# strategy (see below)
# (optional, defaults to `yes`)
auto-configure: yes

# Global configuration parameters for the strategies
# come here
# ...

# Global configuration for the connectors comes here. For example:
# github-secret-key: 123
# or:
# http-secret-key: 12

# List of configured projects
# (optional, defaults to none)
projects:
   # An item for each configured project
   # The ID is the only required field
   # It defines the name of the project as it will
   # be sent and defined by the connector. For example,
   # for the GitHub connector, the project's ID will
   # be the full repository name.
   - id: nemerosa/ontrack
     # Name of the project
     # (optional, defaults to the part of the
     # ID after any last slash, or to the ID itself)
     name: ontrack
     # ID of the strategy to use
     # (optional, defaults to 'seed')
     branch-strategy: seed

     # Configuration for the connector which is
     # specific to the project comes here. For example:
     # github-secret-key: 1234466
     # or:
     # http-secret-key: 123

     # Configuration for the strategy which is
     # specific to the project comes here
     # ...

# List of configurable branching strategies, based
# on naming conventions only. For more complex
# branching strategies, you should consider
# create a branching strategy extension.
# (optional, defaults to none)
strategies:
   # An item for each branching strategy
   # The ID is the only required field. It is
   # referred to by the `branch-strategy` field
   # of the project
   - id: sample
     # Then, following field can be defined
     # All are optional and their default values
     # are displayed below (unless mentioned otherwise). Those
     # default values are based on the Seed branching strategy.
     #
     # All expressions have the following replacement tokens:
     # - project or PROJECT or Project - to render the project name in a specific style
     # - branch or BRANCH or Branch - to render the branch name in a specific style
     #
     # The path to the job which generates a new branch
     seed-expression: "${project}/${project}-seed"
     # The path to the job which generates a pipeline for a branch
     branch-seed-expression: "${project}/${project}-*/${project}-*-seed"
     # The path to the job which initiates a pipeline instance for a branch
     # This job is given a commit/revision as a parameter whose name is
     # defined by the `commit-parameter` attribute below
     branch-start-expression: "${project}/${project}-*/${project}-*-build"
     # Name of the branch to use when generating a job path in the expressions
     # listed above.
     # Besides the case formatting, all special characters are escaped into -
     branch-name-expression: "${branch}"
     # List of prefixes to remove from the branch name when using it to
     # generate the path to a job
     # By default, no prefix is applied.
     branch-name-prefixes:
        - "branches/"
     # Name of the parameter to pass to the pipeline starting job which
     # will contain the name of the job
     commit-parameter: "COMMIT"
```

Thus, the most basic configuration you can have is... an _empty file_.

It will have the following characteristics:

* projects are auto configured using the default `seed` branching strategy
* no security enabled for the connectors (because no secret key is defined)

## Strategies

The [Seed](https://github.com/nemerosa/seed) branching strategy is the
default being used in this plug-in and is referred to using the `seed` ID.
Normally, you do not need to declare it, unless you declare another default
strategy and want to use the `seed` strategy for another project, like in:

```yaml
# Default strategy for all projects
branch-strategy: "custom"
projects:
   - id: "A"
     # This project uses the 'custom' strategy
   - id: "B"
     branch-strategy: "seed"
     # This project uses the 'seed' strategy
```

When your branching strategy is similar to the Seed one, but with different
names for the jobs, you can use a configurable branch strategy, declared
directly in the configuration file. For example, if the starting job for a
pipeline is `_package` instead of `_build`, you can have:

```yaml
strategies:
   - id: "package"
     branch-start-expression: "${project}/${project}-*/${project}-*-package"
projects:
   - id: "MyProject"
     branch-strategy: "package"
```

If all your projects are using the same strategy, you can make this strategy a
default:

```yaml
branch-strategy: "package"
strategies:
   - id: "package"
     branch-start-expression: "${project}/${project}-*/${project}-*-package"
```

For more complex strategies, you can always create a Jenkins plug-in which provides a
[new branching strategy](extension/BranchingStrategy.md).

### Project level parameters for the Seed branching strategy

Those parameters can also be defined in the global configuration.

* `pipeline-delete` - defaults to `yes` - defines if the whole pipeline is
  deleted when a branch is deleted, or only its pipeline generator.
* `pipeline-auto` - defaults to `yes` - defines if the pipeline must be
automatically regenerated (by triggering the branch seed job) if a file
changes in the `seed` folder
* `pipeline-trigger` - defaults to `yes` - defines if the pipeline must
be started in case of a commit
* `seed` - default to `${project}/${project}-seed` - path to the job which
generates a new branch pipeline seed. The `BRANCH` parameter
contains the name of the branch to initialise.
* `pipeline-seed` - default to `${project}/${project}-*/${project}-*-seed` -
path to the job which is triggered to generate a new branch pipeline
* `pipeline-start` - default to `${project}/${project}-*/${project}-*-build` -
path to the job which is triggered to start a pipeline.
* `pipeline-commit` - defaults to `COMMIT` - defines the name of the parameter
to send to the pipeline start job to contain the commit or the revision to build

## Connectors

* [HTTP API](connector/HTTP.md)
* [GitHub Web hook](connector/GitHub.md)
