package in.lms.sinchan.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import in.lms.sinchan.entity.Librarian;
import in.lms.sinchan.model.request.LibrarianCreateRequest;
import in.lms.sinchan.model.request.LibrarianUpdateRequest;
import in.lms.sinchan.model.response.LibrarianCreateResponse;
import in.lms.sinchan.service.LibrarianService;

@RestController("librarianController")
@RequestMapping(value = "/librarian")
public class LibrarianController {

    @Autowired
    private LibrarianService librarianService;

    @PostMapping(value = "/create")
    public ResponseEntity<?> persistLibrarianDetailsInDB(
                    @RequestBody LibrarianCreateRequest librarianCreateReqest) {
        LibrarianCreateResponse librarianResponse =
                        librarianService.saveLibrarianDetails(librarianCreateReqest);
        return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ModelMap().addAttribute("", librarianResponse));
    }

    @GetMapping(value = "/get")
    public ResponseEntity<?> getLibrarianById(
                    @PathVariable(value = "id", required = true) String id) {
        Librarian libraian = librarianService.getLibrarianById(id);
        return ResponseEntity.status(HttpStatus.OK)
                        .body(new ModelMap().addAttribute("msg", libraian));
    }

    @GetMapping(value = "/getAll")
    public ResponseEntity<?> getAllLibrarian() {
        List<Librarian> listOfLibrarian = librarianService.getAllLibrarian();
        return ResponseEntity.status(HttpStatus.OK)
                        .body(new ModelMap().addAttribute("msg", listOfLibrarian));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteLibrarian(
                    @PathVariable(value = "id", required = true) String id) {
        librarianService.deleteLibrarian(id);
        return ResponseEntity.status(HttpStatus.OK)
                        .body(new ModelMap().addAttribute("msg", "Successfully Deleted"));
    }


    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateLibrarianDetails(
                    @RequestBody LibrarianUpdateRequest librarianUpdateRequest,
                    @PathVariable(value = "id", required = true) String id) {
        librarianService.updateLibrarianDetails(librarianUpdateRequest, id);
        return ResponseEntity.status(HttpStatus.OK)
                        .body(new ModelMap().addAttribute("msg", "Successfully updated"));
    }
}
