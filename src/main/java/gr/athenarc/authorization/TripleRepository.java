package gr.athenarc.authorization;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripleRepository extends CrudRepository<AuthTriple, Long> {

}
