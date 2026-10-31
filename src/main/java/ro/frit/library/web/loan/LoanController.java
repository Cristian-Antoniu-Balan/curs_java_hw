package ro.frit.library.web.loan;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ro.frit.library.domain.book.Book;
import ro.frit.library.domain.loan.Loan;
import ro.frit.library.domain.member.Member;
import ro.frit.library.service.NotFoundException;
import ro.frit.library.service.book.BookService;
import ro.frit.library.service.loan.LoanService;
import ro.frit.library.service.member.MemberService;

@Tag(name = "Loans", description = "Loans catalog")
@RestController 
@RequestMapping("/loans")
@RequiredArgsConstructor 
public class LoanController {
    private final LoanService loanService;
    private final BookService bookService;
    private final MemberService memberService;

    @Operation(summary = "List loans")
    @GetMapping 
    public List<LoanResponse> getAllLoans(
        @Parameter(description = "If true, only active (not returned); if false, only returned; omit for all")
        @RequestParam(value = "active", required = false) Boolean active
    ) {
        return loanService.findAll(active).stream()
            .map(this::toResponse)
            .toList();
    }

    @Operation(summary = "Get loan by id")
    @ApiResponse(responseCode = "404", description = "Loan not found")
    @GetMapping("/{id}")
    public LoanResponse findById(@PathVariable("id") String id) {
        Loan loan = loanService.findById(id)
            .orElseThrow(() -> new NotFoundException("Loan not found for id: " + id));
        
        return toResponse(loan);
    }

    @Operation(summary = "Create a loan")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Loan created"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "404", description = "Book or member not found"),
        @ApiResponse(responseCode = "409", description = "Book not available for loan")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse loan(@Valid @RequestBody LoanRequest loanRequest) {
        return toResponse(loanService.loan(loanRequest));
    }

    @Operation(summary = "Return a loan")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Loan returned"),
        @ApiResponse(responseCode = "404", description = "Loan, book, or member not found"),
        @ApiResponse(responseCode = "409", description = "Loan already returned")
    })
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
