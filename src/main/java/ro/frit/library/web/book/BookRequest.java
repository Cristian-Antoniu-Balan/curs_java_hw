package ro.frit.library.web.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record BookRequest(
        @NotBlank String title,
        @NotBlank String author,
        @Min(1) int copies) {}
