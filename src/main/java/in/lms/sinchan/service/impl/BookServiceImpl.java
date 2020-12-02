package in.lms.sinchan.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
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
import in.lms.sinchan.entity.Librarian;
import in.lms.sinchan.entity.Student;
import in.lms.sinchan.exception.BookDoesNotExistException;
import in.lms.sinchan.exception.InvalidInput;
import in.lms.sinchan.exception.LibrarianNotFound;
import in.lms.sinchan.exception.NotEligible;
import in.lms.sinchan.exception.StudentNotFoundException;
import in.lms.sinchan.lmsProducer.LmsProducer;
import in.lms.sinchan.model.request.BIRDRequest;
import in.lms.sinchan.model.request.BookCreateRequest;
import in.lms.sinchan.model.request.BookUpdateRequest;
import in.lms.sinchan.model.response.BookCreateResponse;
import in.lms.sinchan.repository.BIRDRepository;
import in.lms.sinchan.repository.BookRepository;
import in.lms.sinchan.repository.LibrarianRepository;
import in.lms.sinchan.repository.StudentRepository;
import in.lms.sinchan.service.BookService;
import in.lms.sinchan.util.Constants;
import lombok.extern.slf4j.Slf4j;

@Component("bookServiceImpl")
@Slf4j
@CacheConfig(cacheNames = { "booksCache" })
public class BookServiceImpl implements BookService {

	private static final String topic = "bookTopic";

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

	@Autowired
	private LibrarianRepository librarianRepository;

	private CacheManager cacheManager;

	@Autowired
	public BookServiceImpl(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	@PostConstruct
	public void init() {
		cache = cacheManager.getCache("booksCache");
	}

	@Override
	public BookCreateResponse persistBookInDB(BookCreateRequest bookCreateRequest) throws Exception {
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
		book.setActive(true);
		bookRepository.save(book);
		cache.put(book.getId(), book);
		BookCreateResponse bookCreateResponse = new BookCreateResponse();
		bookCreateResponse.setBookId(book.getId());
		bookCreateResponse.setMsg("Successfully created");
		log.info(":::::BookCreateResponse : {}", bookCreateResponse);
		return bookCreateResponse;
	}

	@Cacheable(value = "booksCache", key = "#id", unless = "#result == null")
	@Override
	public Book getBookDetails(String id) throws Exception {
		log.info("-----BookServiceImpl Class, getBookDetails method-----");
		Book book = bookRepository.findBookById(id);
		if (ObjectUtils.isEmpty(book)) {
			throw new BookDoesNotExistException("Book does not exist. Please persist book details first");
		}
		return book;
	}

	@CachePut(value = "booksCache")
	@Override
	public List<Book> getAllBooks() {
		log.info("-----BookServiceImpl Class, getAllBooks method-----");
		return bookRepository.findAll();
	}

	@CacheEvict(value = "booksCache", key = "#id")
	@Override
	public void deleteBookDetails(String id) throws Exception {
		Book book = bookRepository.findBookById(id);
		if (ObjectUtils.isEmpty(book)) {
			throw new BookDoesNotExistException("Book does not exist. Please persist book details first");
		}
		bookRepository.delete(book);
		return;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateBookDetails(BookUpdateRequest bookUpdateRequest, String id) throws Exception {
		cache.evictIfPresent(id);
		Book book = bookRepository.findBookById(id);
		if (ObjectUtils.isEmpty(book)) {
			throw new BookDoesNotExistException("Book does not exist. Please persist book details first");
		}
		JSONObject bookFromDB = (JSONObject) new JSONParser().parse(objectMapper.writeValueAsString(book));
		JSONObject bookFromPayload = (JSONObject) new JSONParser()
				.parse(objectMapper.writeValueAsString(bookUpdateRequest));
		for (Object obj : bookFromPayload.keySet()) {
			String param = (String) obj;
			bookFromDB.put(param, bookFromPayload.get(param));
		}
		// objectMapper.setDateFormat(new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa"));
		Book updatedBook = bookRepository.save(objectMapper.readValue(bookFromDB.toJSONString(), Book.class));
		cache.putIfAbsent(id, updatedBook);
		return;
	}

	@Override
	public String issueBookToStudent(BIRDRequest birdRequest) throws Exception {
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
				bird.setActive(true);
				birdRepository.save(bird);
				long numberOfBooksIssued = student.getLibraryDetails().stream()
						.filter(p -> p.isActive() == true ? true : false).count();
				student.setEligibleToIssueBook(numberOfBooksIssued <= 3 ? true : false);
				student.getLibraryDetails().add(bird);
				/*
				 * X got the book he/she as looking for now he must not get the availability
				 * recommendation of that book.
				 */
				student.getMostAwatedBooks().removeIf(b -> b.equals(bird.getBookId()));
				studentRepository.save(student);

				Book book = bookRepository.findBookById(birdRequest.getBookId());
				book.setIssued(true);
				book.setAvailable(false);
				book.setAvailableOn(bird.getIssuedExpiryDate());
				bookRepository.save(book);
				log.info(":::::student libraryDetails {}", student.getLibraryDetails());

				return bird.getId();
			} else {
				throw new NotEligible(
						"Sutdent with id: " + student.getId() + " not eligible to issue any sort of book from LMS");
			}
		} else {
			throw new StudentNotFoundException("Student with id : " + birdRequest.getIssuedBy() + " does not exist");
		}
	}

	/*
	 * Cron job to remind the last date to return the books
	 */
	// @Scheduled(cron = "${reminderto.return.book}")
	@Scheduled(fixedDelayString = "${reminder.time:300000}")
	public void reminderToReturnBook() {
		log.info(":::::Cron job to remind issue date going to be expired today");
		List<BIRD> listOfBird = birdRepository.findBooksByIssuedExpiryDate(new Date());
		listOfBird.stream().forEach(b -> {
			Student student = studentRepository.findStudentById(b.getIssuedBy());
			emailService.sendMail(new ModelMap().addAttribute(Constants.TO, student.getEmail())
					.addAttribute(Constants.SUBJECT, "Reminder to return LMS's issued books")
					.addAttribute(Constants.BODY, " Hi, " + student.getFirstName() + "Please return book with Id: "
							+ b.getBookId() + " as today the last for returning the book. Thanks"));
		});
	}

	/*
	 * Cron to remind that the issue date has been expired and fine has been added.
	 */
	@Scheduled(fixedDelayString = "${reminder.time:300000}")
	public void reminderToReturnBookAfterExpireyDate() {
		log.info(":::::Cron job to remind issue date has been expired and fine has been added");
		List<BIRD> listOfBird = birdRepository.findAllBIRD();
		listOfBird.stream().filter(bird -> {
			log.info(":::::bird {}", bird);
			return bird.isIssuedDateExpired() == true ? true : false;
		}).forEach(b -> {
			Student student = studentRepository.findStudentById(b.getIssuedBy());
			log.info(":::::student {}", student);
			emailService.sendMail(new ModelMap().addAttribute(Constants.TO, student.getEmail())
					.addAttribute(Constants.SUBJECT,
							"Reminder to return LMS's issued books, time has been expired and fine has been added.")
					.addAttribute(Constants.BODY, " Hi, " + student.getFirstName() + "Please return book with Id: "
							+ b.getBookId() + " as today the last for returning the book. Thanks"));
		});
	}

	@Override
	public void returnBookToLMS(BIRDRequest birdRequest) throws Exception {

		if (!ObjectUtils.isEmpty(birdRequest)) {
			// validateBirdRequestDetails(birdRequest);
			/*
			 * Update Book details
			 */
			Book book = bookRepository.findBookById(birdRequest.getBookId());
			book.setIssued(false);
			book.setAvailable(true);
			book.setAvailableOn(new Date());
			bookRepository.save(book);
			log.info(":::::book {}", book);
			/*
			 * Update bird details
			 */
			BIRD bird = birdRepository.findBIRDByBookId(book.getId());
			bird.setActive(false);
			bird.setReturnedOn(new Date());
			long lateDays = (new Date().getTime() - bird.getIssuedExpiryDate().getTime()) / ((1000 * 60 * 60 * 24));

			bird.setLateReturnDays(lateDays);
			bird.setFineAmount(lateDays * book.getFinePerDay());
			birdRepository.save(bird);
			log.info(":::::bird {}", bird);
			Student student = studentRepository.findStudentById(bird.getIssuedBy());
			List<BIRD> listOfBird = student.getLibraryDetails();
			listOfBird.removeIf(b -> b.getId().equals(bird.getId()));
			listOfBird.add(bird);
			studentRepository.save(student);
			log.info("::::::student {}", student);

			/*
			 * Sending notification to all students who looking the particular book
			 */

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", book.getId());
			jsonObject.put("bookName", book.getBookName());
			jsonObject.put("version", book.getVersion());
			lmsProducer.produce(topic, jsonObject.toJSONString());
		} else {
			throw new InvalidInput("Invalid input.");
		}
	}

	private boolean validateBirdRequestDetails(BIRDRequest birdRequest) throws Exception {
		if (!StringUtils.isNullOrEmpty(birdRequest.getBookId())) {
			Book book = bookRepository.findBookById(birdRequest.getBookId());
			if (ObjectUtils.isEmpty(book)) {
				throw new BookDoesNotExistException("Book does not exist. Please insert book detatils first.");
			}
		} else {
			throw new InvalidInput("Book id must not be null or empty");
		}
		if (!StringUtils.isNullOrEmpty(birdRequest.getIssuedBy())) {
			Student student = studentRepository.findStudentById(birdRequest.getIssuedBy());
			if (ObjectUtils.isEmpty(student)) {
				throw new StudentNotFoundException("Student does not exist. Please store student details first.");
			}
		} else {
			throw new InvalidInput("Student id must not be null or empty");
		}
		if (!StringUtils.isNullOrEmpty(birdRequest.getIssuerId())) {
			Librarian librarian = librarianRepository.findLibrarianById(birdRequest.getIssuerId());
			if (ObjectUtils.isEmpty(librarian)) {
				throw new LibrarianNotFound("Librarian does not exist.");
			}
		} else {
			throw new InvalidInput("Issuer id must not be null or empty");
		}
		return true;
	}

	@CachePut(value = "booksCache")
	@Override
	public List<Book> getAvailableBooks() {
		List<Book> listOfBooks = bookRepository.findBookByIsActiveAndIsAvailable(true, true);
		return listOfBooks;
	}

	@Override
	public List<String> getListOfIssuedBooks() {
		List<Book> books = bookRepository.findBookByIsIssued(true);
		return books.stream().map(Book::getId).collect(Collectors.toList());
	}

	@Override
	public void clearCache() {
		// TODO Auto-generated method stub
		
	}
}
