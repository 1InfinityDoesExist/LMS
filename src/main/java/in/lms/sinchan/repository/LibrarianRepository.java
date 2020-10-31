package in.lms.sinchan.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import in.lms.sinchan.entity.Librarian;

@Repository
public interface LibrarianRepository extends MongoRepository<Librarian, String> {

}
