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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.RequiredArgsConstructor;
import ro.frit.library.service.book.BookService;

@Tag(name = "Books", description = "Book catalog")
@RestController 
@RequestMapping("/books")
@RequiredArgsConstructor 
public class BookController {
    private final BookService bookService;

    @Operation(summary = "List books")
    @GetMapping
    public List<BookResponse> getAllBooks(
        @Parameter(description = "If true, only books with free copies; if false, only fully loaned; omit for all")
        @RequestParam(value = "available", required = false) Boolean available
    ) {
        return bookService.findAll(available).stream()
            .map(BookResponse::from)
            .toList();
    }

    @Operation(summary = "Get book by id")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @GetMapping("/{id}")
    public BookResponse book(@PathVariable("id") String id) {
        return bookService.findById(id)
            .map(BookResponse::from)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Book not found for id: " + id));    
    }

    @Operation(summary = "Create book")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created"),
        @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse postBook(@Valid @RequestBody BookRequest bookRequest) {
        return BookResponse.from(bookService.create(bookRequest));
    }

    @Operation(summary = "Replace book")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Replaced"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "404", description = "Book not found"),
        @ApiResponse(responseCode = "409", description = "Copies less than currently loaned copies")
    })
    @PutMapping("/{id}")
    public BookResponse putBook(
            @PathVariable("id") String id,
            @Valid @RequestBody BookRequest bookRequest) {
        return bookService.replace(id, bookRequest)
            .map(BookResponse::from)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Book not found for id: " + id));
    }

    @Operation(summary = "Partially update book", description = "Null fields are left unchanged")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Updated"),
        @ApiResponse(responseCode = "400", description = "Validation failed or blank title/author"),
        @ApiResponse(responseCode = "404", description = "Book not found"),
        @ApiResponse(responseCode = "409", description = "Copies less than currently loaned copies")
    })
    @PatchMapping("/{id}")
    public BookResponse patchBook(
            @PathVariable("id") String id,
            @Valid @RequestBody BookUpdateRequest bookRequest) {
        return bookService.update(id, bookRequest)
                .map(BookResponse::from)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Book not found for id: " + id));
    }

    @Operation(summary = "Delete book")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Deleted"),
        @ApiResponse(responseCode = "404", description = "Book not found"),
        @ApiResponse(responseCode = "409", description = "Book has active loans")
    })
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable("id") String id) {
        if (!bookService.delete(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Book not found for id: " + id);
        };
    }
}
