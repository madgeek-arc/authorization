package gr.athenarc.authorization;

import java.util.List;

public interface AuthorizationService {

    boolean canDo(String sub, String action, String obj);
    default boolean canDo(AuthTriple triple) {
        boolean result = false;
        if (triple != null) {
            result = canDo(triple.getSubject(), triple.getAction(), triple.getObject());
        }
        return result;
    }

    List<AuthTriple> whoCan(String action, String obj);
    List<AuthTriple> whatCan(String sub, String obj);
    List<AuthTriple> whereCan(String sub, String action);
}
