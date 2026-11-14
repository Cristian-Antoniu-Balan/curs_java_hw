package ro.frit.library.web.member;

import lombok.Builder;
import ro.frit.library.domain.member.Member;

@Builder(toBuilder = true)
public record MemberResponse(String id, String firstName, String lastName, String email, int activeLoans) {

    public static MemberResponse from(Member member, int activeLoans) {
        return new MemberResponse(
            member.getId(),
            member.getFirstName(),
            member.getLastName(),
            member.getEmail(),
            activeLoans
        );
    }
}
