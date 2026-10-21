# Authorization — Testing

| Suite | Tests | Covers |
|---|---|---|
| `AuthorizationApplicationTests` | 7 | Context load, ordered create/read/check/delete flow, group isolation |
| `repository/PermissionRepositoryTest` | 24 | Every finder, `exists` and delete method, with and without group |
| `service/AuthorizationServiceTest` | 19 | `canDo`/`whoCan`/`whatCan`/`whereCan` and their group overloads, `canDo(null)` |
| `config/AuthorizationAutoConfigurationTest` | 3 | Next to Spring Boot's DataSource/HibernateJpa/Transaction auto-configurations: the host's `dataSource`, `entityManagerFactory` and `transactionManager` stay in place; store and query; duplicate tuple rejected |

The first three suites start only `AuthorizationAutoConfiguration` (`@SpringBootTest(classes = ...)`) with
profile `test`, which loads `src/test/resources/application-test.properties`: in-memory H2 and
`create-drop`. `PermissionRepositoryTest` and `AuthorizationServiceTest` roll back each test
(`@Transactional("authTransactionManager")`). `AuthorizationAutoConfigurationTest` uses an
`ApplicationContextRunner` with its own in-memory databases.

## Running

```bash
./mvnw test
./mvnw test -Dtest=AuthorizationServiceTest
```

No external services are needed. Result: 53 tests, 0 failures.

## Not covered

- Non-H2 databases (dialect and naming behaviour on PostgreSQL).
- No coverage tool and no CI configuration in the repository.
