package ro.frit.library.service.member;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ro.frit.library.domain.loan.Loan;
import ro.frit.library.domain.member.Member;
import ro.frit.library.repository.loan.LoanRepository;
import ro.frit.library.repository.member.MemberRepository;
import ro.frit.library.service.ConflictException;
import ro.frit.library.service.EmailAlreadyInUseException;
import ro.frit.library.web.member.MemberRequest;
import ro.frit.library.web.member.MemberResponse;

@Service 
@RequiredArgsConstructor 
public class MemberService {
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    // DB should compute this - join tables
    public List<MemberResponse> findAllWithLoanCount() {
        Map<String, Long> loansPerMember = activeLoanCountByMemberId();
        return memberRepository.findAll().stream()
            .map(m -> MemberResponse.from(m, loansPerMember.getOrDefault(m.getId(), 0L).intValue()))
            .toList();
    }

    public Optional<Member> findById(String id) {
        return memberRepository.findById(id);
    }

    // DB should compute this - join tables
    public Optional<MemberResponse> findOneByIdWithLoanCount(String memberId) {
        return memberRepository.findById(memberId)
            .map(m -> MemberResponse.from(m, activeLoanCountForMemberId(memberId)));
    }

    public MemberResponse createMember(MemberRequest memberRequest) {
        String newEmail = normalize(memberRequest.email());
        if (memberRepository.findByEmail(newEmail).isPresent()) {
            throw new EmailAlreadyInUseException(newEmail);
        }

        Member member = Member.builder()
            .id(UUID.randomUUID().toString())
            .firstName(memberRequest.firstName())
            .lastName(memberRequest.lastName())
            .email(newEmail)
            .build();

        Member saved = memberRepository.save(member);
        return MemberResponse.from(saved, 0);
    }

    public Optional<MemberResponse> replace(String id, MemberRequest memberRequest) {
        return memberRepository.findById(id)
            .map(existing -> {
                String normalizedRequestEmail = normalize(memberRequest.email());
                Optional<Member> existingIdByEmail = memberRepository.findByEmail(normalizedRequestEmail);
                if (existingIdByEmail.isPresent() && !existingIdByEmail.get().getId().equals(id)) {
                    throw new EmailAlreadyInUseException(normalizedRequestEmail);
                }
                Member updated = existing.toBuilder()
                    .firstName(memberRequest.firstName())
                    .lastName(memberRequest.lastName())
                    .email(normalizedRequestEmail)
                    .build();
                
                Member saved = memberRepository.save(updated);
                return MemberResponse.from(saved, activeLoanCountForMemberId(saved.getId()));
            });
    }

    public boolean delete(String id) {
        Optional<Member> existing = memberRepository.findById(id);

        if (existing.isEmpty()) {
            return false;
        }

        boolean hasActiveLoans = loanRepository.findAllByMemberId(id).stream()
            .filter(Loan::isActive)
            .findAny()
            .isPresent();
        if (hasActiveLoans) {
            throw new ConflictException("Cannot delete member with active loans");
        }

        return memberRepository.deleteById(id);
    }

    public Map<String, MemberResponse> findAllAndMapById() {
        return findAllWithLoanCount().stream()
            .collect(Collectors.toMap(MemberResponse::id, m -> m));
    }

    private static String normalize(String email) {
        return email.trim().toLowerCase();
    }

    private Map<String, Long> activeLoanCountByMemberId() {
        return loanRepository.findAll().stream()
            .filter(Loan::isActive)
            .collect(Collectors.groupingBy(Loan::getMemberId, Collectors.counting()));
    }

    private int activeLoanCountForMemberId(String memberId) {
        return (int) loanRepository.findAllByMemberId(memberId).stream()
            .filter(Loan::isActive)
            .count();
    }
}
