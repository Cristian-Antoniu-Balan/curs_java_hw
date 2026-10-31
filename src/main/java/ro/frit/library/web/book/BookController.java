package ro.frit.library.web.book;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.RequiredArgsConstructor;
import ro.frit.library.service.book.BookService;

@RestController 
@RequestMapping("/books")
@RequiredArgsConstructor 
public class BookController {
    private final BookService bookService;

    @GetMapping
    public List<BookResponse> getAllBooks() {
        return bookService.findAll().stream()
            .map(BookResponse::from)
            .toList();
    }

    @GetMapping("/{id}")
    public BookResponse book(@PathVariable("id") String id) {
        return bookService.findById(id)
            .map(BookResponse::from)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Book not found for id: " + id));    
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse postBook(@Valid @RequestBody BookRequest bookRequest) {
        return BookResponse.from(bookService.create(bookRequest));
    }

    @PutMapping("/{id}")
    public BookResponse putBook(
            @PathVariable("id") String id,
            @Valid @RequestBody BookRequest bookRequest) {
        return bookService.replace(id, bookRequest)
            .map(BookResponse::from)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Book not found for id: " + id));
    }

    @PatchMapping("/{id}")
    public BookResponse patchBook(
            @PathVariable("id") String id,
            @Valid @RequestBody BookUpdateRequest bookRequest) {
        return bookService.update(id, bookRequest)
                .map(BookResponse::from)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Book not found for id: " + id));
    }

    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable("id") String id) {
        if (!bookService.delete(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Book not found for id: " + id);
        };
    }
}
