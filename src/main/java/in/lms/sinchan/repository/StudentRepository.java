package in.lms.sinchan.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import in.lms.sinchan.entity.Student;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {

    @Query(value = "{'isActive':true}")
    public Student findStudentById(String id);

    @Query(value = "{'isActive':false, 'email':?0, 'phone':?1}")
    public Student findStudentByEmailAndPhone(String email, String mobile);

    @Query(value = "{'mostAwatedBooks':{'$in':?0}}, 'isActive':true")
    public List<Student> findStudentByBookId(String bookId);

    @Query(value = "{'isActive':true, 'email':?0}")
    public Student findStudentByEmail(String email);

}
