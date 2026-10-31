package ro.frit.library.repository.loan;

import java.util.List;
import java.util.Optional;

import ro.frit.library.domain.loan.Loan;

public interface LoanRepository {
    List<Loan> findAll();
    Optional<Loan> findByLoanId(String id);
    List<Loan> findAllByMemberId(String id);
    Loan save(Loan loan);
}
