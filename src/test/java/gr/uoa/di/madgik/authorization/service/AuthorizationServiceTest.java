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

package gr.uoa.di.madgik.authorization.service;

import gr.uoa.di.madgik.authorization.config.AuthorizationAutoConfiguration;
import gr.uoa.di.madgik.authorization.domain.Permission;
import gr.uoa.di.madgik.authorization.repository.PermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {AuthorizationAutoConfiguration.class}, properties = {"spring.profiles.active=test"})
@Transactional("authTransactionManager")
class AuthorizationServiceTest {

    @Autowired
    private Authorization authorization;

    @Autowired
    private PermissionRepository repository;

    private static final String USER1 = "user1";
    private static final String USER2 = "user2";
    private static final String READ = "read";
    private static final String WRITE = "write";
    private static final String MANAGE = "manage";
    private static final String RES1 = "resource1";
    private static final String RES2 = "resource2";

    @BeforeEach
    void setUp() {
        repository.save(new Permission(USER1, READ, RES1));
        repository.save(new Permission(USER1, WRITE, RES1));
        repository.save(new Permission(USER1, MANAGE, RES2));
        repository.save(new Permission(USER2, READ, RES1));
        repository.save(new Permission(USER2, READ, RES2));
    }

    // -------------------------------------------------------------------------
    // canDo(String, String, String)
    // -------------------------------------------------------------------------

    @Test
    void canDo_trueWhenPermissionExists() {
        assertTrue(authorization.canDo(USER1, READ, RES1));
    }

    @Test
    void canDo_falseWhenPermissionAbsent() {
        assertFalse(authorization.canDo(USER1, READ, RES2));
    }

    @Test
    void canDo_falseForUnknownSubject() {
        assertFalse(authorization.canDo("nobody", READ, RES1));
    }

    // -------------------------------------------------------------------------
    // canDo(Permission) — default interface method
    // -------------------------------------------------------------------------

    @Test
    void canDo_permissionObject_trueWhenExists() {
        assertTrue(authorization.canDo(new Permission(USER1, READ, RES1)));
    }

    @Test
    void canDo_permissionObject_falseWhenAbsent() {
        assertFalse(authorization.canDo(new Permission(USER1, READ, RES2)));
    }

    @Test
    void canDo_nullPermission_returnsFalse() {
        assertFalse(authorization.canDo(null));
    }

    // -------------------------------------------------------------------------
    // whoCan
    // -------------------------------------------------------------------------

    @Test
    void whoCan_returnsSubjectsWithActionOnObject() {
        Set<Permission> results = authorization.whoCan(READ, RES1);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(p -> READ.equals(p.getAction()) && RES1.equals(p.getObject())));
    }

    @Test
    void whoCan_returnsEmptyWhenNoneMatch() {
        assertTrue(authorization.whoCan(MANAGE, RES1).isEmpty());
    }

    // -------------------------------------------------------------------------
    // whatCan
    // -------------------------------------------------------------------------

    @Test
    void whatCan_returnsActionsForSubjectOnObject() {
        Set<Permission> results = authorization.whatCan(USER1, RES1);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(p -> USER1.equals(p.getSubject()) && RES1.equals(p.getObject())));
    }

    @Test
    void whatCan_singleResultForUser2OnResource2() {
        Set<Permission> results = authorization.whatCan(USER2, RES2);
        assertEquals(1, results.size());
        assertEquals(READ, results.iterator().next().getAction());
    }

    @Test
    void whatCan_returnsEmptyForUnknownCombination() {
        assertTrue(authorization.whatCan(USER1, "unknown").isEmpty());
    }

    // -------------------------------------------------------------------------
    // whereCan
    // -------------------------------------------------------------------------

    @Test
    void whereCan_returnsObjectsWhereSubjectCanDoAction() {
        Set<Permission> results = authorization.whereCan(USER1, READ);
        assertEquals(1, results.size());
        assertEquals(RES1, results.iterator().next().getObject());
    }

    @Test
    void whereCan_returnsMultipleObjects() {
        Set<Permission> results = authorization.whereCan(USER2, READ);
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(p -> RES1.equals(p.getObject())));
        assertTrue(results.stream().anyMatch(p -> RES2.equals(p.getObject())));
    }

    @Test
    void whereCan_singleResultForUser1Manage() {
        Set<Permission> results = authorization.whereCan(USER1, MANAGE);
        assertEquals(1, results.size());
        assertEquals(RES2, results.iterator().next().getObject());
    }

    // -------------------------------------------------------------------------
    // Group-aware overloads
    // -------------------------------------------------------------------------

    private static final String GROUP_A = "groupA";
    private static final String GROUP_B = "groupB";

    @Test
    void canDo_group_trueWhenPermissionExistsForGroup() {
        repository.save(new Permission(USER1, READ, RES1, GROUP_A));
        assertTrue(authorization.canDo(USER1, READ, RES1, GROUP_A));
    }

    @Test
    void canDo_group_falseForWrongGroup() {
        repository.save(new Permission(USER1, READ, RES1, GROUP_A));
        assertFalse(authorization.canDo(USER1, READ, RES1, GROUP_B));
    }

    @Test
    void whoCan_group_returnsOnlyMatchingGroup() {
        repository.save(new Permission(USER1, READ, RES1, GROUP_A));
        repository.save(new Permission(USER2, READ, RES1, GROUP_A));
        repository.save(new Permission(USER1, READ, RES1, GROUP_B));

        Set<Permission> results = authorization.whoCan(READ, RES1, GROUP_A);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(p -> GROUP_A.equals(p.getSubjectGroup())));
    }

    @Test
    void whatCan_group_returnsOnlyMatchingGroup() {
        repository.save(new Permission(USER1, READ, RES1, GROUP_A));
        repository.save(new Permission(USER1, WRITE, RES1, GROUP_A));
        repository.save(new Permission(USER1, READ, RES1, GROUP_B));

        Set<Permission> results = authorization.whatCan(USER1, RES1, GROUP_A);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(p -> GROUP_A.equals(p.getSubjectGroup())));
    }

    @Test
    void whereCan_group_returnsOnlyMatchingGroup() {
        repository.save(new Permission(USER1, READ, RES1, GROUP_A));
        repository.save(new Permission(USER1, READ, RES2, GROUP_A));
        repository.save(new Permission(USER1, READ, RES1, GROUP_B));

        Set<Permission> results = authorization.whereCan(USER1, READ, GROUP_A);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(p -> GROUP_A.equals(p.getSubjectGroup())));
    }
}
