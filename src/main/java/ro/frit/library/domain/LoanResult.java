package ro.frit.library.domain;

public sealed interface LoanResult permits LoanResult.Loaned, LoanResult.NotAvailable, LoanResult.UnknownBook {

    record Loaned(Loan loan) implements LoanResult {}

    record NotAvailable(String bookId) implements LoanResult {}

    record UnknownBook(String bookId) implements LoanResult {}

    default String describe() {
        return switch (this) {
            case Loaned(Loan loan) -> String.format("Book id %s loand to %s", loan.getBookId(), loan.getMemberEmail());
            case NotAvailable(String bookId) -> String.format("Book id %s is not available", bookId);
            case UnknownBook(String bookId) -> String.format("Book id %s does not exist", bookId);
        };
    }
}
