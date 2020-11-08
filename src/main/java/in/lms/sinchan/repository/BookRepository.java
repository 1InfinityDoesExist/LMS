package in.lms.sinchan.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import in.lms.sinchan.entity.Book;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {

    @Query(value = "{'isActive':true, 'id':?0}")
    public Book findBookById(String id);

    @Query(value = "{'isActive':?0, 'isAvailable':?1}")
    public List<Book> findBookByIsActiveAndIsAvailable(boolean b, boolean c);

    @Query(value = "{'isIssued':true, 'isActive':true}")
    public List<Book> findBookByIsIssued(boolean b);

}
