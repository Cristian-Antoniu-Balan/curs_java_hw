package ro.frit.library.config;

import java.util.UUID;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ro.frit.library.config.LibraryProperties.SeedBook;
import ro.frit.library.domain.book.Book;
import ro.frit.library.repository.book.BookRepository;

@Configuration
public class LibraryConfig {

    @Bean 
    ApplicationRunner seedBooks(LibraryProperties libraryProperties, BookRepository bookRepository) {
        return args -> libraryProperties.seedBooks().stream()
            .map(LibraryConfig::toBook)
            .forEach(bookRepository::save);
    }

    private static Book toBook(SeedBook seedBook) {
        return Book.builder()
                .id(UUID.randomUUID().toString())
                .title(seedBook.title())
                .author(seedBook.author())
                .copies(seedBook.copies())
                .loanedCopies(0)
                .build();
    }
}
