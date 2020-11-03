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
import org.springframework.web.bind.annotation.RestController;
import in.lms.sinchan.entity.Student;
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
        StudentResponse studentResponse = studentService.persist(studentRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ModelMap().addAttribute("msg", "Successfully Created")
                                        .addAttribute("response", studentResponse.getMsg()));
    }

    @GetMapping(value = "/get/{id}")
    public ResponseEntity<?> getStudentDetails(
                    @PathVariable(value = "id", required = true) String id) throws Exception {
        Student student = studentService.getStudentDetails(id);
        return ResponseEntity.status(HttpStatus.OK)
                        .body(new ModelMap().addAttribute("response", student));
    }

    @GetMapping(value = "/getAll")
    public ResponseEntity<?> getAllStudentRecords() throws Exception {
        List<Student> listOfStudents = studentService.getAllStudentDetails();
        return ResponseEntity.status(HttpStatus.OK)
                        .body(new ModelMap().addAttribute("response", listOfStudents));
    }

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


}
