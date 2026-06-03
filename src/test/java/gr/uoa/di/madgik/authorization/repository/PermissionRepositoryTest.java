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

package gr.uoa.di.madgik.authorization.repository;

import gr.uoa.di.madgik.authorization.config.AuthorizationAutoConfiguration;
import gr.uoa.di.madgik.authorization.domain.Permission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {AuthorizationAutoConfiguration.class}, properties = {"spring.profiles.active=test"})
@Transactional("authTransactionManager")
class PermissionRepositoryTest {

    @Autowired
    private PermissionRepository repository;

    private static final String USER1 = "user1";
    private static final String USER2 = "user2";
    private static final String READ = "read";
    private static final String WRITE = "write";
    private static final String RES1 = "resource1";
    private static final String RES2 = "resource2";
    private static final String GROUP_A = "groupA";
    private static final String GROUP_B = "groupB";

    @BeforeEach
    void setUp() {
        repository.save(new Permission(USER1, READ, RES1));
        repository.save(new Permission(USER1, WRITE, RES1));
        repository.save(new Permission(USER1, READ, RES2));
        repository.save(new Permission(USER2, READ, RES1));
        repository.save(new Permission(USER2, WRITE, RES2));
    }

    // -------------------------------------------------------------------------
    // Single-field finders
    // -------------------------------------------------------------------------

    @Test
    void findBySubject_returnsAllForSubject() {
        List<Permission> results = repository.findBySubject(USER1);
        assertEquals(3, results.size());
        assertTrue(results.stream().allMatch(p -> USER1.equals(p.getSubject())));
    }

    @Test
    void findBySubject_returnsEmptyForUnknownSubject() {
        assertTrue(repository.findBySubject("nobody").isEmpty());
    }

    @Test
    void findByAction_returnsAllForAction() {
        List<Permission> results = repository.findByAction(READ);
        assertEquals(3, results.size());
        assertTrue(results.stream().allMatch(p -> READ.equals(p.getAction())));
    }

    @Test
    void findByObject_returnsAllForObject() {
        List<Permission> results = repository.findByObject(RES1);
        assertEquals(3, results.size());
        assertTrue(results.stream().allMatch(p -> RES1.equals(p.getObject())));
    }

    // -------------------------------------------------------------------------
    // Two-field finders
    // -------------------------------------------------------------------------

    @Test
    void findBySubjectAndAction_returnsMatchingSet() {
        Set<Permission> results = repository.findBySubjectAndAction(USER1, READ);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(p -> USER1.equals(p.getSubject()) && READ.equals(p.getAction())));
    }

    @Test
    void findBySubjectAndObject_returnsMatchingSet() {
        Set<Permission> results = repository.findBySubjectAndObject(USER1, RES1);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(p -> USER1.equals(p.getSubject()) && RES1.equals(p.getObject())));
    }

    @Test
    void findByActionAndObject_returnsMatchingSet() {
        Set<Permission> results = repository.findByActionAndObject(READ, RES1);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(p -> READ.equals(p.getAction()) && RES1.equals(p.getObject())));
    }

    // -------------------------------------------------------------------------
    // Three-field finders
    // -------------------------------------------------------------------------

    @Test
    void findAllBySubjectAndActionAndObject_returnsAllMatchingGroups() {
        repository.save(new Permission(USER1, READ, RES1, GROUP_A));
        repository.save(new Permission(USER1, READ, RES1, GROUP_B));

        Set<Permission> results = repository.findAllBySubjectAndActionAndObject(USER1, READ, RES1);
        // default group entry from setUp + groupA + groupB
        assertEquals(3, results.size());
    }

    // -------------------------------------------------------------------------
    // Exists
    // -------------------------------------------------------------------------

    @Test
    void existsBySubjectAndActionAndObject_trueWhenPresent() {
        assertTrue(repository.existsBySubjectAndActionAndObject(USER1, READ, RES1));
    }

    @Test
    void existsBySubjectAndActionAndObject_falseWhenAbsent() {
        assertFalse(repository.existsBySubjectAndActionAndObject(USER1, WRITE, RES2));
    }

    // -------------------------------------------------------------------------
    // Delete methods
    // -------------------------------------------------------------------------

    @Test
    void deleteAllBySubject_removesAllEntriesForSubject() {
        repository.deleteAllBySubject(USER1);
        assertTrue(repository.findBySubject(USER1).isEmpty());
        assertEquals(2, repository.findBySubject(USER2).size());
    }

    @Test
    void deleteAllBySubjectAndActionAndObject_removesOnlyMatchingEntry() {
        repository.deleteAllBySubjectAndActionAndObject(USER1, READ, RES1);
        assertFalse(repository.existsBySubjectAndActionAndObject(USER1, READ, RES1));
        assertTrue(repository.existsBySubjectAndActionAndObject(USER1, WRITE, RES1));
    }

    // -------------------------------------------------------------------------
    // Group-aware single-field finders
    // -------------------------------------------------------------------------

    @Test
    void findBySubjectAndSubjectGroup_returnsOnlyMatchingGroup() {
        repository.save(new Permission(USER1, READ, RES1, GROUP_A));
        repository.save(new Permission(USER1, WRITE, RES2, GROUP_A));
        repository.save(new Permission(USER1, READ, RES2, GROUP_B));

        List<Permission> results = repository.findBySubjectAndSubjectGroup(USER1, GROUP_A);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(p -> GROUP_A.equals(p.getSubjectGroup())));
    }

    @Test
    void findByActionAndSubjectGroup_returnsOnlyMatchingGroup() {
        repository.save(new Permission(USER1, READ, RES1, GROUP_A));
        repository.save(new Permission(USER2, READ, RES2, GROUP_A));

        List<Permission> results = repository.findByActionAndSubjectGroup(READ, GROUP_A);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(p -> READ.equals(p.getAction()) && GROUP_A.equals(p.getSubjectGroup())));
    }

    @Test
    void findByObjectAndSubjectGroup_returnsOnlyMatchingGroup() {
        repository.save(new Permission(USER1, READ, RES1, GROUP_A));
        repository.save(new Permission(USER2, WRITE, RES1, GROUP_A));

        List<Permission> results = repository.findByObjectAndSubjectGroup(RES1, GROUP_A);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(p -> RES1.equals(p.getObject()) && GROUP_A.equals(p.getSubjectGroup())));
    }

    // -------------------------------------------------------------------------
    // Group-aware two-field finders
    // -------------------------------------------------------------------------

    @Test
    void findBySubjectAndActionAndSubjectGroup_returnsMatchingSet() {
        repository.save(new Permission(USER1, READ, RES1, GROUP_A));
        repository.save(new Permission(USER1, READ, RES2, GROUP_A));
        repository.save(new Permission(USER1, READ, RES1, GROUP_B));

        Set<Permission> results = repository.findBySubjectAndActionAndSubjectGroup(USER1, READ, GROUP_A);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(p -> GROUP_A.equals(p.getSubjectGroup())));
    }

    @Test
    void findBySubjectAndObjectAndSubjectGroup_returnsMatchingSet() {
        repository.save(new Permission(USER1, READ, RES1, GROUP_A));
        repository.save(new Permission(USER1, WRITE, RES1, GROUP_A));
        repository.save(new Permission(USER1, READ, RES1, GROUP_B));

        Set<Permission> results = repository.findBySubjectAndObjectAndSubjectGroup(USER1, RES1, GROUP_A);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(p -> GROUP_A.equals(p.getSubjectGroup())));
    }

    @Test
    void findByActionAndObjectAndSubjectGroup_returnsMatchingSet() {
        repository.save(new Permission(USER1, READ, RES1, GROUP_A));
        repository.save(new Permission(USER2, READ, RES1, GROUP_A));
        repository.save(new Permission(USER1, READ, RES1, GROUP_B));

        Set<Permission> results = repository.findByActionAndObjectAndSubjectGroup(READ, RES1, GROUP_A);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(p -> GROUP_A.equals(p.getSubjectGroup())));
    }

    // -------------------------------------------------------------------------
    // Group-aware three-field finders
    // -------------------------------------------------------------------------

    @Test
    void findAllBySubjectAndActionAndObjectAndSubjectGroup_returnsEntryForGroup() {
        repository.save(new Permission(USER1, READ, RES1, GROUP_A));

        Set<Permission> results = repository.findAllBySubjectAndActionAndObjectAndSubjectGroup(USER1, READ, RES1, GROUP_A);
        assertEquals(1, results.size());
    }

    // -------------------------------------------------------------------------
    // Group-aware exists
    // -------------------------------------------------------------------------

    @Test
    void existsBySubjectAndActionAndObjectAndSubjectGroup_trueWhenPresent() {
        repository.save(new Permission(USER1, READ, RES1, GROUP_A));
        assertTrue(repository.existsBySubjectAndActionAndObjectAndSubjectGroup(USER1, READ, RES1, GROUP_A));
    }

    @Test
    void existsBySubjectAndActionAndObjectAndSubjectGroup_falseWhenAbsent() {
        assertFalse(repository.existsBySubjectAndActionAndObjectAndSubjectGroup(USER1, READ, RES1, GROUP_A));
    }

    @Test
    void existsBySubjectAndActionAndObjectAndSubjectGroup_falseForWrongGroup() {
        repository.save(new Permission(USER1, READ, RES1, GROUP_A));
        assertFalse(repository.existsBySubjectAndActionAndObjectAndSubjectGroup(USER1, READ, RES1, GROUP_B));
    }

    // -------------------------------------------------------------------------
    // Group-aware delete methods
    // -------------------------------------------------------------------------

    @Test
    void deleteAllBySubjectAndSubjectGroup_removesOnlyMatchingGroup() {
        repository.save(new Permission(USER1, READ, RES1, GROUP_A));
        repository.save(new Permission(USER1, WRITE, RES2, GROUP_A));
        repository.save(new Permission(USER1, READ, RES1, GROUP_B));

        repository.deleteAllBySubjectAndSubjectGroup(USER1, GROUP_A);

        assertTrue(repository.findBySubjectAndSubjectGroup(USER1, GROUP_A).isEmpty());
        assertEquals(1, repository.findBySubjectAndSubjectGroup(USER1, GROUP_B).size());
    }

    @Test
    void deleteAllBySubjectAndActionAndObjectAndSubjectGroup_removesOnlyMatchingEntry() {
        repository.save(new Permission(USER1, READ, RES1, GROUP_A));
        repository.save(new Permission(USER1, READ, RES1, GROUP_B));

        repository.deleteAllBySubjectAndActionAndObjectAndSubjectGroup(USER1, READ, RES1, GROUP_A);

        assertFalse(repository.existsBySubjectAndActionAndObjectAndSubjectGroup(USER1, READ, RES1, GROUP_A));
        assertTrue(repository.existsBySubjectAndActionAndObjectAndSubjectGroup(USER1, READ, RES1, GROUP_B));
    }
}
