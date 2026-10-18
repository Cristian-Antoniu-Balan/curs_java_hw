package ro.frit.library.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.RequiredArgsConstructor;
import ro.frit.library.domain.Library;

@RestController 
@RequestMapping("/books")
@RequiredArgsConstructor 
public class BookController {
    private final Library library;

    @GetMapping
    public List<BookResponse> getAllBooks() {
        return library.allBooks().stream()
            .map(BookResponse::from)
            .toList();
    }

    @GetMapping("/{id}")
    public BookResponse book(@PathVariable("id") String id) {
        return library.findBook(id)
            .map(BookResponse::from)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, String.format("No book with id %s exists", id)));
    }
}
