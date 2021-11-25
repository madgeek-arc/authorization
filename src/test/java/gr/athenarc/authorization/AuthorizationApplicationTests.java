package gr.athenarc.authorization;

import gr.athenarc.authorization.domain.AuthTriple;
import gr.athenarc.authorization.service.AuthorizationService;
import gr.athenarc.authorization.repository.AuthRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthorizationApplicationTests {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private AuthorizationService authorizationService;

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
    @Transactional
    void testUpdateUsingTransactional() {
        // create entry
        AuthTriple triple = new AuthTriple("test", "write", "test");
        triple = authRepository.save(triple);

        Long id = triple.getId();

        // Update using setters
        triple.setSubject("user");
        triple.setAction("read");
        triple.setObject("resource");

        // retrieve result from db
        Optional<AuthTriple> resultOp = authRepository.findById(id);
        assertTrue(resultOp.isPresent());
        AuthTriple result = resultOp.get();

        // test triple-result values
        assertEquals("Subject: ", triple.getSubject(), result.getSubject());
        assertEquals("Action: ", triple.getAction(), result.getAction());
        assertEquals("Object: ", triple.getObject(), result.getObject());

        authRepository.delete(triple);
    }

    @Test
    @Order(2)
    void createEntries() {
        AuthTriple triple = new AuthTriple(SUBJECT_USER1, ACTION_READ, OBJECT_RESOURCE1);
        authRepository.save(triple);
        triple = new AuthTriple(SUBJECT_USER1, ACTION_WRITE, OBJECT_RESOURCE1);
        authRepository.save(triple);
        triple = new AuthTriple(SUBJECT_USER1, ACTION_MANAGE, OBJECT_RESOURCE1);
        authRepository.save(triple);
        triple = new AuthTriple(SUBJECT_USER1, ACTION_VALIDATE, OBJECT_RESOURCE1);
        authRepository.save(triple);

        triple = new AuthTriple(SUBJECT_USER2, ACTION_WRITE, OBJECT_RESOURCE1);
        authRepository.save(triple);
        triple = new AuthTriple(SUBJECT_USER2, ACTION_READ, OBJECT_RESOURCE1);
        authRepository.save(triple);

        triple = new AuthTriple(SUBJECT_USER2, ACTION_WRITE, OBJECT_RESOURCE2);
        authRepository.save(triple);
        triple = new AuthTriple(SUBJECT_USER2, ACTION_READ, OBJECT_RESOURCE2);
        authRepository.save(triple);
    }

    @Test
    @Order(3)
    void findEntriesBySubject() {
        List<AuthTriple> list = authRepository.findBySubject("user1");
        assertEquals("user2 triples = 4", 4, list.size());

        list = authRepository.findBySubject("user2");
        assertEquals("user2 triples = 4", 4, list.size());
    }

    @Test
    @Order(4)
    void entryExists() {
        boolean hasAccess = authorizationService.canDo(SUBJECT_USER2, ACTION_READ, OBJECT_RESOURCE1);
        assertEquals("user2 can read resource2 = true", true, hasAccess);
    }

    @Test
    @Order(5)
    void entryNotExists() {
        boolean hasAccess = authorizationService.canDo(SUBJECT_USER2, ACTION_VALIDATE, OBJECT_RESOURCE2);
        assertEquals("user2 can validate resource2 = false", false, hasAccess);
    }

    @Test
    @Order(6)
    void deleteEntries() {
        AuthTriple triple = authRepository.findBySubjectAndActionAndObject(SUBJECT_USER1, ACTION_READ, OBJECT_RESOURCE1).get();
        authRepository.delete(triple);
        triple = authRepository.findBySubjectAndActionAndObject(SUBJECT_USER1, ACTION_WRITE, OBJECT_RESOURCE1).get();
        authRepository.delete(triple);
        triple = authRepository.findBySubjectAndActionAndObject(SUBJECT_USER1, ACTION_MANAGE, OBJECT_RESOURCE1).get();
        authRepository.delete(triple);
        triple = authRepository.findBySubjectAndActionAndObject(SUBJECT_USER1, ACTION_VALIDATE, OBJECT_RESOURCE1).get();
        authRepository.delete(triple);

        triple = authRepository.findBySubjectAndActionAndObject(SUBJECT_USER2, ACTION_WRITE, OBJECT_RESOURCE1).get();
        authRepository.delete(triple);
        triple = authRepository.findBySubjectAndActionAndObject(SUBJECT_USER2, ACTION_READ, OBJECT_RESOURCE1).get();
        authRepository.delete(triple);

        triple = authRepository.findBySubjectAndActionAndObject(SUBJECT_USER2, ACTION_WRITE, OBJECT_RESOURCE2).get();
        authRepository.delete(triple);
        triple = authRepository.findBySubjectAndActionAndObject(SUBJECT_USER2, ACTION_READ, OBJECT_RESOURCE2).get();
        authRepository.delete(triple);
    }
}
