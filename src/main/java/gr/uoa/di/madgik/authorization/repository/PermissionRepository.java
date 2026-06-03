/*
 * Copyright 2021-2026 OpenAIRE AMKE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gr.uoa.di.madgik.authorization.repository;

import gr.uoa.di.madgik.authorization.domain.Permission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
@Transactional(transactionManager = "authTransactionManager", readOnly = true)
public interface PermissionRepository extends CrudRepository<Permission, Long> {

    List<Permission> findBySubject(String sub);
    List<Permission> findByAction(String action);
    List<Permission> findByObject(String obj);

    Set<Permission> findBySubjectAndAction(String sub, String action);
    Set<Permission> findBySubjectAndObject(String sub, String obj);
    Set<Permission> findByActionAndObject(String action, String obj);

    Set<Permission> findAllBySubjectAndActionAndObject(String sub, String action, String obj);

    @Transactional(transactionManager = "authTransactionManager")
    void deleteAllBySubject(String sub);
    @Transactional(transactionManager = "authTransactionManager")
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


    Set<Permission> findAllBySubjectAndActionAndObjectAndSubjectGroup(String sub, String action, String obj, String group);


    @Transactional(transactionManager = "authTransactionManager")
    void deleteAllBySubjectAndSubjectGroup(String sub, String group);
    @Transactional(transactionManager = "authTransactionManager")
    void deleteAllBySubjectAndActionAndObjectAndSubjectGroup(String sub, String action, String obj, String group);
    boolean existsBySubjectAndActionAndObjectAndSubjectGroup(String sub, String action, String obj, String group);

}
