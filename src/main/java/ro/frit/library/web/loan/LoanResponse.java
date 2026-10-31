package ro.frit.library.web.loan;

import java.time.LocalDateTime;

import lombok.Builder;
import ro.frit.library.domain.book.Book;
import ro.frit.library.domain.loan.Loan;
import ro.frit.library.domain.member.Member;
import ro.frit.library.web.book.BookResponse;
import ro.frit.library.web.member.MemberResponse;

@Builder(toBuilder = true)
public record LoanResponse(
    String id,
    BookResponse book,
    MemberResponse member,
    LocalDateTime loanedAt,
    LocalDateTime returnedAt,
    boolean isActive) {

        public static LoanResponse from(Loan loan, Book book, Member member) {
            return new LoanResponse(
                loan.getId(),
                BookResponse.from(book),
                MemberResponse.from(member),
                loan.getLoanedAt(),
                loan.getReturnedAt(),
                loan.isActive());
        }
}
