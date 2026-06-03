/*
 * Copyright 2022-2026 OpenAIRE AMKE
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

package gr.uoa.di.madgik.authorization;

import gr.uoa.di.madgik.authorization.config.AuthorizationAutoConfiguration;
import gr.uoa.di.madgik.authorization.domain.Permission;
import gr.uoa.di.madgik.authorization.service.Authorization;
import gr.uoa.di.madgik.authorization.repository.PermissionRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest(classes = {AuthorizationAutoConfiguration.class}, properties = {"spring.profiles.active=test"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthorizationApplicationTests {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private Authorization authorization;

    private static final String SUBJECT_USER1 = "user1";
    private static final String SUBJECT_USER2 = "user2";
    private static final String ACTION_READ = "read";
    private static final String ACTION_WRITE = "write";
    private static final String ACTION_MANAGE = "manage";
    private static final String ACTION_VALIDATE = "validate";
    private static final String OBJECT_RESOURCE1 = "resource1";
    private static final String OBJECT_RESOURCE2 = "resource2";


    @Test
    void contextLoads() {
    }

    @Test
    @Order(1)
    void createEntries() {
        Permission triple = new Permission(SUBJECT_USER1, ACTION_READ, OBJECT_RESOURCE1);
        permissionRepository.save(triple);
        triple = new Permission(SUBJECT_USER1, ACTION_WRITE, OBJECT_RESOURCE1);
        permissionRepository.save(triple);
        triple = new Permission(SUBJECT_USER1, ACTION_MANAGE, OBJECT_RESOURCE1);
        permissionRepository.save(triple);
        triple = new Permission(SUBJECT_USER1, ACTION_VALIDATE, OBJECT_RESOURCE1);
        permissionRepository.save(triple);

        triple = new Permission(SUBJECT_USER2, ACTION_WRITE, OBJECT_RESOURCE1);
        permissionRepository.save(triple);
        triple = new Permission(SUBJECT_USER2, ACTION_READ, OBJECT_RESOURCE1);
        permissionRepository.save(triple);

        triple = new Permission(SUBJECT_USER2, ACTION_WRITE, OBJECT_RESOURCE2);
        permissionRepository.save(triple);
        triple = new Permission(SUBJECT_USER2, ACTION_READ, OBJECT_RESOURCE2);
        permissionRepository.save(triple);
    }

    @Test
    @Order(3)
    void findEntriesBySubject() {
        List<Permission> list = permissionRepository.findBySubject("user1");
        assertEquals("user1 triples = 4", 4, list.size());

        list = permissionRepository.findBySubject("user2");
        assertEquals("user2 triples = 4", 4, list.size());
    }

    @Test
    @Order(4)
    void entryExists() {
        boolean hasAccess = authorization.canDo(SUBJECT_USER2, ACTION_READ, OBJECT_RESOURCE1);
        assertEquals("user2 can read resource1 = true", true, hasAccess);
    }

    @Test
    @Order(5)
    void entryNotExists() {
        boolean hasAccess = authorization.canDo(SUBJECT_USER2, ACTION_VALIDATE, OBJECT_RESOURCE2);
        assertEquals("user2 can validate resource2 = false", false, hasAccess);
    }

    @Test
    @Order(6)
    void deleteEntries() {
        permissionRepository.deleteAllBySubjectAndActionAndObject(SUBJECT_USER1, ACTION_READ, OBJECT_RESOURCE1);
        permissionRepository.deleteAllBySubjectAndActionAndObject(SUBJECT_USER1, ACTION_WRITE, OBJECT_RESOURCE1);
        permissionRepository.deleteAllBySubjectAndActionAndObject(SUBJECT_USER1, ACTION_MANAGE, OBJECT_RESOURCE1);
        permissionRepository.deleteAllBySubjectAndActionAndObject(SUBJECT_USER1, ACTION_VALIDATE, OBJECT_RESOURCE1);

        permissionRepository.deleteAllBySubjectAndActionAndObject(SUBJECT_USER2, ACTION_WRITE, OBJECT_RESOURCE1);
        permissionRepository.deleteAllBySubjectAndActionAndObject(SUBJECT_USER2, ACTION_READ, OBJECT_RESOURCE1);

        permissionRepository.deleteAllBySubjectAndActionAndObject(SUBJECT_USER2, ACTION_WRITE, OBJECT_RESOURCE2);
        permissionRepository.deleteAllBySubjectAndActionAndObject(SUBJECT_USER2, ACTION_READ, OBJECT_RESOURCE2);

        assertEquals("all entries deleted", 0, ((List<Permission>) permissionRepository.findAll()).size());
    }

    @Test
    @Transactional
    void subjectGroupIsolation() {
        String groupA = "groupA";
        String groupB = "groupB";

        Permission pA = permissionRepository.save(new Permission(SUBJECT_USER1, ACTION_READ, OBJECT_RESOURCE1, groupA));
        Permission pB = permissionRepository.save(new Permission(SUBJECT_USER1, ACTION_READ, OBJECT_RESOURCE1, groupB));

        List<Permission> groupAResults = permissionRepository.findBySubjectAndSubjectGroup(SUBJECT_USER1, groupA);
        assertEquals("groupA has 1 entry", 1, groupAResults.size());
        assertEquals("groupA entry group", groupA, groupAResults.get(0).getSubjectGroup());

        List<Permission> groupBResults = permissionRepository.findBySubjectAndSubjectGroup(SUBJECT_USER1, groupB);
        assertEquals("groupB has 1 entry", 1, groupBResults.size());

        assertTrue(permissionRepository.existsBySubjectAndActionAndObjectAndSubjectGroup(SUBJECT_USER1, ACTION_READ, OBJECT_RESOURCE1, groupA));
        assertFalse(permissionRepository.existsBySubjectAndActionAndObjectAndSubjectGroup(SUBJECT_USER1, ACTION_WRITE, OBJECT_RESOURCE1, groupA));

        permissionRepository.delete(pA);
        permissionRepository.delete(pB);
    }
}
