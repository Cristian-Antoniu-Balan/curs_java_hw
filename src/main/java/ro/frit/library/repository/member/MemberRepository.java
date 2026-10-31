package ro.frit.library.repository.member;

import java.util.List;
import java.util.Optional;

import ro.frit.library.domain.member.Member;

public interface MemberRepository {
    List<Member> findAll();
    Optional<Member> findById(String id);
    Optional<Member> findByEmail(String email);
    Member save(Member member);
    boolean deleteById(String id);
}
