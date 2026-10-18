package ro.frit.library.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ro.frit.library.config.LibraryProperties.SeedBook;
import ro.frit.library.domain.Book;
import ro.frit.library.domain.Library;

@Configuration
public class LibraryConfig {

    @Bean 
    public Library library(LibraryProperties libraryProperties) {
        List<Book> books = libraryProperties.seedBooks().stream()
            .map(LibraryConfig::toBook)
            .toList();
            
        return new Library(books);
    }

    private static Book toBook(SeedBook seedBook) {
        return Book.builder()
                .id(seedBook.id())
                .title(seedBook.title())
                .author(seedBook.author())
                .copies(seedBook.copies())
                .loanedCopies(0)
                .build();
    }
}
