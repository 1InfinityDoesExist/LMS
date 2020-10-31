package in.lms.sinchan.service;

import java.util.List;
import org.springframework.stereotype.Service;
import in.lms.sinchan.entity.Student;
import in.lms.sinchan.model.request.StudentRequest;
import in.lms.sinchan.model.request.StudentUpdateRequest;
import in.lms.sinchan.model.response.StudentResponse;

@Service
public interface StudentService {

    public StudentResponse persist(StudentRequest studentRequest) throws Exception;

    public Student getStudentDetails(String id) throws Exception;

    public List<Student> getAllStudentDetails() throws Exception;

    public void deleteStudent(String id) throws Exception;

    public List<String> updateStudent(StudentUpdateRequest studentUpdateRequest, String id)
                    throws Exception;

}
