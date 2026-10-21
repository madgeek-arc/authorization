# Authorization — Development

## Tools

| Tool | Version | Evidence |
|---|---|---|
| JDK | 21+ | `pom.xml` `java.version` |
| Maven | via wrapper (`./mvnw`) | `.mvn/wrapper` |

There's no linter, formatter, licence-header plugin or code generation configured. Java files carry
an Apache 2.0 header by convention.

## Commands

```bash
./mvnw clean verify        # compile + 53 tests on in-memory H2
./mvnw install             # install the SNAPSHOT for local host applications
```

Verified: `./mvnw verify` passes (53 tests).

## Trying changes in a host application

1. `./mvnw install`.
2. Point the host at `gr.uoa.di.madgik:authorization:<SNAPSHOT>`.
3. Configure `authorization.datasource.*` against a local database, for example PostgreSQL in Docker,
   or H2 (add `com.h2database:h2` to the host for that).
4. Set `authorization.jpa.properties.hibernate.show_sql=true` to inspect queries.

## Adding a query

Add a derived query method to `PermissionRepository`. Keep the class-level read-only transaction
for reads. Annotate writes with `@Transactional(transactionManager = "authTransactionManager")`.
Add a test in `PermissionRepositoryTest`.
