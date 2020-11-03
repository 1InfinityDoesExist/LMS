package in.lms.sinchan.service;

import java.util.List;
import org.springframework.stereotype.Service;
import in.lms.sinchan.entity.Librarian;
import in.lms.sinchan.model.request.LibrarianCreateRequest;
import in.lms.sinchan.model.request.LibrarianUpdateRequest;
import in.lms.sinchan.model.response.LibrarianCreateResponse;

@Service
public interface LibrarianService {

    public LibrarianCreateResponse saveLibrarianDetails(
                    LibrarianCreateRequest librarianCreateReqest) throws Exception;

    public Librarian getLibrarianById(String id) throws Exception;

    public List<Librarian> getAllLibrarian();

    public void deleteLibrarian(String id) throws Exception;

    public void updateLibrarianDetails(LibrarianUpdateRequest librarianUpdateRequest, String id)
                    throws Exception;

}
