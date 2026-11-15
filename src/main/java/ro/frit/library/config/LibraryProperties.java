package ro.frit.library.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "library")
public record LibraryProperties(
        Cors cors,
        List<SeedBook> seedBooks,
        List<SeedMember> seedMembers
) {

    public LibraryProperties {
        cors = cors == null ? new Cors("") : cors;
        seedBooks = seedBooks == null ? List.of() : List.copyOf(seedBooks);
        seedMembers = seedMembers == null ? List.of() : List.copyOf(seedMembers);
    }

    public record Cors(String allowedOrigins) {
        
        public Cors {
            allowedOrigins = allowedOrigins == null ? "" : allowedOrigins;
        }

        public List<String> allowedOriginList() {
            if (allowedOrigins.isBlank()) {
                return List.of();
            }
            return Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList();
        }
    }

    public record SeedBook(String title, String author, int copies) {}

    public record SeedMember(String firstName, String lastName, String email) {}
}
