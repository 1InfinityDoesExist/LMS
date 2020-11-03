package in.lms.sinchan.service.impl;

import java.util.Date;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
import lombok.extern.slf4j.Slf4j;

@Component("bookServiceImpl")
@Slf4j
public class BookServiceImpl implements BookService {


    @Autowired
    @Qualifier("bookCache")
    private Cache cache;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Override
    public BookCreateResponse persistBookInDB(BookCreateRequest bookCreateRequest) {
        log.info(":::::BookServiceImpl Class, persistBookInDB method:::::");
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
        bookCreateResponse.setBookId(book.getId());
        bookCreateResponse.setMsg("Successfully created");
        log.info(":::::BookCreateResponse : {}", bookCreateResponse);
        return bookCreateResponse;
    }

    @Cacheable(value = "bookDetails", key = "#id", condition = "#id != 'rocky'")
    @Override
    public Book getBookDetails(String id) throws Exception {
        log.info("-----BookServiceImpl Class, getBookDetails method-----");
        Book book = bookRepository.findBookById(id);
        if (ObjectUtils.isEmpty(book)) {
            throw new BookDoesNotExistException(
                            "Book does not exist. Please persist book details first");
        }
        return book;
    }

    @Cacheable(value = "allBooks")
    @Override
    public List<Book> getAllBooks() {
        log.info("-----BookServiceImpl Class, getAllBooks method-----");
        return bookRepository.findAll();
    }

    @CacheEvict(value = "bookDetails", key = "#id")
    @Override
    public void deleteBookDetails(String id) throws Exception {
        Book book = bookRepository.findBookById(id);
        if (ObjectUtils.isEmpty(book)) {
            throw new BookDoesNotExistException(
                            "Book does not exist. Please persist book details first");
        }
        bookRepository.delete(book);
        return;
    }

    @SuppressWarnings("unchecked")
    @CachePut(value = "bookDetails", key = "#id")
    @Override
    public void updateBookDetails(BookUpdateRequest bookUpdateRequest, String id) throws Exception {
        Book book = bookRepository.findBookById(id);
        if (ObjectUtils.isEmpty(book)) {
            throw new BookDoesNotExistException(
                            "Book does not exist. Please persist book details first");
        }
        JSONObject bookFromDB = (JSONObject) new JSONParser()
                        .parse(objectMapper.writeValueAsString(book));
        JSONObject bookFromPayload = (JSONObject) new JSONParser()
                        .parse(objectMapper.writeValueAsString(bookUpdateRequest));
        for (Object obj : bookFromPayload.keySet()) {
            String param = (String) obj;
            bookFromDB.put(param, bookFromPayload.get(param));
        }
        // objectMapper.setDateFormat(new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa"));
        bookRepository.save(objectMapper.readValue(bookFromDB.toJSONString(), Book.class));
        return;
    }

    @CacheEvict(value = "bookdDetails", allEntries = true)
    @Override
    public void clearCache() {
        // TODO Auto-generated method stub

    }

}
