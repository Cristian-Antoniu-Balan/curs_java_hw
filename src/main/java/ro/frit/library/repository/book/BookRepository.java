package ro.frit.library.repository.book;

import java.util.List;
import java.util.Optional;

import ro.frit.library.domain.book.Book;

public interface BookRepository {
    List<Book> findAll();
    Optional<Book> findById(String id);
    Book save(Book book);
    boolean deleteById(String id);
}
