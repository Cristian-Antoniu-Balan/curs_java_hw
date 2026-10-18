package ro.frit.library.web;

import lombok.Builder;
import ro.frit.library.domain.Book;

@Builder(toBuilder = true)
public record BookResponse(String id, String title, String author, int copies, boolean isAvailable) {
    
    public static BookResponse from(Book book) {
        return new BookResponse(
            book.getId(),
            book.getTitle(),
            book.getAuthor(),
            book.getCopies(),
            book.isAvailable()
        );
    }
}
