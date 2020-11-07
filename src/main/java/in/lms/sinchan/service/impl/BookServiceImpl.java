package in.lms.sinchan.service.impl;

import java.util.Date;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.lms.sinchan.email.EmailService;
import in.lms.sinchan.entity.BIRD;
import in.lms.sinchan.entity.Book;
import in.lms.sinchan.entity.Student;
import in.lms.sinchan.exception.BookDoesNotExistException;
import in.lms.sinchan.exception.BookNotPersistedInDB;
import in.lms.sinchan.exception.NotEligible;
import in.lms.sinchan.exception.StudentNotFoundException;
import in.lms.sinchan.lmsProducer.LmsProducer;
import in.lms.sinchan.model.request.BIRDRequest;
import in.lms.sinchan.model.request.BookCreateRequest;
import in.lms.sinchan.model.request.BookUpdateRequest;
import in.lms.sinchan.model.response.BookCreateResponse;
import in.lms.sinchan.repository.BIRDRepository;
import in.lms.sinchan.repository.BookRepository;
import in.lms.sinchan.repository.StudentRepository;
import in.lms.sinchan.service.BookService;
import in.lms.sinchan.util.Constants;
import lombok.extern.slf4j.Slf4j;

@Component("bookServiceImpl")
@Slf4j
public class BookServiceImpl implements BookService {

    private static final String topic = "bookTopic";

    @Autowired
    @Qualifier("bookCache")
    private Cache cache;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LmsProducer lmsProducer;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private BIRDRepository birdRepository;

    @Autowired
    private EmailService emailService;


    @Value("${reminderto.return.book}")
    private String reminder;

    @Override
    public BookCreateResponse persistBookInDB(BookCreateRequest bookCreateRequest)
                    throws Exception {
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
        book.setGener(bookCreateRequest.getGener());
        bookRepository.save(book);
        /*
         * Calling Kafka producer for notification
         */
        if (!StringUtils.isNullOrEmpty(book.getId())) {
            lmsProducer.produce(topic, book);
        } else {
            throw new BookNotPersistedInDB("Could not persist book details in db.");
        }
        BookCreateResponse bookCreateResponse = new BookCreateResponse();
        bookCreateResponse.setBookId(book.getId());
        bookCreateResponse.setMsg("Successfully created");
        log.info(":::::BookCreateResponse : {}", bookCreateResponse);
        return bookCreateResponse;
    }

    // @Cacheable(value = "bookDetails", key = "#id", condition = "#id != 'rocky'")
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

    // @Cacheable(value = "allBooks")
    @Override
    public List<Book> getAllBooks() {
        log.info("-----BookServiceImpl Class, getAllBooks method-----");
        return bookRepository.findAll();
    }

    // @CacheEvict(value = "bookDetails", key = "#id")
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
    // @CachePut(value = "bookDetails", key = "#id")
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

    // @CacheEvict(value = "bookdDetails", allEntries = true)
    @Override
    public void clearCache() {
        // TODO Auto-generated method stub

    }

    @Override
    public void issueBookToStudent(BIRDRequest birdRequest) throws Exception {
        log.info("-----BookServiceImpl Class, issueBookToStudent method-----");
        Student student = studentRepository.findStudentById(birdRequest.getIssuedBy());
        if (!ObjectUtils.isEmpty(student)) {
            if (student.isEligibleToIssueBook()) {
                BIRD bird = new BIRD();
                bird.setBookId(birdRequest.getBookId());
                bird.setIssuedBy(birdRequest.getIssuedBy());
                bird.setIssuedDate(new Date());
                bird.setIssuedExpiryDate(10);
                bird.setIssuerId(birdRequest.getIssuerId());
                birdRepository.save(bird);
                long numberOfBooksIssued = student.getLibraryDetails().stream()
                                .filter(p -> p.isActive() == true ? true : false).count();
                student.setEligibleToIssueBook(numberOfBooksIssued <= 3 ? true : false);
                student.getLibraryDetails().add(bird);
                studentRepository.save(student);
                log.info(":::::student libraryDetails {}", student.getLibraryDetails());
            } else {
                throw new NotEligible("Sutdent with id: " + student.getId()
                                + " not eligible to issue any sort of book from LMS");
            }
        } else {
            throw new StudentNotFoundException(
                            "Student with id : " + birdRequest.getIssuedBy() + " does not exist");
        }
    }


    /*
     * Cron job to remind the last date to return the books
     */
    @Scheduled(cron = "${reminderto.return.book}")
    public void reminderToReturnBook() {
        log.info(":::::Cron job to remind issue date going to be expired today");
        List<BIRD> listOfBird = birdRepository.findBooksByIssuedExpiryDate(new Date());
        listOfBird.stream().forEach(b -> {
            Student student = studentRepository.findStudentById(b.getIssuedBy());
            emailService.sendMail(new ModelMap().addAttribute(Constants.TO, student.getEmail())
                            .addAttribute(Constants.SUBJECT,
                                            "Reminder to return LMS's issued books")
                            .addAttribute(Constants.BODY, " Hi, " + student.getFirstName()
                                            + "Please return book with Id: "
                                            + b.getBookId()
                                            + " as today the last for returning the book. Thanks"));
        });
    }

    /*
     * Cron to remind that the issue date has been expired and fine has been added.
     */
    @Scheduled(fixedDelayString = "${reminder.time:900000}")
    public void reminderToReturnBookAfterExpireyDate() {
        log.info(":::::Cron job to remind issue date has been expired and fine has been added");
        List<BIRD> listOfBird = birdRepository.findAllBIRD();
        listOfBird.stream().filter(bird -> {
            log.info(":::::bird {}", bird);
            return bird.isIssuedDateExpired();
        }).forEach(b -> {
            Student student = studentRepository.findStudentById(b.getIssuedBy());
            log.info(":::::student {}", student);
            emailService.sendMail(new ModelMap()
                            .addAttribute(Constants.TO, student.getEmail())
                            .addAttribute(Constants.SUBJECT,
                                            "Reminder to return LMS's issued books, time has been expired and fine has been added.")
                            .addAttribute(Constants.BODY, " Hi, "
                                            + student.getFirstName()
                                            + "Please return book with Id: "
                                            + b.getBookId()
                                            + " as today the last for returning the book. Thanks"));
        });
    }

    @Override
    public void returnBookToLMS(BIRDRequest birdRequest) {
        // TODO Auto-generated method stub

    }


}
