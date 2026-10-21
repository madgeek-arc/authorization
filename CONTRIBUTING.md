# Contributing

Contributions are welcome: bug reports, feature requests and pull requests.

## Reporting issues

Use [GitHub Issues](https://github.com/madgeek-arc/authorization/issues) for bugs, feature requests and
questions. For bugs, include the version, the environment, the steps to reproduce, and the expected
and actual behaviour.

## Branching

- `main` is the integration branch and always carries the next `-SNAPSHOT` version.
- Work on short-lived branches named `<type>/<short-description>` (for example `feat/permission-index`,
  `docs/configuration`) and open a pull request against `main`.
- Without write access to the repository, fork it, create the branch in your fork and open the
  pull request from there.

## Commit messages

Use [Conventional Commits](https://www.conventionalcommits.org/): `feat:`, `fix:`, `docs:`, `refactor:`,
`test:`, `build:`, `chore:`. Mark breaking changes with `!` or a `BREAKING CHANGE:` footer. The
history already follows this.

## Pull requests

- Describe the change and its motivation, and link the related issue.
- At least one maintainer review is required before merging.
- Update `README.md`/`docs/` when the Java API, configuration keys or bean names change. These are
  part of the library's contract.
- Add an entry to `CHANGELOG.md` under *Unreleased*.

## Required checks

- `./mvnw clean verify` must pass.
- New repository or service methods need tests in `PermissionRepositoryTest` /
  `AuthorizationServiceTest`.
- New Java files carry the Apache 2.0 header used in the existing sources.

## Releases and versioning

- [Semantic Versioning](https://semver.org/). Package renames and bean or property renames are
  breaking changes.
- Release with `maven-release-plugin` (`./mvnw release:prepare release:perform`). Tags are named
  `v<version>`, and artefacts deploy to the repositories in `pom.xml` `distributionManagement`.
- Move the *Unreleased* changelog entries under the new version heading as part of the release.
