package in.lms.sinchan.service.impl;

import java.util.Date;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.lms.sinchan.entity.Book;
import in.lms.sinchan.exception.BookDoesNotExistException;
import in.lms.sinchan.model.request.BookCreateRequest;
import in.lms.sinchan.model.request.BookUpdateRequest;
import in.lms.sinchan.model.response.BookCreateResponse;
import in.lms.sinchan.repository.BookRepository;
import in.lms.sinchan.service.BookService;

@Component
public class BookServiceImpl implements BookService {


    @Autowired
    private BookRepository bookRepository;

    @Override
    public BookCreateResponse persistBookInDB(BookCreateRequest bookCreateRequest) {
        Book book = new Book();
        book.setAuthor(bookCreateRequest.getAuthor());
        book.setAvailable(true);
        book.setAvailableOn(new Date());
        book.setBookName(bookCreateRequest.getBookName());
        book.setFinePerDay(bookCreateRequest.getFinePerDay());
        book.setIssued(false);
        book.setSection(bookCreateRequest.getSection());
        book.setVersion(bookCreateRequest.getVersion());
        bookRepository.save(book);
        BookCreateResponse bookCreateResponse = new BookCreateResponse();
        bookCreateResponse.setBookId(book.getBookId());
        bookCreateResponse.setMsg("Successfully created");
        return bookCreateResponse;
    }

    @Override
    public Book getBookDetails(String id) throws Exception {
        Book book = bookRepository.findBookByBookId(id);
        if (ObjectUtils.isEmpty(book)) {
            throw new BookDoesNotExistException(
                            "Book does not exist. Please persist book details first");
        }
        return book;
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public void deleteBookDetails(String id) throws Exception {
        Book book = bookRepository.findBookByBookId(id);
        if (ObjectUtils.isEmpty(book)) {
            throw new BookDoesNotExistException(
                            "Book does not exist. Please persist book details first");
        }
        bookRepository.delete(book);
        return;

    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateBookDetails(BookUpdateRequest bookUpdateRequest, String id) throws Exception {
        Book book = bookRepository.findBookByBookId(id);
        if (ObjectUtils.isEmpty(book)) {
            throw new BookDoesNotExistException(
                            "Book does not exist. Please persist book details first");
        }
        JSONObject bookFromDB = (JSONObject) new JSONParser()
                        .parse(new ObjectMapper().writeValueAsString(book));
        JSONObject bookFromPayload = (JSONObject) new JSONParser()
                        .parse(new ObjectMapper().writeValueAsString(bookUpdateRequest));
        for (Object obj : bookFromPayload.keySet()) {
            String param = (String) obj;
            bookFromDB.put(param, bookFromPayload.get(param));
        }
        bookRepository.save(new ObjectMapper().readValue(bookFromDB.toJSONString(), Book.class));
        return;
    }

}
