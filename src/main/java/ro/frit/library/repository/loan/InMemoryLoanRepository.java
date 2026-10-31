package ro.frit.library.repository.loan;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import ro.frit.library.domain.loan.Loan;

@Repository 
public class InMemoryLoanRepository implements LoanRepository{
    private final Map<String, Loan> loans = new LinkedHashMap<>();

    @Override 
    public List<Loan> findAll() {
        return List.copyOf(loans.values());
    }

    @Override 
    public Optional<Loan> findByLoanId(String id) {
        return Optional.ofNullable(loans.get(id));
    }

    @Override 
    public List<Loan> findAllByMemberId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Member id cannot be empty");
        }
        return loans.values().stream()
            .filter(loan -> loan.getMemberId().equals(id))
            .toList();
    }

    @Override 
    public Loan save(Loan loan) {
        loans.put(loan.getId(), loan);
        return loan;
    }
}
