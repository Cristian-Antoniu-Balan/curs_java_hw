package ro.frit.library.service.book;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ro.frit.library.domain.book.Book;
import ro.frit.library.repository.book.BookRepository;
import ro.frit.library.web.book.BookRequest;
import ro.frit.library.web.book.BookUpdateRequest;

@Service 
@RequiredArgsConstructor 
public class BookService {
    private final BookRepository bookRepository;

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Optional<Book> findById(String id) {
        return bookRepository.findById(id);
    }

    public Book create(BookRequest bookRequest) {
        Book book = Book.builder()
            .id(UUID.randomUUID().toString())
            .title(bookRequest.title())
            .author(bookRequest.author())
            .copies(bookRequest.copies())
            .loanedCopies(0)
            .build();

        return bookRepository.save(book);
    }

    public Optional<Book> replace(String id, BookRequest bookRequest) {
        return bookRepository.findById(id)
            .map(existing -> {
                if (bookRequest.copies() < existing.getLoanedCopies()) {
                    throw new IllegalArgumentException(
                        "Copies cannot be less than loaned copies (" + existing.getLoanedCopies() + ")"
                    );
                }
                Book updated = existing.toBuilder()
                    .title(bookRequest.title())
                    .author(bookRequest.author())
                    .copies(bookRequest.copies())
                    .build();
                
                return bookRepository.save(updated); 
            });
    }

    public Optional<Book> update(String id, BookUpdateRequest bookUpdateRequest) {
        return bookRepository.findById(id)
            .map(existing -> {
                var builder = existing.toBuilder();
                final String newTitle = bookUpdateRequest.title();
                final String newAuthor = bookUpdateRequest.author();
                if (newTitle != null) {
                    if (newTitle.isBlank()) {
                        throw new IllegalArgumentException("Title cannot be blank");
                    };
                    builder.title(bookUpdateRequest.title());
                };
                if (newAuthor != null) {
                    if (newAuthor.isBlank()) {
                        throw new IllegalArgumentException("Author cannot be blank");
                    };
                    builder.author(bookUpdateRequest.author());
                };
                if (bookUpdateRequest.copies() != null) {
                    if (bookUpdateRequest.copies() < existing.getLoanedCopies()) {
                        throw new IllegalArgumentException(
                            "Copies cannot be less than loaned copies (" + existing.getLoanedCopies() + ")"
                        );
                    };
                    builder.copies(bookUpdateRequest.copies());
                };
                
                return bookRepository.save(builder.build());
            });
    }

    public boolean delete(String id) {
        Optional<Book> existing = bookRepository.findById(id);

        if (existing.isEmpty()) {
            return false;
        };

        if (existing.get().getLoanedCopies() > 0) {
            throw new IllegalStateException("Cannot delete book with active loans: " + existing.get().getLoanedCopies());
        };

        return bookRepository.deleteById(id);
    }
}
