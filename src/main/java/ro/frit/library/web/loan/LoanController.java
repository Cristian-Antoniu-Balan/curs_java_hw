package ro.frit.library.web.loan;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ro.frit.library.domain.book.Book;
import ro.frit.library.domain.loan.Loan;
import ro.frit.library.domain.member.Member;
import ro.frit.library.service.NotFoundException;
import ro.frit.library.service.book.BookService;
import ro.frit.library.service.loan.LoanService;
import ro.frit.library.service.member.MemberService;

@RestController 
@RequestMapping("/loans")
@RequiredArgsConstructor 
public class LoanController {
    private final LoanService loanService;
    private final BookService bookService;
    private final MemberService memberService;

    @GetMapping 
    public List<LoanResponse> getAllLoans() {
        return loanService.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    @GetMapping("/{id}")
    public LoanResponse findById(@PathVariable("id") String id) {
        Loan loan = loanService.findById(id)
            .orElseThrow(() -> new NotFoundException("Loan not found for id: " + id));
        
        return toResponse(loan);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse loan(@Valid @RequestBody LoanRequest loanRequest) {
        return toResponse(loanService.loan(loanRequest));
    }

    @PostMapping("/{id}/return")
    public LoanResponse returnLoan(@PathVariable("id") String id) {
        return toResponse(loanService.returnLoan(id));
    }

    private LoanResponse toResponse(Loan loan) {
        Book book = bookService.findById(loan.getBookId())
            .orElseThrow(() -> new NotFoundException("Invalid book"));
        
        Member member = memberService.findById(loan.getMemberId())
            .orElseThrow(() -> new NotFoundException("Invalid member"));
        
        return LoanResponse.from(loan, book, member);
    }
}
