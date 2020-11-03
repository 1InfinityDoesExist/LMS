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

@Component("bookServiceImpl")
public class BookServiceImpl implements BookService {


    @Autowired
    @Qualifier("bookCache")
    private Cache cache;

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
        cache.put("books", book);
        BookCreateResponse bookCreateResponse = new BookCreateResponse();
        bookCreateResponse.setBookId(book.getBookId());
        bookCreateResponse.setMsg("Successfully created");
        return bookCreateResponse;
    }

    @Cacheable(value = "books", key = "#id", condition = "#id!=null", unless = "#result==null")
    @Override
    public Book getBookDetails(String id) throws Exception {
        Book book = bookRepository.findBookByBookId(id);
        if (ObjectUtils.isEmpty(book)) {
            throw new BookDoesNotExistException(
                            "Book does not exist. Please persist book details first");
        }
        return book;
    }

    @Cacheable(value = "books")
    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @CacheEvict(value = "books", key = "#id")
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

    @CachePut(value = "books", key = "#bookUpdateRequest.bookName", unless = "#result==null")
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

    @CacheEvict(value = "books", allEntries = true)
    @Override
    public void clearCache() {
        // TODO Auto-generated method stub

    }

}
