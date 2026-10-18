package ro.frit.library.domain;

import java.time.LocalDateTime;

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
public class Loan {
    private String id;
    private String bookId;
    private String memberEmail;
    private LocalDateTime loanedAt;
}
