package ro.frit.library.web.member;

import lombok.Builder;
import ro.frit.library.domain.member.Member;

@Builder(toBuilder = true)
public record MemberResponse(String id, String firstName, String lastName, String email) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(
            member.getId(),
            member.getFirstName(),
            member.getLastName(),
            member.getEmail()
        );
    }
}
