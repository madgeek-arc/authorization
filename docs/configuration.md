# Authorization — Configuration

All settings are Spring Boot properties supplied by the host application. They're bound in
`src/main/java/gr/uoa/di/madgik/authorization/config/AuthorizationAutoConfiguration.java`.

| Property | Required | Default | Description |
|---|---|---|---|
| `authorization.datasource.url` | **Yes** | — | JDBC URL of the permissions database |
| `authorization.datasource.username` | Usually | — | DB user |
| `authorization.datasource.password` | Usually | — | DB password. **Secret** |
| `authorization.datasource.driverClassName` | No | detected from URL | JDBC driver class. The driver must be on the host's classpath |
| `authorization.datasource.*` (other) | No | Spring Boot defaults | Any `DataSourceProperties` field (`authDataSourceProperties` bean) |
| `authorization.datasource.configuration.*` | No | Hikari defaults | HikariCP pool settings bound to `authDataSource`, for example `maximum-pool-size`, `connection-timeout`, `pool-name` |
| `authorization.jpa.properties.*` | Recommended | — | Raw Hibernate properties passed to `authEntityManagerFactory`, for example `hibernate.dialect`, `hibernate.hbm2ddl.auto`, `hibernate.show_sql` |
| `authorization.jpa.*` (other) | No | — | Other `JpaProperties` fields are bound, but only `properties` is passed to the entity manager factory |

The jar ships no defaults: the host must set at least `authorization.datasource.url` (and usually the
credentials and dialect).

## Schema

The library runs no migrations. Either let Hibernate manage the table with
`authorization.jpa.properties.hibernate.hbm2ddl.auto` (`update`, or `validate` with a pre-created table;
`create-drop` is for tests only), or create it yourself. For PostgreSQL the mapping corresponds to the
following. Hibernate generates the unquoted names `Permission`, `subjectGroup` and `Permission_SEQ`, which
PostgreSQL folds to lower case:

```sql
create sequence permission_seq increment by 50;
create table permission (
    id           bigint primary key,
    subjectgroup varchar(255),
    subject      varchar(255),
    action       varchar(255),
    object       varchar(255),
    constraint uk_permission_tuple unique (subject, action, object, subjectgroup)
);
create index idx_permission_action_object on permission (action, object);
```

With `hbm2ddl.auto=update`, Hibernate can't add `uk_permission_tuple` to a table that already contains
duplicate tuples: it logs the failure and continues. Remove the duplicates first, for example:

```sql
delete from permission p using permission q
where p.id > q.id and p.subject = q.subject and p.action = q.action
  and p.object = q.object and p.subjectgroup = q.subjectgroup;
```

Saving a duplicate tuple raises `DataIntegrityViolationException`.

## Example (placeholders only)

```properties
authorization.datasource.url=jdbc:postgresql://<db-host>:5432/<permissions-db>
authorization.datasource.username=<db-user>
authorization.datasource.password=<db-password>
authorization.datasource.driverClassName=org.postgresql.Driver
authorization.datasource.configuration.maximum-pool-size=10
authorization.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
authorization.jpa.properties.hibernate.hbm2ddl.auto=update
authorization.jpa.properties.hibernate.show_sql=false
```

## Beans the host can override

Each infrastructure bean is `@ConditionalOnMissingBean` by **name**. Define a bean with the same
name to replace it, and declare it with `@Bean(defaultCandidate = false)` so it doesn't compete with the
host's own beans of the same type.

| Bean name | Type | Override use |
|---|---|---|
| `authDataSource` | `HikariDataSource` | Reuse an existing pool, or use a non-Hikari datasource. The declared parameter type is `HikariDataSource` |
| `authEntityManagerFactory` | `LocalContainerEntityManagerFactoryBean` | Custom JPA setup |
| `authTransactionManager` | `PlatformTransactionManager` | Custom transaction handling |
| any `Authorization` bean | `Authorization` | Replace `AuthorizationService` entirely |

`authDataSourceProperties` and `authJpaProperties` are always registered.

## Coexisting with the host's datasource

All `auth*` beans are declared with `defaultCandidate = false`. They aren't injected by type and are
ignored by Spring Boot's `@ConditionalOnMissingBean`/`@ConditionalOnSingleCandidate` checks, so a host
that uses Spring Boot's default `spring.datasource.*` / JPA auto-configuration keeps its `dataSource`,
`entityManagerFactory` and `transactionManager`.
(`AuthorizationAutoConfigurationTest#keepsHostDataSourceAndJpaBeans`)

Inject them by name (`@Qualifier("authDataSource")`) when needed, and reference `authTransactionManager`
explicitly in any host `@Transactional` that writes permissions.

## Environments

| Environment | Datasource |
|---|---|
| Tests (this repo) | `jdbc:h2:mem:authDB`, `create-drop` (`src/test/resources/application-test.properties`, profile `test`) |
| Host deployments | Set per deployment through environment variables or an external configuration. Not stored in the repository |

## Secrets

`authorization.datasource.password` (and the username) must come from the host's secret management,
for example environment variables (`AUTHORIZATION_DATASOURCE_PASSWORD`, through Spring Boot relaxed
binding). Never commit them.
