package ro.frit.library.web.loan;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record LoanRequest(@NotBlank String bookId, @NotBlank String memberId) {

    public LoanRequest {
        
        if (bookId == null || bookId.isBlank()) {
            throw new IllegalArgumentException("Book id cannot be blank");
        }

        if (memberId == null || memberId.isBlank()) {
            throw new IllegalArgumentException("Member id cannot be blank");
        }
    }
}
