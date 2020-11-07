package in.lms.sinchan.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.lms.sinchan.aws.AWSOperations;
import in.lms.sinchan.email.EmailService;
import in.lms.sinchan.entity.BIRD;
import in.lms.sinchan.entity.OtpDetails;
import in.lms.sinchan.entity.Role;
import in.lms.sinchan.entity.Student;
import in.lms.sinchan.entity.Tenant;
import in.lms.sinchan.exception.InvalidInput;
import in.lms.sinchan.exception.RoleNotFoundException;
import in.lms.sinchan.exception.StudentNotFoundException;
import in.lms.sinchan.exception.TenantNotFoundException;
import in.lms.sinchan.model.OtpVerificationDetails;
import in.lms.sinchan.model.request.StudentRequest;
import in.lms.sinchan.model.request.StudentUpdateRequest;
import in.lms.sinchan.model.response.StudentResponse;
import in.lms.sinchan.repository.OtpDetailsRepository;
import in.lms.sinchan.repository.RoleRepository;
import in.lms.sinchan.repository.StudentRepository;
import in.lms.sinchan.repository.TenantRepository;
import in.lms.sinchan.service.StudentService;
import in.lms.sinchan.util.OTPGeneration;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
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

    @Autowired
    private AWSOperations awsOperations;

    @Autowired
    private OtpDetailsRepository otpDetailsRepository;

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
        int emailOtp = otpGeneration.generateOTP();
        int mobileOtp = otpGeneration.generateOTP();

        if (StringUtils.isNullOrEmpty(studentRequest.getEmail())
                        && StringUtils.isNullOrEmpty(studentRequest.getPhone())) {
            throw new InvalidInput("Either eamil or mobile number must be present");
        } else {
            log.info(":::::About to send otp via email.");
            String emailMsg = emailService.sendMail(new ModelMap()
                            .addAttribute("to", studentRequest.getEmail())
                            .addAttribute("subject", "OTP For Email Verification.")
                            .addAttribute("body", "Hi your otp for email verification is :"
                                            + emailOtp));
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
        log.info("::::::student Id {}", student.getId());
        OtpDetails otpDetails = new OtpDetails();
        otpDetails.setId(student.getId());
        otpDetails.setEmailOtp(emailOtp);
        otpDetails.setMobileOtp(mobileOtp);
        otpDetails.setEmailOtpExpiryDate(10);
        otpDetails.setMobileOtpExpiryDate(10);
        otpDetailsRepository.save(otpDetails);
        log.info(":::::otpDetails Id {}", otpDetails.getId());
        StudentResponse studentResponse = new StudentResponse();
        studentResponse.setStudentId(student.getId());
        studentResponse.setMsg(msg);
        return studentResponse;
    }

    @Override
    public Student getStudentDetails(String id) throws Exception {
        Student student = null;
        if (!StringUtils.isNullOrEmpty(id)) {
            student = studentRepository.findStudentById(id);
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

    /*
     * Verify Student Email
     */

    @Override
    public List<String> verifyStudentEmailAndMobile(OtpVerificationDetails otpVerifyDetails)
                    throws Exception {
        List<String> response = new ArrayList<>();
        if (!ObjectUtils.isEmpty(otpVerifyDetails)) {
            if (ObjectUtils.isEmpty(otpVerifyDetails.getEmail())
                            && ObjectUtils.isEmpty(otpVerifyDetails.getMobile())) {
                throw new InvalidInput("Either email or phone must be present.");
            } else {
                Student student = studentRepository.findStudentByEmailAndPhone(
                                otpVerifyDetails.getEmail(), otpVerifyDetails.getMobile());
                if (ObjectUtils.isEmpty(student)) {
                    throw new StudentNotFoundException(String.format(
                                    "Student not found with email %s and phone %s",
                                    otpVerifyDetails.getEmail(), otpVerifyDetails.getMobile()));
                } else {
                    OtpDetails otpDetails =
                                    otpDetailsRepository.findOtpDetailsById(student.getId());
                    if (student.isEmailVerified()) {
                        response.add("EmailId already verified");
                    } else {
                        if (otpDetails.isEmailOtpExpired()) {
                            response.add("Otp has been expired. Please request for a new otp");
                        } else {
                            if (otpDetails.getEmailOtp().equals(otpVerifyDetails.getEmailOtp())) {
                                student.setEmailVerified(true);
                                studentRepository.save(student);
                                response.add("Email successfully verified.");
                            } else {
                                response.add("Otp is incorrect, Please insert the correct otp sent to u via email.");
                            }
                        }
                    }
                    if (student.isMobileVerified()) {
                        response.add("Phone number already verified.");
                    } else {
                        if (otpDetails.isMobileOtpExpired()) {
                            response.add("Otp has been expired. Please request for a new otp");
                        } else {
                            if (otpDetails.getMobileOtp().equals(otpVerifyDetails.getMobileOtp())) {
                                student.setMobileVerified(true);
                                studentRepository.save(student);
                                response.add("Phone number successfully verified.");
                            } else {
                                response.add("Otp is incorrect. Please insert correct  mobile otp send to u via sms.");
                            }
                        }
                    }

                }
            }
        } else {
            throw new InvalidInput("Invalid input.");
        }
        return response;

    }

    /*
     * Upload Profile Image of the Student
     */
    @Override
    public String uploadImageUrl(MultipartFile image, String email) throws Exception {
        return awsOperations.uploadProfilePic(image, "imageProfile", email);
    }


    @Override
    public List<String> getStudentCurrentIssuedBooksList(String id) throws Exception {
        Student student = studentRepository.findStudentById(id);
        if (ObjectUtils.isEmpty(student)) {
            throw new StudentNotFoundException("Student does not exist with id : " + id);
        } else {
            List<String> books = student.getLibraryDetails().stream()
                            .filter(b -> b.isActive() == true ? true : false)
                            .map(BIRD::getBookId).collect(Collectors.toList());
            return books;
        }
    }

    @Override
    public List<BIRD> getLMSHistory(String id) throws Exception {
        Student student = studentRepository.findStudentById(id);
        if (ObjectUtils.isEmpty(student)) {
            throw new StudentNotFoundException("Student does not exist with id : " + id);
        } else {
            return student.getLibraryDetails();
        }
    }

    @Override
    public List<String> getAllProfileImages(String email) throws Exception {
        return awsOperations.getAllProfileImages(email, "imageProfile");
    }

    @Override
    public void deleteProfileImage(String email, String image) {
        awsOperations.deleteProfileImage(email, image, "imageProfile");

    }
}
