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
