package ro.frit.library.domain.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter 
@ToString
@NoArgsConstructor 
@AllArgsConstructor 
@Builder(toBuilder = true) 
@EqualsAndHashCode(of = "id")
public class Book {
    private String id;
    private String title;
    private String author;
    private int copies;
    private int loanedCopies;

    public boolean isAvailable() {
        return (copies - loanedCopies) > 0;
    }
}
