package in.lms.sinchan.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import in.lms.sinchan.entity.Student;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {

    public Student findStudentByStudentId(String id);

}
