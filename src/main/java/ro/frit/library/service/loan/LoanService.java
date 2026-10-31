package ro.frit.library.service.loan;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ro.frit.library.domain.book.Book;
import ro.frit.library.domain.loan.Loan;
import ro.frit.library.repository.book.BookRepository;
import ro.frit.library.repository.loan.LoanRepository;
import ro.frit.library.repository.member.MemberRepository;
import ro.frit.library.service.ConflictException;
import ro.frit.library.service.NotFoundException;
import ro.frit.library.web.loan.LoanRequest;

@Service 
@RequiredArgsConstructor 
public class LoanService {
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public Loan loan(LoanRequest loanRequest) {
        memberRepository.findById(loanRequest.memberId())
            .orElseThrow(() -> new NotFoundException("Member not found for id: " + loanRequest.memberId()));

        Book book = bookRepository.findById(loanRequest.bookId())
            .orElseThrow(() -> new NotFoundException("Book not found for id: " + loanRequest.bookId()));

        if (!book.isAvailable()) {
            throw new ConflictException("Book is not available for loan");
        }

        Loan loan = Loan.builder()
            .id(UUID.randomUUID().toString())
            .bookId(loanRequest.bookId())
            .memberId(loanRequest.memberId())
            .loanedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))
            .returnedAt(null)
            .build();

        Book updatedBook = book.toBuilder()
            .loanedCopies(book.getLoanedCopies() + 1)
            .build();
        bookRepository.save(updatedBook);

        return loanRepository.save(loan);
    }

    public Loan returnLoan(String id) {
        Loan loan = loanRepository.findByLoanId(id)
            .orElseThrow(() -> new NotFoundException("Loan not found for id: " + id));

        if (!loan.isActive()) {
            throw new ConflictException("Loan already returned");
        }

        memberRepository.findById(loan.getMemberId())
            .orElseThrow(() -> new NotFoundException("Member for loan not found"));

        Book book = bookRepository.findById(loan.getBookId())
            .orElseThrow(() -> new NotFoundException("Book for loan not found"));

        if (book.getLoanedCopies() == 0) {
            throw new NotFoundException("No loaned copies of the book exist");
        }

        Loan returnedLoan = loan.toBuilder()
            .returnedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))
            .build();
        
        Book returnedBook = book.toBuilder()
            .loanedCopies(book.getLoanedCopies() - 1)
            .build();
        bookRepository.save(returnedBook);

        return loanRepository.save(returnedLoan);
    }

    public List<Loan> findAll() {
        return loanRepository.findAll();
    }

    public Optional<Loan> findById(String id) {
        return loanRepository.findByLoanId(id);
    }

    public List<Loan> findAllByMemberId(String id) {
        return loanRepository.findAllByMemberId(id);
    }
}
