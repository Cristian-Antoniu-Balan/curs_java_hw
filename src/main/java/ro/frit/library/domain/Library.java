package ro.frit.library.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Library {
    private Map<String, Book> books = new LinkedHashMap<>();
    private final List<Loan> loans = new ArrayList<>();

    public Library(List<Book> books) {
        books.forEach(book -> {
            if (this.books.putIfAbsent(book.getId(), book) != null) {
                throw new IllegalArgumentException("Duplicate book id " + book.getId());
            };
        });
    }

    public List<Book> allBooks() {
        return books.values().stream().toList();
    }

    public Optional<Book> findBook(String bookId) {
        return Optional.ofNullable(books.get(bookId));
    }

    public List<Book> availableBooks() {
        // result can be null/empty ?
        return books.values().stream()
                .filter(Book::isAvailable)
                .toList();
    }

    public Map<String, Long> loansPerMember() {
        return loans.stream()
                .collect(Collectors.groupingBy(Loan::getMemberEmail, Collectors.counting()));
    }
}
