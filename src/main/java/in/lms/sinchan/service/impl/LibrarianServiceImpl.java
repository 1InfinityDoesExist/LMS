package in.lms.sinchan.service.impl;

import java.util.Date;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.lms.sinchan.email.EmailService;
import in.lms.sinchan.entity.Librarian;
import in.lms.sinchan.entity.Role;
import in.lms.sinchan.exception.InvalidInput;
import in.lms.sinchan.exception.LibrarianNotFound;
import in.lms.sinchan.exception.MailNotSentException;
import in.lms.sinchan.exception.RoleNotFoundException;
import in.lms.sinchan.model.request.LibrarianCreateRequest;
import in.lms.sinchan.model.request.LibrarianUpdateRequest;
import in.lms.sinchan.model.response.LibrarianCreateResponse;
import in.lms.sinchan.repository.LibrarianRepository;
import in.lms.sinchan.repository.RoleRepository;
import in.lms.sinchan.service.LibrarianService;
import in.lms.sinchan.util.OTPGeneration;

@Component
public class LibrarianServiceImpl implements LibrarianService {

    @Autowired
    private LibrarianRepository librarianRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OTPGeneration otpGeneration;

    @Override
    public LibrarianCreateResponse saveLibrarianDetails(
                    LibrarianCreateRequest librarianCreateReqest) throws Exception {
        if (!ObjectUtils.isEmpty(librarianCreateReqest)
                        && ObjectUtils.isEmpty(librarianCreateReqest.getEmail())
                        && ObjectUtils.isEmpty(librarianCreateReqest.getPhone())) {
            throw new InvalidInput("Either email or phone must be present");
        }
        Librarian librarian = new Librarian();
        if (!ObjectUtils.isEmpty(librarianCreateReqest.getRole())) {
            Role role = roleRepository.findRoleByName(librarianCreateReqest.getRole());
            if (!ObjectUtils.isEmpty(role)) {
                librarian.setRole(role.getId());
            } else {
                throw new RoleNotFoundException("Role does not exist with name : "
                                + librarianCreateReqest.getRole() + ". Please create a role first");
            }
        } else {
            throw new InvalidInput("Role field must not be null or empty");
        }
        librarian.setDateOfJoining(new Date());
        if (!ObjectUtils.isEmpty(librarianCreateReqest.getEmail())) {
            String emailResponse = emailService.sendMail(
                            new ModelMap().addAttribute("to", librarianCreateReqest.getEmail())
                                            .addAttribute("subject", "Email Verification OTP")
                                            .addAttribute("body", otpGeneration.generateOTP()));
            if (emailResponse.equals("Mail sent successfully")) {
                librarian.setEmail(librarianCreateReqest.getEmail());
            } else {
                throw new MailNotSentException("Could not sent email to verify the otp");
            }
        } else {
            throw new InvalidInput("EmailId field cannot be blank or empty");
        }
        librarian.setFirstName(librarianCreateReqest.getFirstName());
        librarian.setLastName(librarianCreateReqest.getLastName());
        librarian.setPhone(librarianCreateReqest.getPhone());
        librarianRepository.save(librarian);
        LibrarianCreateResponse response = new LibrarianCreateResponse();
        response.setId(librarian.getId());
        response.setMsg("Successfully persisted librarian details in db.");
        return response;
    }

    @Override
    public Librarian getLibrarianById(String id) throws Exception {
        Librarian librarian = librarianRepository.findLibrarianById(id);
        if (!ObjectUtils.isEmpty(librarian)) {
            return librarian;
        } else {
            throw new LibrarianNotFound("No librarian exist with id : " + id);
        }
    }

    @Override
    public List<Librarian> getAllLibrarian() {
        return librarianRepository.findAll();
    }

    @Override
    public void deleteLibrarian(String id) throws LibrarianNotFound {
        Librarian librarian = librarianRepository.findLibrarianById(id);
        if (!ObjectUtils.isEmpty(librarian)) {
            librarianRepository.delete(librarian);
            return;
        } else {
            throw new LibrarianNotFound("No librarian exist with id : " + id);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateLibrarianDetails(LibrarianUpdateRequest librarianUpdateRequest, String id)
                    throws LibrarianNotFound, JsonProcessingException, ParseException {
        Librarian librarian = librarianRepository.findLibrarianById(id);
        if (!ObjectUtils.isEmpty(librarian)) {
            JSONObject librarianFromDB = (JSONObject) new JSONParser()
                            .parse(new ObjectMapper().writeValueAsString(librarian));
            JSONObject librarinFromPaylaod = (JSONObject) new JSONParser()
                            .parse(new ObjectMapper().writeValueAsString(librarianUpdateRequest));

            for (Object obj : librarinFromPaylaod.keySet()) {
                String param = (String) obj;
                librarianFromDB.put(param, librarinFromPaylaod.get(param));
            }
            librarianRepository.save(new ObjectMapper().readValue(librarianFromDB.toJSONString(),
                            Librarian.class));
            return;
        } else {
            throw new LibrarianNotFound("No librarian exist with id : " + id);
        }

    }

}
