package gr.athenarc.authorization.service;

import gr.athenarc.authorization.domain.AuthTriple;
import gr.athenarc.authorization.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimpleAuthorizationService implements AuthorizationService {

    private final AuthRepository repository;

    @Autowired
    public SimpleAuthorizationService(AuthRepository authRepository) {
        this.repository = authRepository;
    }

    @Override
    public boolean canDo(String sub, String action, String obj) {
        return repository.existsBySubjectAndActionAndObject(sub, action, obj);
    }

    @Override
    public List<AuthTriple> whoCan(String action, String obj) {
        return repository.findByActionAndObject(action, obj);
    }

    @Override
    public List<AuthTriple> whatCan(String sub, String obj) {
        return repository.findBySubjectAndObject(sub, obj);
    }

    @Override
    public List<AuthTriple> whereCan(String sub, String action) {
        return repository.findBySubjectAndAction(sub, action);
    }
}
