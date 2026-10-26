# Authorization — Security

## Authorisation model

- A **permission** is a tuple `(subject, action, object, subjectGroup)`. Holding a permission means a
  row with exactly those values exists. (`AuthorizationService#canDo`)
- **Deny by default**: no row means no permission. (`AuthorizationServiceTest#canDo_falseForUnknownSubject`)
- **No roles, hierarchy or wildcards.** Strings are compared exactly. Groupings such as roles or teams
  have to be stored as their own subjects, or resolved by the host before calling `canDo`.
- **`subjectGroup`** namespaces permissions (default `"default"`). By design, calls without a group
  match **any** group. Where a check must stay within one group, call the group-aware overloads.
  (`PermissionRepositoryTest#findAllBySubjectAndActionAndObject_returnsAllMatchingGroups`)
- **Custom `Authorization` implementations:** the group overloads' default implementations ignore
  `group` and delegate to the ungrouped method. A host-supplied implementation that doesn't override
  them silently drops group isolation. (`service/Authorization.java`)

## Who can change permissions

The library doesn't restrict callers. Any host code with access to `PermissionRepository` can create
or delete permissions. The host is responsible for guarding the code paths that grant permissions.

## Authentication, tokens, CORS/CSRF

Not applicable. The library has no web layer and doesn't handle identities. The host maps its
authenticated principal to a `subject` string.

## Secrets

Database credentials are the only secrets. Supply them from the host's secret management. See
[configuration.md](configuration.md#secrets). Use TLS on the JDBC connection where the database is
remote (driver-specific URL parameters, for example `sslmode=require` for PostgreSQL).

## Personal data and logging

Subjects are typically user identifiers, so the permission table holds personal data. The host's
data-retention and erasure processes must cover it (`deleteAllBySubject` removes every permission of
a subject). The library itself logs nothing. (no logger in the sources)
