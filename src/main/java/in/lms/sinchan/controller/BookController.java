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
import in.lms.sinchan.entity.Book;
import in.lms.sinchan.model.request.BookCreateRequest;
import in.lms.sinchan.model.request.BookUpdateRequest;
import in.lms.sinchan.model.response.BookCreateResponse;
import in.lms.sinchan.service.BookService;

@RestController("bookController")
@RequestMapping(path = "/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping(value = "/create")
    public ResponseEntity<?> persistBookDetailsInDB(
                    @RequestBody BookCreateRequest bookCreateRequest) {
        BookCreateResponse response = bookService.persistBookInDB(bookCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ModelMap().addAttribute("msg", "Succesfully created")
                                        .addAttribute("response", response));
    }

    @GetMapping(value = "/get/{id}")
    public ResponseEntity<?> getBooksByBookID(
                    @PathVariable(value = "id", required = true) String id) throws Exception {
        Book book = bookService.getBookDetails(id);
        return ResponseEntity.status(HttpStatus.OK)
                        .body(new ModelMap().addAttribute("response", book));
    }

    @GetMapping(value = "/getAll")
    public ResponseEntity<?> getAllBooksDetails() {
        List<Book> listOfBooks = bookService.getAllBooks();
        return ResponseEntity.status(HttpStatus.OK)
                        .body(new ModelMap().addAttribute("response", listOfBooks));
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteBookDetails(
                    @PathVariable(value = "id", required = true) String id) throws Exception {
        bookService.deleteBookDetails(id);
        return ResponseEntity.status(HttpStatus.OK)
                        .body(new ModelMap().addAttribute("response", "Successfully deleted"));
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateBookDetails(@RequestBody BookUpdateRequest bookUpdateRequest,
                    @PathVariable(value = "id", required = true) String id) throws Exception {
        bookService.updateBookDetails(bookUpdateRequest, id);
        return ResponseEntity.status(HttpStatus.OK)
                        .body(new ModelMap().addAttribute("msg", "Successfully updated"));
    }
}
