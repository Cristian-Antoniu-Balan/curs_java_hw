package ro.frit.library.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "library")
public record LibraryProperties(
        List<SeedBook> seedBooks,
        List<SeedMember> seedMembers
) {

    public LibraryProperties {
        seedBooks = seedBooks == null ? List.of() : List.copyOf(seedBooks);
        seedMembers = seedMembers == null ? List.of() : List.copyOf(seedMembers);
    }

    public record SeedBook(String title, String author, int copies) {}

    public record SeedMember(String firstName, String lastName, String email) {}
}
