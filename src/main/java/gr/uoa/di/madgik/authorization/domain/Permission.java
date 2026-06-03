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

package gr.uoa.di.madgik.authorization.domain;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class Permission {
    private static final String DEFAULT_GROUP = "default";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String subjectGroup = DEFAULT_GROUP;

    private String subject;
    private String action;
    private String object;

    public Permission() {}

    public Permission(String subject, String action, String object) {
        this.subject = subject;
        this.action = action;
        this.object = object;
    }

    public Permission(String subject, String action, String object, String subjectGroup) {
        this.subject = subject;
        this.action = action;
        this.object = object;
        this.subjectGroup = subjectGroup;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Business-key fields are intentionally read-only after construction so that
    // hashCode stays stable when a Permission is held inside a Set.
    public String getSubject() {
        return subject;
    }

    public String getAction() {
        return action;
    }

    public String getObject() {
        return object;
    }

    public String getSubjectGroup() {
        return subjectGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission)) return false;
        Permission that = (Permission) o;
        return Objects.equals(subject, that.subject)
                && Objects.equals(action, that.action)
                && Objects.equals(object, that.object)
                && Objects.equals(subjectGroup, that.subjectGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, action, object, subjectGroup);
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", subjectGroup='" + subjectGroup + '\'' +
                ", subject='" + subject + '\'' +
                ", action='" + action + '\'' +
                ", object='" + object + '\'' +
                '}';
    }
}
