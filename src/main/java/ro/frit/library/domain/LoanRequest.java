package ro.frit.library.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record LoanRequest(@NotBlank String bookId, @NotBlank String memberEmail) {

    public LoanRequest {
        
        if (bookId == null || bookId.isBlank()) {
            throw new IllegalArgumentException("Book id cannot be blank");
        }

        if (memberEmail == null || memberEmail.isBlank()) {
            throw new IllegalArgumentException("Member email cannot be blank");
        }
    }
}
