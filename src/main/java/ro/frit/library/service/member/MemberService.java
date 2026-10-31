package ro.frit.library.service.member;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ro.frit.library.domain.loan.Loan;
import ro.frit.library.domain.member.Member;
import ro.frit.library.repository.loan.LoanRepository;
import ro.frit.library.repository.member.MemberRepository;
import ro.frit.library.service.ConflictException;
import ro.frit.library.service.EmailAlreadyInUseException;
import ro.frit.library.web.member.MemberRequest;

@Service 
@RequiredArgsConstructor 
public class MemberService {
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    public Optional<Member> findById(String id) {
        return memberRepository.findById(id);
    }

    public Member createMember(MemberRequest memberRequest) {
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

        return memberRepository.save(member);
    }

    public Optional<Member> replace(String id, MemberRequest memberRequest) {
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
                
                return memberRepository.save(updated);
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

    private static String normalize(String email) {
        return email.trim().toLowerCase();
    }
}
