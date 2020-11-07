package in.lms.sinchan.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import in.lms.sinchan.entity.Book;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {

    @Query(value = "{'isActive:true}")
    public Book findBookById(String id);

    @Query(value = "{'isActive:true, 'isAvailable'=true}")
    public List<Book> findBookByIsActiveAndIsAvailable(boolean b, boolean c);

}
