package in.lms.sinchan.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import in.lms.sinchan.entity.Book;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {

    public Book findBookById(String id);

}
