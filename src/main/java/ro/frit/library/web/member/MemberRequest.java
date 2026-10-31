package ro.frit.library.web.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record MemberRequest(
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotBlank @Email String email
) {}
