package gr.athenarc.authorization.service;

import gr.athenarc.authorization.domain.Permission;
import gr.athenarc.authorization.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthorizationService implements Authorization {

    private final PermissionRepository repository;

    @Autowired
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
