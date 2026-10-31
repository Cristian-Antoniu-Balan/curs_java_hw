package ro.frit.library.repository.book;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import ro.frit.library.domain.book.Book;

@Repository 
public class InMemoryBookRepository implements BookRepository {
    private final Map<String, Book> books = new LinkedHashMap<>();

    @Override 
    public List<Book> findAll() {
        return List.copyOf(books.values());
    }

    @Override 
    public Optional<Book> findById(String id) {
        return Optional.ofNullable(books.get(id));
    }

    @Override 
    public Book save(Book book) {
        books.put(book.getId(), book);
        return book;
    }

    @Override 
    public boolean deleteById(String id) {
        return books.remove(id) != null;
    }
}
