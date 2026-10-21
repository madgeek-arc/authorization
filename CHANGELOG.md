# Changelog

## Unreleased

Changes on `main` since `v2.0.0`, taken from the commit history.

### Features

* Add group-aware overloads to the `Authorization` interface with backward-compatible default implementations ([9066fa9](https://github.com/madgeek-arc/authorization/commit/9066fa9))

### Bug Fixes

* Stop shipping `application.properties` in the jar (H2 file database under `/tmp`, `admin`/`password`, H2 console at `/h2`). Hosts must configure `authorization.datasource.*` themselves
* Declare the `auth*` infrastructure beans with `defaultCandidate = false` and stop enabling the global `DataSourceProperties`/`JpaProperties` beans, so the host's default `DataSource`, JPA and transaction manager auto-configuration is no longer displaced
* Add a unique constraint on `(subject, action, object, subjectGroup)` (`uk_permission_tuple`) and an index on `(action, object)`. With `hbm2ddl.auto=update`, existing duplicate rows must be removed before the constraint can be created
* Replace `@ConditionalOnClass` with `@ConditionalOnMissingBean(Authorization.class)` so downstream applications can supply their own implementation ([9754906](https://github.com/madgeek-arc/authorization/commit/9754906))
* Override `equals`/`hashCode` on `Permission` and remove setters to keep `hashCode` stable inside sets ([fc1f9d5](https://github.com/madgeek-arc/authorization/commit/fc1f9d5))
* Use the `authTransactionManager` in `@Transactional` annotations ([3f4947d](https://github.com/madgeek-arc/authorization/commit/3f4947d))

### Tests

* Cover coexistence with the host's data source and rejection of duplicate tuples

### Documentation

* Add README, `docs/`, contribution guide

## 2.0.0 (2025-02-11)

### ⚠ BREAKING CHANGES

* Change fully qualified package names

### Bug Fixes

* Add datasource properties qualifier ([ab570bd](https://github.com/madgeek-arc/authorization/commit/ab570bd9ad3da84181a3fbdf00eb19605bda27c6))
* Change datasource configuration and properties ([c969659](https://github.com/madgeek-arc/authorization/commit/c969659eaf6cddd4e86f1d38a53c71236d73c293))

### Build System

* Change fully qualified package names ([a2942e7](https://github.com/madgeek-arc/authorization/commit/a2942e799c69b9ee9461abf2a44e4ea0caa8887e))
