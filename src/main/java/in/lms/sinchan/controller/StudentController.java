package in.lms.sinchan.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import in.lms.sinchan.entity.Student;
import in.lms.sinchan.exception.InvalidInput;
import in.lms.sinchan.exception.RoleNotFoundException;
import in.lms.sinchan.exception.StudentNotFoundException;
import in.lms.sinchan.exception.TenantNotFoundException;
import in.lms.sinchan.model.OtpVerificationDetails;
import in.lms.sinchan.model.request.StudentRequest;
import in.lms.sinchan.model.request.StudentUpdateRequest;
import in.lms.sinchan.model.response.StudentResponse;
import in.lms.sinchan.service.StudentService;

@RestController("studentController")
@RequestMapping(value = "/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> saveStudent(@RequestBody StudentRequest studentRequest)
                    throws Exception {
        try {
            StudentResponse studentResponse = studentService.persist(studentRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                            .body(new ModelMap().addAttribute("msg", "Successfully Created")
                                            .addAttribute("response", studentResponse.getMsg()));
        } catch (final TenantNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ModelMap().addAttribute("msg", ex.getMessage()));
        } catch (final InvalidInput ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ModelMap().addAttribute("msg", ex.getMessage()));
        } catch (final RoleNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ModelMap().addAttribute("msg", ex.getMessage()));
        }
    }

    @GetMapping(value = "/get/{id}")
    public ResponseEntity<?> getStudentDetails(
                    @PathVariable(value = "id", required = true) String id) throws Exception {
        try {
            Student student = studentService.getStudentDetails(id);
            return ResponseEntity.status(HttpStatus.OK)
                            .body(new ModelMap().addAttribute("response", student));
        } catch (final StudentNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ModelMap().addAttribute("msg", ex.getMessage()));
        } catch (final InvalidInput ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ModelMap().addAttribute("msg", ex.getMessage()));
        }

    }

    @GetMapping(value = "/getAll")
    public ResponseEntity<?> getAllStudentRecords() throws Exception {
        List<Student> listOfStudents = studentService.getAllStudentDetails();
        return ResponseEntity.status(HttpStatus.OK)
                        .body(new ModelMap().addAttribute("response", listOfStudents));
    }

    /*
     * Check for Book Return and dues
     */
    @GetMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteStudentRecords(
                    @PathVariable(value = "id", required = true) String id) throws Exception {
        studentService.deleteStudent(id);
        return ResponseEntity.status(HttpStatus.OK)
                        .body(new ModelMap().addAttribute("msg", "Successfully deleted."));
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateStudent(@RequestBody StudentUpdateRequest studentUpdateRequest,
                    @PathVariable(value = "id", required = true) String id) throws Exception {
        List<String> response = studentService.updateStudent(studentUpdateRequest, id);
        return ResponseEntity.status(HttpStatus.OK)
                        .body(new ModelMap().addAttribute("response", response));
    }


    @PostMapping(value = "/uploadImage/{email}")
    public ResponseEntity<?> uploadImage(
                    @RequestParam(value = "file", required = true) MultipartFile image,
                    @PathVariable(value = "email", required = true) String email) throws Exception {
        String imageUrl = studentService.uploadImageUrl(image, email);
        return ResponseEntity.status(HttpStatus.OK)
                        .body(new ModelMap().addAttribute("msg", "Successfully uploaded image.")
                                        .addAttribute("image", imageUrl));
    }

    @PostMapping(value = "/verify")
    public ResponseEntity<?> verifyStudentEmail(
                    @RequestBody OtpVerificationDetails otpVerifyDetails) throws Exception {
        try {
            List<String> response = studentService.verifyStudentEmailAndMobile(otpVerifyDetails);
            return ResponseEntity.status(HttpStatus.OK)
                            .body(new ModelMap().addAttribute("msg", response));
        } catch (final StudentNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ModelMap().addAttribute("msg", ex.getMessage()));
        } catch (final InvalidInput ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ModelMap().addAttribute("msg", ex.getMessage()));
        }
    }
}
