# Authorization

A Spring Boot auto-configured library that stores permissions as `(subject, action, object, group)`
tuples in a dedicated relational database and answers questions such as *can this subject do this
action on this object?*

## Component metadata

| Field | Value |
|---|---|
| Component | Authorization (`gr.uoa.di.madgik:authorization`) |
| Type | Supporting service (Java library embedded in a host application, not deployed on its own) |
| Technology | Java 21, Spring Boot 3.5 auto-configuration, Spring Data JPA / Hibernate, HikariCP, Maven |
| API | No (Java API only) |
| Swagger | Not applicable |
| Deployment | Library (Maven artefact) |
| Documentation | [docs/](docs/) |

## Role

Provides the per-resource permission store used by backend services to authorise actions on
individual objects. The host application decides what subjects, actions and objects mean.

## Prerequisites

- A Spring Boot 3 application on Java 21+.
- A JDBC database and its driver on the host's classpath (for example PostgreSQL). The library
  bundles no runtime driver.

## Quick start

```xml
<dependency>
    <groupId>gr.uoa.di.madgik</groupId>
    <artifactId>authorization</artifactId>
    <version>2.0.0</version>
</dependency>
```

```properties
authorization.datasource.url=jdbc:postgresql://<db-host>:5432/<permissions-db>
authorization.datasource.username=<db-user>
authorization.datasource.password=<db-password>
authorization.datasource.driverClassName=org.postgresql.Driver
authorization.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
authorization.jpa.properties.hibernate.hbm2ddl.auto=update
```

```java
@Service
public class DocumentGuard {

    private final Authorization authorization;
    private final PermissionRepository permissions;

    public DocumentGuard(Authorization authorization, PermissionRepository permissions) {
        this.authorization = authorization;
        this.permissions = permissions;
    }

    public void share(String user, String documentId) {
        permissions.save(new Permission(user, "read", documentId));
    }

    public boolean canRead(String user, String documentId) {
        return authorization.canDo(user, "read", documentId);
    }
}
```

The library uses its own data source, entity manager factory and transaction manager. They don't
replace the host's `spring.datasource.*` / JPA setup. Write permissions inside transactions on
`authTransactionManager`. See [docs/configuration.md](docs/configuration.md#coexisting-with-the-hosts-datasource).

## Build

```bash
./mvnw clean verify     # build + tests (in-memory H2)
./mvnw install          # install into the local Maven repository
```

## Documentation

- [Architecture](docs/architecture.md) · [Integration](docs/integration.md)
- [Configuration](docs/configuration.md) · [Security](docs/security.md)
- [Development](docs/development.md) · [Testing](docs/testing.md)
- [Contributing](CONTRIBUTING.md) · [Changelog](CHANGELOG.md)

There are no HTTP API, deployment or operations guides, because the library is not deployed on its own.

## Licence

Apache License 2.0, see [LICENSE](LICENSE). Maintained by the madgeek-arc team
(<https://github.com/madgeek-arc/authorization>).
