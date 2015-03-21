Configuration
=============

The configuration of the Seed Jenkins plug-in is expressed in YAML:

```yaml
# Global configuration
github: yes
# Projects
projects:
   - id: nemerosa/ontrack
     # Project specific configuration
     name: ontrack
     # Project overridden configuration
     github: yes
```

## Global parameters

| Parameter | Default | Values | Description | Project level? |
|-----------|---------|--------|-------------|----------------|
| `github` | `no` | `yes` or `no` | Is a GitHub web hook active? | Yes |
| `github-secret` | _none_ | Token | Secret token to use for securing the hook | Yes |


## Project parameters

| Parameter | Default | Description | Example |
|-----------|---------|-------------|---------|
| `id` | _required_ | ID used to identify the project in the event received by the plug-in | `nemerosa/ontrack` |
| `name` | Part after the last slash in the `id` | Name used to identify the folder and the jobs for the project | `ontrack` (default) |
| `seed` | `${name}/${name}-seed` | Path to the Project Seed job | `ontrack/ontrack-seed` (default) |
| `branch-seed` | `${name}/${name}-*/${name}-*-seed` | Path to a Branch Seed job, where `*` stands for the branch name | `ontrack/ontrack-*/ontrack-*-seed` |
| `branch-start` | `${name}/${name}-*/${name}-*-build` | Path to a Branch pipeline starting job, where `*` stands for the branch name | `ontrack/ontrack-*/ontrack-*-build` |
