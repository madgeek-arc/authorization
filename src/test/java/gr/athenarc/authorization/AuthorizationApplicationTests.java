package gr.athenarc.authorization;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthorizationApplicationTests {

	@Autowired private TripleRepository tripleRepository;


	@Test
	void contextLoads() {
	}

	@Test
	@Transactional
	void testUpdateUsingTransactional() {
		// create entry
		AuthTriple triple = new AuthTriple("test", "write", "test");
		triple = tripleRepository.save(triple);

		Long id = triple.getId();

		// Update using setters
		triple.setSubject("user");
		triple.setAction("read");
		triple.setObject("resource");

		// retrieve result from db
		Optional<AuthTriple> resultOp = tripleRepository.findById(id);
		assertTrue(resultOp.isPresent());
		AuthTriple result = resultOp.get();

		// test triple-result values
		assertEquals("Subject: ", triple.getSubject(), result.getSubject());
		assertEquals("Action: ", triple.getAction(), result.getAction());
		assertEquals("Object: ", triple.getObject(), result.getObject());
	}
}
