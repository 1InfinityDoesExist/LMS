package in.lms.sinchan.service;

import java.util.List;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import in.lms.sinchan.entity.Book;
import in.lms.sinchan.model.request.BIRDRequest;
import in.lms.sinchan.model.request.BookCreateRequest;
import in.lms.sinchan.model.request.BookUpdateRequest;
import in.lms.sinchan.model.response.BookCreateResponse;

@Service
public interface BookService {

    public BookCreateResponse persistBookInDB(BookCreateRequest bookCreateRequest) throws Exception;

    public Book getBookDetails(String id) throws Exception;

    public List<Book> getAllBooks();

    public void deleteBookDetails(String id) throws Exception;

    public void updateBookDetails(BookUpdateRequest bookUpdateRequest, String id) throws Exception;

    public void clearCache();

    public String issueBookToStudent(BIRDRequest birdRequest) throws Exception;

    public void returnBookToLMS(BIRDRequest birdRequest) throws Exception;

    public List<Book> getAvailableBooks();

    public List<String> getListOfIssuedBooks();

}
