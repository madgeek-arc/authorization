# Authorization — Integrations

| Direction | Counterpart | Protocol | Purpose | Configuration key | Evidence path/symbol |
|---|---|---|---|---|---|
| provided | Host Spring Boot application (in-process) | Java API: `Authorization`, `PermissionRepository` beans | Check and manage per-object permissions | — | `config/AuthorizationAutoConfiguration.java`, `service/Authorization.java`, `repository/PermissionRepository.java` |
| consumed | Relational database (provided by the host) | JDBC through HikariCP, JPA/Hibernate | Persist `Permission` rows | `authorization.datasource.*`, `authorization.jpa.*` | `AuthorizationAutoConfiguration#authDataSource`, `#authEntityManagerFactory` |

The library makes no network calls besides JDBC. Which applications embed it, and which database
engine they use, is recorded in their own documentation.

## Provided interfaces

There's no HTTP API or OpenAPI. The Java contract is summarised in
[architecture.md](architecture.md#interfaces). Writes happen through `PermissionRepository`
(`save`, `delete`, `deleteAllBySubject[AndSubjectGroup]`,
`deleteAllBySubjectAndActionAndObject[AndSubjectGroup]`). Transactions run on
`authTransactionManager`.

## Failure behaviour

| Situation | Behaviour |
|---|---|
| Database unreachable at startup | Hibernate reads the JDBC metadata when the entity manager factory starts. Without an explicit `hibernate.dialect`, or with `hbm2ddl.auto` set, an unreachable database fails the host's startup |
| Database unavailable at runtime | Spring `DataAccessException` subclasses, or `CannotCreateTransactionException` when no connection can be obtained, propagate to the caller. No retry or fallback |
| Unknown subject, action or object | `canDo` returns `false`. Finders return empty collections |
| Duplicate tuple saved | `DataIntegrityViolationException` (unique constraint `uk_permission_tuple`) |
| Timeouts | HikariCP defaults (30 s connection timeout) unless `authorization.datasource.configuration.*` overrides them |
