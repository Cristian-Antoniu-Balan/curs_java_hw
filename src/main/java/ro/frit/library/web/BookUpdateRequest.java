package ro.frit.library.web;

import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder(toBuilder = true)
public record BookUpdateRequest(
    String title,
    String author,
    @Min(1) Integer copies
) {}
