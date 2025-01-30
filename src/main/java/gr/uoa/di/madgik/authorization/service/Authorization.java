/**
 * Copyright 2021-2025 OpenAIRE AMKE
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

import gr.uoa.di.madgik.authorization.domain.Permission;

import java.util.Set;

public interface Authorization {

    boolean canDo(String sub, String action, String obj);

    default boolean canDo(Permission permission) {
        boolean result = false;
        if (permission != null) {
            result = canDo(permission.getSubject(), permission.getAction(), permission.getObject());
        }
        return result;
    }

    Set<Permission> whoCan(String action, String obj);

    Set<Permission> whatCan(String sub, String obj);

    Set<Permission> whereCan(String sub, String action);
}
