package in.lms.sinchan.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.lms.sinchan.email.EmailService;
import in.lms.sinchan.entity.Role;
import in.lms.sinchan.entity.Student;
import in.lms.sinchan.entity.Tenant;
import in.lms.sinchan.exception.InvalidInput;
import in.lms.sinchan.exception.RoleNotFoundException;
import in.lms.sinchan.exception.StudentNotFoundException;
import in.lms.sinchan.exception.TenantNotFoundException;
import in.lms.sinchan.model.request.StudentRequest;
import in.lms.sinchan.model.request.StudentUpdateRequest;
import in.lms.sinchan.model.response.StudentResponse;
import in.lms.sinchan.repository.RoleRepository;
import in.lms.sinchan.repository.StudentRepository;
import in.lms.sinchan.repository.TenantRepository;
import in.lms.sinchan.service.StudentService;
import in.lms.sinchan.util.OTPGeneration;

@Component
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OTPGeneration otpGeneration;

    @Override
    public StudentResponse persist(StudentRequest studentRequest) throws Exception {
        List<String> msg = new ArrayList<String>();
        if (!StringUtils.isNullOrEmpty(studentRequest.getParentTenant())) {
            Optional<Tenant> tenant = tenantRepository.findById(studentRequest.getParentTenant());
            if (ObjectUtils.isEmpty(tenant)) {
                throw new TenantNotFoundException(
                                "Tenant does not exist. Please create tenant first.");
            }
        }
        if (StringUtils.isNullOrEmpty(studentRequest.getEmail())
                        && StringUtils.isNullOrEmpty(studentRequest.getPhone())) {
            throw new InvalidInput("Either eamil or mobile number must be present");
        } else {
            String emailMsg = emailService.sendMail(new ModelMap()
                            .addAttribute("to", studentRequest.getEmail())
                            .addAttribute("subject", "OTP For Email Verification.")
                            .addAttribute("body", "Hi your otp for email verification is :"
                                            + otpGeneration.generateOTP()));
            if (emailMsg.equals("Mail sent successfully")) {
                msg.add("Please verify your email by entering the otp sent the registered emailId.");
            }
        }
        Student student = new Student();
        student.setAddress(studentRequest.getAddress());
        student.setDob(studentRequest.getDob());
        student.setEmail(studentRequest.getEmail());
        student.setFirstName(studentRequest.getFirstName());
        student.setLastName(studentRequest.getLastName());
        student.setParentTenant(studentRequest.getParentTenant());
        student.setPhone(studentRequest.getPhone());
        if (!StringUtils.isNullOrEmpty(studentRequest.getRole())) {
            Optional<Role> role = roleRepository.findById(studentRequest.getRole());
            if (!ObjectUtils.isEmpty(role) && role.isPresent()) {
                student.setRole(role.get().getId());
            }
        } else {
            new RoleNotFoundException("Role not found. Please create a role first.");
        }
        studentRepository.save(student);
        StudentResponse studentResponse = new StudentResponse();
        studentResponse.setStudentId(student.getStudentId());
        studentResponse.setMsg(msg);
        return studentResponse;
    }

    @Override
    public Student getStudentDetails(String id) throws Exception {
        Student student = null;
        if (!StringUtils.isNullOrEmpty(id)) {
            student = studentRepository.findStudentByStudentId(id);
            if (!ObjectUtils.isEmpty(student)) {
                return student;
            } else {
                throw new StudentNotFoundException("Student details does not exit with id : " + id);
            }
        } else {
            throw new InvalidInput("Id must not be null or empty");
        }
    }

    @Override
    public List<Student> getAllStudentDetails() {
        return studentRepository.findAll();
    }

    @Override
    public void deleteStudent(String id) throws Exception {
        Student student = getStudentDetails(id);
        studentRepository.delete(student);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> updateStudent(StudentUpdateRequest studentUpdateRequest, String id)
                    throws Exception {
        List<String> response = new ArrayList<>();
        Student student = getStudentDetails(id);
        JSONObject studentFromDB =
                        (JSONObject) new JSONParser()
                                        .parse(new ObjectMapper().writeValueAsString(student));
        JSONObject studentFromPayload = (JSONObject) new JSONParser()
                        .parse(new ObjectMapper().writeValueAsString(studentUpdateRequest));
        for (Object obj : studentFromPayload.keySet()) {
            String param = (String) obj;
            if (param.equalsIgnoreCase("email")) {
                studentFromDB.put(param, studentFromPayload.get(param));
                String emailMsg = emailService.sendMail(new ModelMap()
                                .addAttribute("to", (Student) studentFromPayload.get(param))
                                .addAttribute("subject", "OTP For Email Verification.")
                                .addAttribute("body", "Hi your otp for email verification is :"
                                                + otpGeneration.generateOTP()));
                if (emailMsg.equals("Mail sent successfully")) {
                    response.add("Please verify your email by entering the otp sent the registered emailId.");
                }
            } else {
                studentFromDB.put(param, studentFromPayload.get(param));
            }
        }
        Student updatedStudentDetails =
                        new ObjectMapper().readValue(studentFromDB.toJSONString(), Student.class);
        updatedStudentDetails.setEligibleToIssueBook(false);
        studentRepository.save(updatedStudentDetails);
        return response;
    }

}
