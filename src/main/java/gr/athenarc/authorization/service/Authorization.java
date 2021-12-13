package gr.athenarc.authorization.service;

import gr.athenarc.authorization.domain.AuthTriple;

import java.util.Set;

public interface Authorization {

    boolean canDo(String sub, String action, String obj);

    default boolean canDo(AuthTriple triple) {
        boolean result = false;
        if (triple != null) {
            result = canDo(triple.getSubject(), triple.getAction(), triple.getObject());
        }
        return result;
    }

    Set<AuthTriple> whoCan(String action, String obj);

    Set<AuthTriple> whatCan(String sub, String obj);

    Set<AuthTriple> whereCan(String sub, String action);
}
