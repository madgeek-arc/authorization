package gr.uoa.di.madgik.authorization.repository;

import gr.uoa.di.madgik.authorization.domain.Permission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepository extends CrudRepository<Permission, Long> {

    List<Permission> findBySubject(String sub);
    List<Permission> findByAction(String action);
    List<Permission> findByObject(String obj);

    Set<Permission> findBySubjectAndAction(String sub, String action);
    Set<Permission> findBySubjectAndObject(String sub, String obj);
    Set<Permission> findByActionAndObject(String action, String obj);

    Optional<Permission> findBySubjectAndActionAndObject(String sub, String action, String obj);
    Set<Permission> findAllBySubjectAndActionAndObject(String sub, String action, String obj);

    void deleteAllBySubject(String sub);
    void deleteAllBySubjectAndActionAndObject(String sub, String action, String obj);
    boolean existsBySubjectAndActionAndObject(String sub, String action, String obj);

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    List<Permission> findBySubjectAndSubjectGroup(String sub, String group);
    List<Permission> findByActionAndSubjectGroup(String action, String group);
    List<Permission> findByObjectAndSubjectGroup(String obj, String group);

    Set<Permission> findBySubjectAndActionAndSubjectGroup(String sub, String action, String group);
    Set<Permission> findBySubjectAndObjectAndSubjectGroup(String sub, String obj, String group);
    Set<Permission> findByActionAndObjectAndSubjectGroup(String action, String obj, String group);


    Optional<Permission> findBySubjectAndActionAndObjectAndSubjectGroup(String sub, String action, String obj, String group);
    Set<Permission> findAllBySubjectAndActionAndObjectAndSubjectGroup(String sub, String action, String obj, String group);


    void deleteAllBySubjectAndSubjectGroup(String sub, String group);
    void deleteAllBySubjectAndActionAndObjectAndSubjectGroup(String sub, String action, String obj, String group);
    boolean existsBySubjectAndActionAndObjectAndSubjectGroup(String sub, String action, String obj, String group);

}
