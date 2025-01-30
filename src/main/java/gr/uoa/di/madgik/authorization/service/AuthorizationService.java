package gr.uoa.di.madgik.authorization.service;

import gr.uoa.di.madgik.authorization.domain.Permission;
import gr.uoa.di.madgik.authorization.repository.PermissionRepository;

import java.util.Set;

public class AuthorizationService implements Authorization {

    private final PermissionRepository repository;

    public AuthorizationService(PermissionRepository permissionRepository) {
        this.repository = permissionRepository;
    }

    @Override
    public boolean canDo(String sub, String action, String obj) {
        return repository.existsBySubjectAndActionAndObject(sub, action, obj);
    }

    @Override
    public Set<Permission> whoCan(String action, String obj) {
        return repository.findByActionAndObject(action, obj);
    }

    @Override
    public Set<Permission> whatCan(String sub, String obj) {
        return repository.findBySubjectAndObject(sub, obj);
    }

    @Override
    public Set<Permission> whereCan(String sub, String action) {
        return repository.findBySubjectAndAction(sub, action);
    }
}
