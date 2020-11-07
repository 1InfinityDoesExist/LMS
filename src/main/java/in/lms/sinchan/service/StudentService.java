package in.lms.sinchan.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import in.lms.sinchan.entity.BIRD;
import in.lms.sinchan.entity.Book;
import in.lms.sinchan.entity.Student;
import in.lms.sinchan.exception.StudentNotFoundException;
import in.lms.sinchan.model.OtpVerificationDetails;
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

    public String uploadImageUrl(MultipartFile image, String studentId) throws Exception;

    public List<String> verifyStudentEmailAndMobile(OtpVerificationDetails otpVerifyDetails)
                    throws Exception;

    public List<String> getStudentCurrentIssuedBooksList(String id) throws Exception;

    public List<BIRD> getLMSHistory(String id) throws Exception;

    public List<String> getAllProfileImages(String email) throws Exception;

    public void deleteProfileImage(String email, String image);

}
