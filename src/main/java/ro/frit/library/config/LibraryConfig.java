package ro.frit.library.config;

import java.util.UUID;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ro.frit.library.config.LibraryProperties.SeedBook;
import ro.frit.library.config.LibraryProperties.SeedMember;
import ro.frit.library.domain.book.Book;
import ro.frit.library.domain.member.Member;
import ro.frit.library.repository.book.BookRepository;
import ro.frit.library.repository.member.MemberRepository;

@Configuration
public class LibraryConfig {

    @Bean
    ApplicationRunner seedLibrary(
            LibraryProperties libraryProperties,
            BookRepository bookRepository,
            MemberRepository memberRepository
    ) {
        return args -> {
            libraryProperties.seedBooks().stream()
                    .map(LibraryConfig::toBook)
                    .forEach(bookRepository::save);

            libraryProperties.seedMembers().stream()
                    .map(LibraryConfig::toMember)
                    .forEach(memberRepository::save);
        };
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

    private static Member toMember(SeedMember seedMember) {
        return Member.builder()
                .id(UUID.randomUUID().toString())
                .firstName(seedMember.firstName())
                .lastName(seedMember.lastName())
                .email(seedMember.email())
                .build();
    }
}
